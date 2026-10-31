package com.passerelle.controller;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.passerelle.annotation.RestApi;
import com.passerelle.binding.ArgumentBinder;
import com.passerelle.binding.SessionBinder;
import com.passerelle.mapping.Mapping;
import com.passerelle.mapping.ModelView;
import com.passerelle.session.Session;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
public class FrontController extends HttpServlet {

    private HashMap<String, Mapping> urlMappings;
    private Map<Class<?>, Object> controllerInstances;
    private String viewPrefix;
    private String viewSuffix;
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        super.init();
        urlMappings = (HashMap<String, Mapping>) getServletContext().getAttribute("urlMappings");
        controllerInstances = (Map<Class<?>, Object>) getServletContext().getAttribute("controllerInstances");
        viewPrefix = getServletContext().getInitParameter("view.prefix");
        viewSuffix = getServletContext().getInitParameter("view.suffix");

        if (urlMappings == null) throw new ServletException("urlMappings non initialise");
        if (controllerInstances == null) throw new ServletException("controllerInstances non initialise");
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String page = request.getRequestURI().substring(request.getContextPath().length());
        String key = request.getMethod().toUpperCase() + ":" + page;

        if (urlMappings.containsKey(key)) {
            executeMapping(urlMappings.get(key), request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "URL non mappee : " + key);
        }
    }

    private void executeMapping(Mapping mapping, HttpServletRequest request,
                                HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Object instance = controllerInstances.get(mapping.getClazz());
            if (instance == null) {
                throw new ServletException("Aucune instance trouvee pour : " + mapping.getClazz());
            }

            Method method = mapping.getMethod();
            Object[] args = ArgumentBinder.bind(method, request);

            if (!mapping.isPublic()) {
                Session session = null;
                for (Object arg : args) {
                    if (arg instanceof Session) {
                        session = (Session) arg;
                        break;
                    }
                }

                if (session == null || session.get("role") == null) {
                    response.sendRedirect(request.getContextPath() + "/login/form");
                    return;
                }

                String requiredRole = mapping.getRequiredRole();
                if (requiredRole != null) {
                    String userRole = (String) session.get("role");
                    if (!requiredRole.equals(userRole)) {
                        response.setContentType("text/html;charset=UTF-8");
                        response.getWriter().println("<h1>Acces refuse</h1>");
                        response.getWriter().println("<p>Role requis : " + requiredRole + "</p>");
                        return;
                    }
                }
            }

            Object result = method.invoke(instance, args);

            for (Object arg : args) {
                if (arg instanceof Session) {
                    SessionBinder.update((Session) arg, request);
                    break;
                }
            }

            boolean isRestAPI = mapping.getClazz().isAnnotationPresent(RestApi.class);
            boolean isRestMethod = method.isAnnotationPresent(RestApi.class);

            if (isRestAPI || isRestMethod) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print(gson.toJson(result));
            } else if (result instanceof ModelView) {
                processModelView((ModelView) result, request, response);
            } else {
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().println("Methode executee : " + mapping);
            }

        } catch (Exception e) {
            throw new ServletException("Erreur invocation : " + e.getMessage(), e);
        }
    }

    private void processModelView(ModelView mv, HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }

        RequestDispatcher dispatch = request.getRequestDispatcher(viewPrefix + mv.getView() + viewSuffix);
        dispatch.forward(request, response);
    }
}