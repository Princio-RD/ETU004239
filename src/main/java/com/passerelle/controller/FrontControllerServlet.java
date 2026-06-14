package com.passerelle.controller;

import jakarta.servlet.http.HttpServlet;

public class FrontControllerServlet extends HttpServlet{
    // url du projet
    private final String URL= "http://localhost:8080/BANK2/cheque/list_cheque";

    @Override
    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws  ServletException,IOException{
        processRequest(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws  ServletException,IOException{
        processRequest(request,response);
    }

    protected void processRequest(HttpServletRequest request,HttpServletResponse response) throws  ServletException,IOException{
        response.sendRedirect(URL);
    }
}
