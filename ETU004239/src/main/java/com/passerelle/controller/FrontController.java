package com.passerelle.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.Url;
import com.passerelle.mapping.Mapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.ConvertUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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

        String key = request.getMethod().toUpperCase() + ":" + page;

        if (urlMappings.containsKey(key)) {
            executeMapping(urlMappings.get(key), request, response);
            return;
        }

        if (page.isEmpty() || page.equals("/")) {
            page = "/index";
        }

        RequestDispatcher dispatch =
                request.getRequestDispatcher("/WEB-INF/views" + page + ".jsp");
        dispatch.forward(request, response);
    }

    private void executeMapping(Mapping mapping, HttpServletRequest request,
                                HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();

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
                out.println("Param " + params[i].getName()
                        + " = " + args[i]
                        + " (" + params[i].getType().getSimpleName() + ")");
            }

            method.invoke(instance, args);
            out.println("Methode executee : " + mapping);

        } catch (Exception e) {
            throw new ServletException("Erreur invocation : " + e.getMessage(), e);
        }
    }
}