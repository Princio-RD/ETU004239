package com.passerelle.config;

import com.passerelle.database.Database;

import jakarta.servlet.ServletContext;

public class DbConfig {

    public static void init(ServletContext context) throws Exception {
        String driver = context.getInitParameter("db.driver");
        String url = context.getInitParameter("db.url");
        String user = context.getInitParameter("db.user");
        String password = context.getInitParameter("db.password");

        if (driver == null || url == null) {
            System.out.println("=== [Passerelle] Pas de config BDD ===");
            return;
        }

        Database.init(driver, url, user, password);

        System.out.println("=== [Passerelle] BDD initialisee ===");
        System.out.println("URL : " + url);
    }
}