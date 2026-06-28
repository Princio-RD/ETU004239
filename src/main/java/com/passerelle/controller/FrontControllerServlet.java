package com.passerelle.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import com.passerelle.core.Mapping;

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
        if (sprint0(request, response)) {
            return; 
        }

        response.setContentType("text/plain;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            sprint1(out);
            out.println(); 
            sprint2(out);
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
        out.println("=== Sprint 1 - Scan des Controleurs ===");
        out.println();
        
        @SuppressWarnings("unchecked")
        List<String> listeContro = (List<String>) getServletContext().getAttribute("listeContro");

        if (listeContro != null && !listeContro.isEmpty()) {
            out.println("Total: " + listeContro.size() + " controleur(s)");
            out.println();
            for (String nom : listeContro) {
                out.println(" - " + nom);
            }
        } else {
            out.println("Aucun controleur trouve !");
        }
    }

    private void sprint2(PrintWriter out) {
        out.println("=== Sprint 2 - URL Mappings ===");
        out.println();
        
        @SuppressWarnings("unchecked")
        HashMap<String, Mapping> urlMappings = (HashMap<String, Mapping>) getServletContext().getAttribute("urlMappings");

        if (urlMappings != null && !urlMappings.isEmpty()) {
            out.println("Total: " + urlMappings.size() + " route(s)");
            out.println();
            for (String url : urlMappings.keySet()) {
                Mapping mapping = urlMappings.get(url);
                out.println(" [" + url + "] => " + mapping.getClassName() + "." + mapping.getMethodName() + "()");
            }
        } else {
            out.println("Aucune methode annotee avec @Url trouvee !");
        }
        
        out.println();
        out.println("Pour aller vers BANK2: http://localhost:8082/bank2");
    }
}