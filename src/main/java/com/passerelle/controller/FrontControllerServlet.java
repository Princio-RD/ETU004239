package com.passerelle.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.passerelle.core.Mapping;
import com.passerelle.core.Route;

public class FrontControllerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (sprint0(request, response)) return;
        if (sprint3(request, response)) return;
        if (sprint2(request, response)) return;

        response.setContentType("text/plain;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            sprint1(out);
        }
    }

    private boolean sprint0(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        if (uri != null && uri.contains("/bank2")) {
            response.sendRedirect("http://localhost:8080/BANK2/cheque/list_cheque");
            return true;
        }
        return false;
    }

    private void sprint1(PrintWriter out) {
        out.println("404 - Route introuvable\n");
        
        @SuppressWarnings("unchecked")
        List<String> listeContro = (List<String>) getServletContext().getAttribute("listeContro");
        out.println("Controleurs (Sprint 1) :");
        if (listeContro != null) {
            for (String ctrl : listeContro) out.println("- " + ctrl);
        }
        
        @SuppressWarnings("unchecked")
        HashMap<String, Mapping> urlMappingsOld = (HashMap<String, Mapping>) getServletContext().getAttribute("urlMappingsOld");
        out.println("\nAnciennes routes (Sprint 2 - @Url) :");
        if (urlMappingsOld != null) {
            for (String url : urlMappingsOld.keySet()) out.println("- " + url);
        }

        @SuppressWarnings("unchecked")
        HashMap<Route, Mapping> urlMappings = (HashMap<Route, Mapping>) getServletContext().getAttribute("urlMappings");
        out.println("\nNouvelles routes (Sprint 3 - GetMapping/PostMapping) :");
        if (urlMappings != null) {
            for (Route route : urlMappings.keySet()) out.println("- [" + route.getMethod() + "] " + route.getUrl());
        }
    }

    private boolean sprint2(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String urlRelative = request.getRequestURI().substring(request.getContextPath().length());
        @SuppressWarnings("unchecked")
        HashMap<String, Mapping> urlMappingsOld = (HashMap<String, Mapping>) getServletContext().getAttribute("urlMappingsOld");

        if (urlMappingsOld != null && urlMappingsOld.containsKey(urlRelative)) {
            executerMethode(urlMappingsOld.get(urlRelative), request, response);
            return true;
        }
        return false;
    }

    private boolean sprint3(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String urlRelative = request.getRequestURI().substring(request.getContextPath().length());
        String httpMethod = request.getMethod();
        @SuppressWarnings("unchecked")
        HashMap<Route, Mapping> urlMappings = (HashMap<Route, Mapping>) getServletContext().getAttribute("urlMappings");

        if (urlMappings != null) {
            Route currentRoute = new Route(urlRelative, httpMethod);
            if (urlMappings.containsKey(currentRoute)) {
                executerMethode(urlMappings.get(currentRoute), request, response);
                return true;
            }
        }
        return false;
    }

    private void executerMethode(Mapping mapping, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Class<?> clazz = Class.forName(mapping.getClassName());
            Object controllerInstance = clazz.getDeclaredConstructor().newInstance();
            Method methodJava = clazz.getDeclaredMethod(mapping.getMethodName());
            
            Object result = methodJava.invoke(controllerInstance);
            
            if (result != null) {
                response.setContentType("text/plain;charset=UTF-8");
                try (PrintWriter out = response.getWriter()) {
                    out.println(result.toString());
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}