package com.passerelle.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
        
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        @SuppressWarnings("unchecked")
        List<String> listeControllers = (List<String>) 
            getServletContext().getAttribute("listeContro");
        
        out.println("=== SPRINT 1 - Detection des Controleurs ===");
        out.println("Package : " + 
            getServletContext().getInitParameter("packageToScan"));
        out.println("URL : " + request.getRequestURI());
        out.println("Methode : " + request.getMethod());
        out.println();
        
        if (listeControllers != null && !listeControllers.isEmpty()) {
            out.println("Controleurs trouves :");
            for (int i = 0; i < listeControllers.size(); i++) {
                out.println("  " + (i+1) + ". " + listeControllers.get(i));
            }
            out.println("Total : " + listeControllers.size() + " controleur(s)");
        } else {
            out.println("Aucun controleur trouve");
            out.println();
            out.println("Verifie :");
            out.println("  - packageToScan dans web.xml");
            out.println("  - annotation @Controller sur les classes");
        }
    }
}