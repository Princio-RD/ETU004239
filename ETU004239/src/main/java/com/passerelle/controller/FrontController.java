package com.passerelle.controller;

import com.passerelle.annotation.RestApi;
import com.passerelle.mapping.Mapping;
import com.passerelle.mapping.ModelView;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import org.apache.commons.beanutils.ConvertUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class FrontController extends HttpServlet {

    private HashMap<String, Mapping> urlMappings;
    private String viewPrefix;
    private String viewSuffix;
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        super.init();

        this.urlMappings = (HashMap<String, Mapping>)
                getServletContext().getAttribute("urlMappings");
        this.viewPrefix = getServletContext().getInitParameter("view.prefix");
        this.viewSuffix = getServletContext().getInitParameter("view.suffix");

        if (urlMappings == null) {
            throw new ServletException("urlMappings non initialise - FrameListener manquant ?");
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String page = request.getRequestURI()
                .substring(request.getContextPath().length());

        String key = request.getMethod().toUpperCase() + ":" + page;

        if (urlMappings.containsKey(key)) {
            executeMapping(urlMappings.get(key), request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "URL non mappee : " + key);
        }
    }

    private void executeMapping(Mapping mapping, HttpServletRequest request,
                                HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Class<?> clazz = Class.forName(mapping.getClassName());
            Object instance = clazz.getDeclaredConstructor().newInstance();

            Method method = null;
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(mapping.getMethodName())) {
                    method = m;
                    break;
                }
            }

            if (method == null) {
                throw new ServletException("Methode introuvable : " + mapping.getMethodName());
            }

            Parameter[] params = method.getParameters();
            Object[] args = new Object[params.length];

            for (int i = 0; i < params.length; i++) {
                String value = request.getParameter(params[i].getName());
                args[i] = ConvertUtils.convert(value, params[i].getType());
            }

            Object result = method.invoke(instance, args);
	
			// verification JSON
            boolean isRest = clazz.isAnnotationPresent(RestApi.class)|| method.isAnnotationPresent(RestApi.class);

            if (isRest) {
                processRestApi(result, response);
            } else if (result instanceof ModelView) {
                processModelView((ModelView) result, request, response);
            } else {
                response.setContentType("text/plain;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.println("Methode executee : " + mapping);
            }

        } catch (Exception e) {
            throw new ServletException("Erreur invocation : " + e.getMessage(), e);
        }
    }

    private void processRestApi(Object result, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(result));
    }

    private void processModelView(ModelView mv, HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }

        String view = viewPrefix + mv.getView() + viewSuffix;
        RequestDispatcher dispatch = request.getRequestDispatcher(view);
        dispatch.forward(request, response);
    }
}