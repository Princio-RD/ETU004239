package com.passerelle.listener;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.passerelle.annotation.Controller;

import jakarta.servlet.ServletContextListener;

public class FrameworkListener implements ServletContextListener {
    @Override
    public void contextInitialized(jakarta.servlet.ServletContextEvent sce) {
        String basePackage = sce.getServletContext().getInitParameter("packageToScan");

        List<String> listeControllers = new ArrayList<>();
        if (basePackage == null || basePackage.isEmpty()) {
            throw new RuntimeException("The 'packageToScan' context parameter is required.");
        } try {
                String path = basePackage.replace('.', '/');
                URL resource = Thread.currentThread().getContextClassLoader().getResource(path);

                if (resource != null) {
                    File directory = new File(resource.toURI());
                    File[] files = directory.listFiles();

                    if (files != null) {
                        for (File file : files) {
                            if (file.getName().endsWith(".class")) {
                                String className = basePackage + "." + file.getName().replace(".class", "");
                                Class<?> clazz = Class.forName(className);

                                if (clazz.isAnnotationPresent(Controller.class)) {
                                    listeControllers.add(className);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

