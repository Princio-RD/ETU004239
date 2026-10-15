package com.passerelle.config;

import jakarta.servlet.ServletContext;

public class ViewConfig {

    public static void init(ServletContext context) {
        String prefix = context.getInitParameter("view.prefix");
        String suffix = context.getInitParameter("view.suffix");

        if (prefix == null) prefix = "/WEB-INF/views/";
        if (suffix == null) suffix = ".jsp";

        context.setAttribute("view.prefix", prefix);
        context.setAttribute("view.suffix", suffix);

        System.out.println("=== [Passerelle] Vues ===");
        System.out.println("Prefix : " + prefix);
        System.out.println("Suffix : " + suffix);
    }
}