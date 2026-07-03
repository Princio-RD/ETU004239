package com.passerelle.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Gestion des routes Sprint 3 
            if (sprint3(request, response)) return;
            
            // Fallback sur Sprint 2
            if (sprint2(request, response)) return;
            
            // Cas spécial
            if (sprint0(request, response)) return;

            // Affichage des informations de debug
            response.setContentType("text/plain;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                sprint1(out);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.println("Erreur : " + e.getMessage());
                e.printStackTrace(out);
            }
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
            for (Route route : urlMappings.keySet()) {
                out.println("- [" + route.getMethod() + "] " + route.getUrl());
            }
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
        String httpMethodString = request.getMethod();
        HttpMethod httpMethod = HttpMethod.fromString(httpMethodString);
        
        if (httpMethod == null) {
            return false;
        }

        @SuppressWarnings("unchecked")
        HashMap<Route, Mapping> urlMappings = (HashMap<Route, Mapping>) getServletContext().getAttribute("urlMappings");

        if (urlMappings != null) {
            // 1. Recherche exacte d'abord
            Route currentRoute = new Route(urlRelative, httpMethod);
            if (urlMappings.containsKey(currentRoute)) {
                executerMethode(urlMappings.get(currentRoute), request, response);
                return true;
            }

            // 2. Recherche avec pattern matching (pour les routes dynamiques)
            for (Map.Entry<Route, Mapping> entry : urlMappings.entrySet()) {
                Route route = entry.getKey();
                if (route.getMethod().equals(httpMethod) && matchRoute(route.getUrl(), urlRelative)) {
                    executerMethode(entry.getValue(), request, response);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Match une route avec pattern (ex: /profil/{id} match /profil/123)
     */
    private boolean matchRoute(String pattern, String url) {
        if (pattern.equals(url)) return true;
        
        String[] patternParts = pattern.split("/");
        String[] urlParts = url.split("/");
        
        if (patternParts.length != urlParts.length) return false;
        
        for (int i = 0; i < patternParts.length; i++) {
            if (patternParts[i].startsWith("{") && patternParts[i].endsWith("}")) {
                continue; // C'est un paramètre dynamique, on ignore
            }
            if (!patternParts[i].equals(urlParts[i])) {
                return false;
            }
        }
        return true;
    }

    // executer les methode du controller
    @SuppressWarnings("unchecked")
    private void executerMethode(Mapping mapping, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Map<Class<?>, Object> controllerInstances = 
                (Map<Class<?>, Object>) getServletContext().getAttribute("controllerInstances");
            
            if (controllerInstances == null) {
                throw new ServletException("Aucune instance de contrôleur disponible");
            }

            Class<?> clazz = Class.forName(mapping.getClassName());

            Object controllerInstance = controllerInstances.get(clazz);
            
            if (controllerInstance == null) {
                controllerInstance = clazz.getDeclaredConstructor().newInstance();
                controllerInstances.put(clazz, controllerInstance);
                System.out.println("[FRAMEWORK] Instance créée à la volée pour : " + clazz.getName());
            }

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

    @Override
    public void destroy() {
        super.destroy();
    }
}