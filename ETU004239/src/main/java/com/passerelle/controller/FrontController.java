package com.passerelle.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.Url;
import com.passerelle.mapping.Mapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FrontController extends HttpServlet {

    private List<Class<?>> controllerClasses = new ArrayList<>();
    private HashMap<String, Mapping> urlMappings = new HashMap<>();

    @Override
    public void init() throws ServletException {
        super.init();

        String packageScan = getServletContext().getInitParameter("packageToScan");

        if (packageScan == null || packageScan.isEmpty()) {
            throw new ServletException("erreur critique : parametre packageToScan non trouve");
        }

        try {
            this.controllerClasses = getClassPackage(packageScan);
            this.urlMappings = buildMappings(controllerClasses);
        } catch (Exception e) {
            throw new ServletException("Erreur lors du scan : " + e.getMessage(), e);
        }
    }

    private ArrayList<Class<?>> getClassPackage(String packageName) {
        org.reflections.Reflections reflect = new org.reflections.Reflections(packageName);
        return new ArrayList<>(reflect.getTypesAnnotatedWith(Controller.class));
    }

    private List<Method> getAnnotatedMethods(Class<?> clazz) {
        List<Method> result = new ArrayList<>();
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Url.class)) {
                result.add(m);
            }
        }
        return result;
    }

    private HashMap<String, Mapping> buildMappings(List<Class<?>> controllers) {
        HashMap<String, Mapping> map = new HashMap<>();

        for (Class<?> clazz : controllers) {
            for (Method m : getAnnotatedMethods(clazz)) {
                Url url = m.getAnnotation(Url.class);
                String key = url.method().toUpperCase() + ":" + url.value();
                map.put(key, new Mapping(clazz.getName(), m.getName()));
            }
        }
        return map;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String page = request.getRequestURI()
                .substring(request.getContextPath().length());

        if (page.equals("/scan")) {
            response.setContentType("text/plain;charset=UTF-8");
            PrintWriter out = response.getWriter();

            out.println("=== Controleurs ===");
            for (Class<?> c : controllerClasses) {
                out.println(" - " + c.getName());

                for (Method m : getAnnotatedMethods(c)) {
                    out.println("     -> " + m.getName());
                }
            }

            out.println();
            out.println("=== Mappings ===");
            for (String key : urlMappings.keySet()) {
                out.println(" " + key + " -> " + urlMappings.get(key));
            }
            return;
        }

        if (page.isEmpty() || page.equals("/")) {
            page = "/index";
        }

        RequestDispatcher dispatch =
                request.getRequestDispatcher("/WEB-INF/views" + page + ".jsp");
        dispatch.forward(request, response);
    }
}