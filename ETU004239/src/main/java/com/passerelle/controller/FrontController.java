package com.passerelle.controller;

import com.passerelle.annotation.Public;
import com.passerelle.annotation.Role;
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

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
            Class<?> clazz = mapping.getClazz();
            Method method = mapping.getMethod();

            Object instance = controllerInstances.get(clazz);
            if (instance == null) throw new ServletException("Instance non trouvee");

            // 1. Binder les arguments
            Object[] args = ArgumentBinder.bind(method, request);

            boolean isPublic = method.isAnnotationPresent(Public.class) 
                            || clazz.isAnnotationPresent(Public.class);

            if (!isPublic) {

                Session session = null;
                for (Object arg : args) {
                    if (arg instanceof Session) {
                        session = (Session) arg;
                        break;
                    }
                }

                // A. Verifier si connecte
                if (session == null || session.get("user") == null) {
                    response.sendRedirect(request.getContextPath() + "/login/form");
                    return;
                }

                // B. Verifier le role si l'annotation @Role est presente
                Role roleAnnotation = method.getAnnotation(Role.class);
                if (roleAnnotation == null) {
                    roleAnnotation = clazz.getAnnotation(Role.class);
                }

                if (roleAnnotation != null) {
                    String requiredRole = roleAnnotation.value();
                    String userRole = (String) session.get("role");

                    if (!requiredRole.equals(userRole)) {
                        response.setContentType("text/html;charset=UTF-8");
                        response.getWriter().println("<h1>Acces refuse : role " + requiredRole + " requis.</h1>");
                        return;
                    }
                }
            }

            // 3. Invoquer la methode
            Object result = method.invoke(instance, args);

            // 4. Synchroniser la session
            for (Object arg : args) {
                if (arg instanceof Session) {
                    SessionBinder.update((Session) arg, request);
                    break;
                }
            }

            // 5. Traitement du resultat
            boolean isRestAPI = clazz.isAnnotationPresent(RestApi.class);
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

        RequestDispatcher dispatch =
                request.getRequestDispatcher(viewPrefix + mv.getView() + viewSuffix);
        dispatch.forward(request, response);
    }
}