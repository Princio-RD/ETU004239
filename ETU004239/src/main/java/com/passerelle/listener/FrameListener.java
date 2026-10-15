package com.passerelle.listener;

import com.passerelle.config.DbConfig;
import com.passerelle.config.ViewConfig;
import com.passerelle.scan.ControllerScanner;
import com.passerelle.scan.MappingBuilder;
import com.passerelle.utils.UploadConfig;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class FrameListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        try {
            // 1. Config upload 
            UploadConfig.load(context);

            // 2. Config BDD
            DbConfig.init(context);

            // 3. Config vues
            ViewConfig.init(context);

            // 4. Scan des controleurs
            ControllerScanner.scan(context);

            // 5. Construction des mappings
            MappingBuilder.build(context);

        } catch (Exception e) {
            throw new RuntimeException("Erreur init : " + e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}