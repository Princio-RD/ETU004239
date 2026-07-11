package com.passerelle.listener;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.passerelle.annotation.Controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class FrameworkListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        String packageToScan = ctx.getInitParameter("packageToScan");
        
        // SPRINT 1 : Liste des contrôleurs
        List<String> listeControllers = new ArrayList<>();

        if (packageToScan == null || packageToScan.isEmpty()) {
            ctx.setAttribute("listeContro", listeControllers);
            System.out.println("[SPRINT1] Aucun package à scanner");
            return;
        }

        try {
            String path = packageToScan.replace('.', '/');
            Enumeration<URL> resources = Thread.currentThread()
                .getContextClassLoader().getResources(path);

            System.out.println("[SPRINT1] Scan du package : " + packageToScan);
            
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                System.out.println("[SPRINT1] Ressource trouvée : " + resource);

                // Scan des fichiers (développement)
                if ("file".equals(resource.getProtocol())) {
                    File directory = new File(resource.toURI());
                    if (directory.exists() && directory.isDirectory()) {
                        scanDirectory(directory, packageToScan, listeControllers);
                    }
                } 
                // Scan des JARs (production)
                else if ("jar".equals(resource.getProtocol())) {
                    String jarPath = resource.getPath()
                        .substring(5, resource.getPath().indexOf("!"));
                    try (JarFile jarFile = new JarFile(jarPath)) {
                        Enumeration<JarEntry> entries = jarFile.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            
                            if (name.startsWith(path) && name.endsWith(".class") 
                                && !entry.isDirectory()) {
                                String className = name.replace("/", ".")
                                    .replace(".class", "");
                                detectController(className, listeControllers);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            ctx.log("[SPRINT1] Erreur lors du scan", e);
        }

        // Stockage dans le contexte
        ctx.setAttribute("listeContro", listeControllers);
        
        System.out.println("[SPRINT1] Scan terminé : " + listeControllers.size() 
            + " contrôleur(s) trouvé(s)");
        for (String ctrl : listeControllers) {
            System.out.println(" listee " + ctrl);
        }
    }

    private void scanDirectory(File directory, String packageName, 
                               List<String> controllers) {
        File[] files = directory.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), 
                            controllers);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + 
                    file.getName().replace(".class", "");
                detectController(className, controllers);
            }
        }
    }

    private void detectController(String className, List<String> controllers) {
        try {
            Class<?> clazz = Class.forName(className);
            if (clazz.isAnnotationPresent(Controller.class)) {
                controllers.add(className);
            }
        } catch (ClassNotFoundException ignored) {
            // Ignorer les classes non trouvées
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().removeAttribute("listeContro");
        System.out.println("[SPRINT1] Nettoyage effectué");
    }
}