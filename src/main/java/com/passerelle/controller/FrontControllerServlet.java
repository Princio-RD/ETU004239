package com.passerelle.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import com.passerelle.constant.HttpMethod;
import com.passerelle.core.Mapping;
import com.passerelle.core.Route;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontControllerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) 
            throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) 
            throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) 
            throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) 
            throws ServletException, IOException {
        processRequest(req, res);
    }

    protected void processRequest(HttpServletRequest request, 
                                  HttpServletResponse response) 
            throws ServletException, IOException {
        
        String urlRelative = request.getRequestURI()
            .substring(request.getContextPath().length());
        HttpMethod httpMethod = HttpMethod.fromString(request.getMethod());
        
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        @SuppressWarnings("unchecked")
        HashMap<Route, Mapping> urlMappings = (HashMap<Route, Mapping>) 
            getServletContext().getAttribute("urlMappings");
        
        Route currentRoute = new Route(urlRelative, httpMethod);
        Mapping mapping = urlMappings.get(currentRoute);
        
        if (mapping != null) {
            out.println("=== SPRINT 3 - Routage GetMapping/PostMapping ===");
            out.println("URL : " + urlRelative);
            out.println("Methode HTTP : " + httpMethod);
            out.println();
            
            try {
                String result = executerMethode(mapping);
                out.println("Resultat : " + result);
                out.println("Methode : " + mapping.getMethodName() + "()");
                out.println("Classe : " + mapping.getClassName());
            } catch (Exception e) {
                out.println("Erreur : " + e.getMessage());
                e.printStackTrace(out);
            }
        } else {
            afficherRoutes(out, urlRelative, httpMethod);
        }
    }
    
    private String executerMethode(Mapping mapping) throws Exception {
        Class<?> clazz = Class.forName(mapping.getClassName());
        Object instance = clazz.getDeclaredConstructor().newInstance();
        Method method = clazz.getDeclaredMethod(mapping.getMethodName());
        Object result = method.invoke(instance);
        return result != null ? result.toString() : "null";
    }
    
    private void afficherRoutes(PrintWriter out, String url, HttpMethod method) {
        @SuppressWarnings("unchecked")
        List<String> listeControllers = (List<String>) 
            getServletContext().getAttribute("listeContro");
        
        @SuppressWarnings("unchecked")
        HashMap<Route, Mapping> urlMappings = (HashMap<Route, Mapping>) 
            getServletContext().getAttribute("urlMappings");
        
        out.println("=== SPRINT 3 - Routes disponibles ===");
        out.println("URL demandee : " + url);
        out.println("Methode HTTP : " + method);
        out.println();
        
        out.println("Controleurs :");
        if (listeControllers != null) {
            for (String ctrl : listeControllers) {
                out.println("  - " + ctrl);
            }
        }
        out.println();
        
        out.println("Routes GET/POST :");
        if (urlMappings != null && !urlMappings.isEmpty()) {
            for (Route route : urlMappings.keySet()) {
                Mapping mapping = urlMappings.get(route);
                out.println("  [" + route.getMethod() + "] " + route.getUrl() + 
                           " -> " + mapping.getMethodName() + "()");
            }
        } else {
            out.println("  Aucune route GET/POST trouvee");
        }
    }
}