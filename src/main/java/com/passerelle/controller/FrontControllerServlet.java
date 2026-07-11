package com.passerelle.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.passerelle.constant.HttpMethod;
import com.passerelle.core.Mapping;
import com.passerelle.core.Route;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontControllerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    @SuppressWarnings("unchecked")
    protected void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String url = req.getRequestURI().substring(req.getContextPath().length());
        HttpMethod method = HttpMethod.fromString(req.getMethod());

        HashMap<Route, Mapping> mappings = (HashMap<Route, Mapping>) getServletContext().getAttribute("urlMappings");
        Route current = new Route(url, method);
        Mapping mapping = mappings.get(current);

        if (mapping == null && mappings != null) {
            for (Map.Entry<Route, Mapping> entry : mappings.entrySet()) {
                Route route = entry.getKey();
                String routeUrl = route.getUrl();
                if (url.equals(routeUrl) ||
                    (url.endsWith("/post") && routeUrl.equals(url.substring(0, url.length() - 5)) && method == HttpMethod.POST && route.getMethod() == HttpMethod.POST) ||
                    (url.endsWith("/get") && routeUrl.equals(url.substring(0, url.length() - 4)) && method == HttpMethod.GET && route.getMethod() == HttpMethod.GET)) {
                    mapping = entry.getValue();
                    break;
                }
            }
        }

        res.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = res.getWriter();

        if (mapping != null) {
            executer(mapping, req, out);
        } else {
            afficher(out);
        }
    }

    @SuppressWarnings("unchecked")
    private void executer(Mapping mapping, HttpServletRequest req, PrintWriter out) {
        try {
            Map<Class<?>, Object> instances = (Map<Class<?>, Object>) getServletContext().getAttribute("controllerInstances");
            if (instances == null) {
                out.println("Erreur: instances indisponibles");
                return;
            }

            Class<?> clazz = Class.forName(mapping.getClassName());
            Object instance = instances.get(clazz);
            if (instance == null) {
                instance = clazz.getDeclaredConstructor().newInstance();
                instances.put(clazz, instance);
            }

            Object result = clazz.getDeclaredMethod(mapping.getMethodName()).invoke(instance);
            if (result != null) out.println(result.toString());
        } catch (Exception e) {
            out.println("Erreur: " + e.getMessage());
            e.printStackTrace(out);
        }
    }

    @SuppressWarnings("unchecked")
    private void afficher(PrintWriter out) {
        List<String> ctrls = (List<String>) getServletContext().getAttribute("listeContro");
        out.println("Controleurs :");
        if (ctrls != null) for (String c : ctrls) out.println("- " + c);

        HashMap<String, Mapping> old = (HashMap<String, Mapping>) getServletContext().getAttribute("urlMappingsOld");
        out.println("\nRoutes @Url :");
        if (old != null) for (String u : old.keySet()) out.println("- " + u);

        HashMap<Route, Mapping> news = (HashMap<Route, Mapping>) getServletContext().getAttribute("urlMappings");
        out.println("\nRoutes GET/POST :");
        if (news != null) for (Route r : news.keySet()) out.println("- [" + r.getMethod() + "] " + r.getUrl());
    }
}