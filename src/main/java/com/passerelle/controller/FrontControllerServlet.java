package com.passerelle.controller;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.passerelle.core.Mapping;
import com.passerelle.core.Route;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontControllerServlet extends HttpServlet {

    @Override
    @SuppressWarnings("unchecked")
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String url = req.getRequestURI().substring(req.getContextPath().length());
        String methodHTTP = req.getMethod();
        
        Route currentRoute = new Route(url, methodHTTP);
        HashMap<Route, Mapping> mappings = (HashMap<Route, Mapping>) getServletContext().getAttribute("urlMappings");

        if (mappings != null && mappings.containsKey(currentRoute)) {
            Mapping mapping = mappings.get(currentRoute);
            
            try {
                // Récupération de l'instance du contrôleur depuis le conteneur (IoC)
                Map<Class<?>, Object> instances = (Map<Class<?>, Object>) getServletContext().getAttribute("controllerInstances");
                Class<?> clazz = Class.forName(mapping.getClassName());
                Object controllerInstance = instances.get(clazz);
                
                // Recherche de la méthode ciblée
                Method targetMethod = null;
                for (Method m : clazz.getDeclaredMethods()) {
                    if (m.getName().equals(mapping.getMethodName())) {
                        targetMethod = m;
                        break;
                    }
                }

                if (targetMethod != null) {
                    Object result;
                    
                    // Auto-injection : Si la méthode attend une HttpServletRequest, on la lui passe
                    if (targetMethod.getParameterCount() == 1 && targetMethod.getParameterTypes()[0] == HttpServletRequest.class) {
                        result = targetMethod.invoke(controllerInstance, req);
                    } else {
                        result = targetMethod.invoke(controllerInstance);
                    }
                    
                    // Gestion du retour (Vue JSP ou Texte brut)
                    if (result instanceof String) {
                        String viewPath = (String) result;
                        req.getRequestDispatcher(viewPath).forward(req, res);
                    } else if (result != null) {
                        res.setContentType("text/plain;charset=UTF-8");
                        res.getWriter().print(result.toString());
                    }
                }
            } catch (Exception e) {
                throw new ServletException("Erreur d'exécution du contrôleur", e);
            }
        } else {
            // Si la route n'existe pas
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "La ressource [" + methodHTTP + " " + url + "] est introuvable.");
        }
    }
}