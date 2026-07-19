package com.passerelle.controller;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.passerelle.core.*;

public class FrontControllerServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String url = req.getRequestURI().substring(req.getContextPath().length());
        Route route = new Route(url, req.getMethod());
        HashMap<Route, Mapping> mappings = (HashMap<Route, Mapping>) getServletContext().getAttribute("urlMappings");

        if (mappings != null && mappings.containsKey(route)) {
            Mapping map = mappings.get(route);
            try {
                Map<Class<?>, Object> instances = (Map<Class<?>, Object>) getServletContext().getAttribute("controllerInstances");
                Object ctrl = instances.get(Class.forName(map.getClassName()));
                Method m = null;
                try {
                    m = ctrl.getClass().getDeclaredMethod(map.getMethodName(), HttpServletRequest.class);
                    m.invoke(ctrl, req);
                } catch (NoSuchMethodException e1) {
                    try {
                        m = ctrl.getClass().getDeclaredMethod(map.getMethodName(), HttpServletRequest.class, HttpServletResponse.class);
                        m.invoke(ctrl, req, res);
                    } catch (NoSuchMethodException e2) {
                        m = ctrl.getClass().getDeclaredMethod(map.getMethodName());
                        m.invoke(ctrl);
                    }
                }
                
                // Résolution vue (Prefix + View + Suffix)
                String view = map.getView();
                if (view != null && !view.isEmpty()) {
                    String prefix = getServletContext().getInitParameter("view_prefix");
                    String suffix = getServletContext().getInitParameter("view_suffix");
                    String path = (prefix != null ? prefix : "") + view + (suffix != null ? suffix : "");
                    
                    // Vérifier si la réponse a déjà été écrite
                    if (!res.isCommitted()) {
                        req.getRequestDispatcher(path).forward(req, res);
                    }
                }
            } catch (Exception e) { 
                throw new ServletException(e); 
            }
        } else {
            res.sendError(404);
        }
    }
}