package com.passerelle.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// sprint 0 : point d'entrer unique correspondante a l'URL tapee
public class FrontController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String url = request.getRequestURI();

        String contextPath = request.getContextPath();
        String page = url.substring(contextPath.length());

        String pagination = "/WEB-INF/views" + page + ".jsp";

        RequestDispatcher dispatch = request.getRequestDispatcher(pagination);
        dispatch.forward(request, response);
    }
}