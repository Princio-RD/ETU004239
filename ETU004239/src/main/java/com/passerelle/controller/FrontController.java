package com.passerelle.controller;

import com.passerelle.annotation.RestApi;
import com.passerelle.binding.ArgumentBinder;
import com.passerelle.mapping.Mapping;
import com.passerelle.mapping.ModelView;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Method;
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

        urlMappings = (HashMap<String, Mapping>) getServletContext().getAttribute("urlMappings");
        viewPrefix = getServletContext().getInitParameter("view.prefix");
        viewSuffix = getServletContext().getInitParameter("view.suffix");

        if (urlMappings == null) {
            throw new ServletException("urlMappings non initialise - FrameListener manquant ?");
        }
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

            Object[] args = ArgumentBinder.bind(method, request);
            Object result = method.invoke(instance, args);

            boolean isRestAPI = clazz.isAnnotationPresent(RestApi.class);
            boolean isRestmethode = method.isAnnotationPresent(RestApi.class);

            if (isRestAPI || isRestmethode) {
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