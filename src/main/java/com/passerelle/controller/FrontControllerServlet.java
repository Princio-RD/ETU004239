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
        String urlRelative = request.getRequestURI().substring(request.getContextPath().length());
        HttpMethod httpMethod = HttpMethod.fromString(request.getMethod());
        
        @SuppressWarnings("unchecked")
        HashMap<Route, Mapping> urlMappings = (HashMap<Route, Mapping>) getServletContext().getAttribute("urlMappings");

        Route currentRoute = new Route(urlRelative, httpMethod);
        Mapping mapping = urlMappings.get(currentRoute);

        if (mapping == null && urlMappings != null) {
            for (Map.Entry<Route, Mapping> entry : urlMappings.entrySet()) {
                Route route = entry.getKey();
                
                String routeUrl = route.getUrl();
                if (urlRelative.equals(routeUrl)) {
                    mapping = entry.getValue();
                    System.out.println(" Match exact: " + urlRelative + " → " + route.getMethod());
                    break;
                }
                
                if (urlRelative.endsWith("/post") && routeUrl.equals(urlRelative.substring(0, urlRelative.length() - 5))) {
                    if (httpMethod == HttpMethod.POST && route.getMethod() == HttpMethod.POST) {
                        mapping = entry.getValue();
                        System.out.println("POST détecté: " + urlRelative + " → " + routeUrl + " (" + route.getMethod() + ")");
                        break;
                    }
                }
                
                if (urlRelative.endsWith("/get") && routeUrl.equals(urlRelative.substring(0, urlRelative.length() - 4))) {
                    if (httpMethod == HttpMethod.GET && route.getMethod() == HttpMethod.GET) {
                        mapping = entry.getValue();
                        System.out.println("🔄 GET détecté: " + urlRelative + " → " + routeUrl + " (" + route.getMethod() + ")");
                        break;
                    }
                }
            }
        }
        
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        if (mapping != null) {
            executerMethode(mapping, request, response, out);
        } else {
            afficherSprints(out);
        }
    }
    
    // Méthode pour exécuter la méthode du contrôleur via reflection
    @SuppressWarnings("unchecked")
    private void executerMethode(Mapping mapping, HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            Map<Class<?>, Object> controllerInstances = 
                (Map<Class<?>, Object>) getServletContext().getAttribute("controllerInstances");
            
            if (controllerInstances == null) {
                out.println("Erreur: Aucune instance de contrôleur disponible");
                return;
            }

            Class<?> clazz = Class.forName(mapping.getClassName());
            Object controllerInstance = controllerInstances.get(clazz);
            
            if (controllerInstance == null) {
                controllerInstance = clazz.getDeclaredConstructor().newInstance();
                controllerInstances.put(clazz, controllerInstance);
                System.out.println("[FRAMEWORK] Instance créée à la volée pour : " + clazz.getName());
            }
            
            Method method = clazz.getDeclaredMethod(mapping.getMethodName());
            Object result = method.invoke(controllerInstance);

            System.out.println(" Méthode exécutée : " + mapping.getMethodName() + "()");
            System.out.println("   - URL : " + request.getRequestURI());
            System.out.println("   - Méthode HTTP : " + request.getMethod());
            System.out.println("   - Contrôleur : " + clazz.getSimpleName());
            System.out.println("   - Instance (hashCode) : " + controllerInstance.hashCode());

            if (result != null) {
                out.println(result.toString());
            }
            
        } catch (Exception e) {
            out.println("Erreur lors de l'exécution : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void afficherSprints(PrintWriter out) {
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
}