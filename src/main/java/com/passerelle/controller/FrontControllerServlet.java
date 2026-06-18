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
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String uri = request.getRequestURI();
        
        if (uri != null && uri.contains("/bank2")) {
            response.sendRedirect("http://localhost:8080/BANK2/cheque/list_cheque");
            return;
        }

        response.setContentType("text/plain;charset=UTF-8");
        List<String> listeContro = (List<String>) getServletContext().getAttribute("listeContro");

        try (PrintWriter out = response.getWriter()) {
            out.println("=== Sprint 1 - Scan des Controleurs ===");
            out.println();
            
            if (listeContro != null && !listeContro.isEmpty()) {
                out.println("Total: " + listeContro.size() + " controleur(s)");
                out.println();
                for (String nom : listeContro) {
                    out.println(" - " + nom);
                }
            } else {
                out.println("Aucun controleur trouve !");
            }
            
            out.println();
            out.println("Pour aller vers BANK2: http://localhost:8082/bank2");
        }
    }
}