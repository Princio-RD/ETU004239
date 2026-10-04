package com.passerelle.controller;

import com.passerelle.annotation.Controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class FrontController extends HttpServlet {

    // sprint 1
    private List<Class<?>> controllerClasses = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        super.init();

        String packageScan = getServletContext().getInitParameter("packageToScan");

        if (packageScan == null || packageScan.isEmpty()) {
            throw new ServletException("erreur critique : parametre packageToScan non trouve");
        }

        try {
            this.controllerClasses = getClassPackage(packageScan);
        } catch (Exception e) {
            throw new ServletException("Erreur lors du scan : " + e.getMessage(), e);
        }
    }

    private ArrayList<Class<?>> getClassPackage(String packageName) {
        org.reflections.Reflections reflect = new org.reflections.Reflections(packageName);
        return new ArrayList<>(reflect.getTypesAnnotatedWith(Controller.class));
    }



    // sprint 0
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        String page = url.substring(contextPath.length());

        // sprint 1 :affichage
        if (page.equals("/scan")) {
            response.setContentType("text/plain;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("=== Resultat du scan ===");
            out.println("Nombre : " + controllerClasses.size());
            for (Class<?> clazz : controllerClasses) {
                out.println(" - " + clazz.getName());
            }
            return;
        }
        
		// sprint 0 
        if (page.isEmpty() || page.equals("/")) {
            page = "/index";
        }

        String pagination = "/WEB-INF/views" + page + ".jsp";

        RequestDispatcher dispatch = request.getRequestDispatcher(pagination);
        dispatch.forward(request, response);
    }
}