package com.passerelle.listener;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.passerelle.annotation.Controller;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class FrameworkListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String packageToScan = sce.getServletContext().getInitParameter("packageToScan");
        List<String> listeControllers = new ArrayList<>();

        if (packageToScan == null || packageToScan.isEmpty()) {
            sce.getServletContext().setAttribute("listeContro", listeControllers);
            return;
        }

        try {
            String path = packageToScan.replace('.', '/');
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                // CAS 1 : Fichiers dans le système (développement)
                if (resource.getProtocol().equals("file")) {
                    File directory = new File(resource.toURI());
                    if (directory.exists() && directory.isDirectory()) {
                        for (File file : directory.listFiles()) {
                            if (file.getName().endsWith(".class")) {
                                String className = packageToScan + "." + file.getName().replace(".class", "");
                                Class<?> clazz = Class.forName(className);
                                if (clazz.isAnnotationPresent(Controller.class)) {
                                    listeControllers.add(className);
                                    System.out.println(className);
                                }
                            }
                        }
                    }
                }
                // CAS 2 : Fichiers dans un JAR (production)
                else if (resource.getProtocol().equals("jar")) {
                    String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
                    try (JarFile jarFile = new JarFile(jarPath)) {
                        Enumeration<JarEntry> entries = jarFile.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.startsWith(path) && name.endsWith(".class") && !entry.isDirectory()) {
                                String className = name.replace("/", ".").replace(".class", "");
                                Class<?> clazz = Class.forName(className);
                                if (clazz.isAnnotationPresent(Controller.class)) {
                                    listeControllers.add(className);
                                    System.out.println(className);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sce.getServletContext().setAttribute("listeContro", listeControllers);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Rien
    }
}