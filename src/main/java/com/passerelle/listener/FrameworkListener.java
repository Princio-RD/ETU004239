package com.passerelle.listener;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.Url;
import com.passerelle.core.Mapping;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class FrameworkListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        String packageToScan = ctx.getInitParameter("packageToScan");
        
        List<String> listeControllers = new ArrayList<>();
        HashMap<String, Mapping> urlMappings = new HashMap<>();

        // Si aucun package n'est configuré, on s'arrête proprement
        if (packageToScan == null || packageToScan.isEmpty()) {
            ctx.setAttribute("listeContro", listeControllers);
            ctx.setAttribute("urlMappings", urlMappings);
            return;
        }

        try {
            String path = packageToScan.replace('.', '/');
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                // CAS 1 : Mode Développement (Dossier classique)
                if ("file".equals(resource.getProtocol())) {
                    File directory = new File(resource.toURI());
                    if (directory.exists() && directory.isDirectory()) {
                        for (File file : directory.listFiles(f -> f.getName().endsWith(".class"))) {
                            String className = packageToScan + "." + file.getName().replace(".class", "");
                            scanClassAndMethods(className, listeControllers, urlMappings);
                        }
                    }
                } 
                // CAS 2 : Mode Production (Fichier JAR)
                else if ("jar".equals(resource.getProtocol())) {
                    String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
                    try (JarFile jarFile = new JarFile(jarPath)) {
                        Enumeration<JarEntry> entries = jarFile.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            
                            if (name.startsWith(path) && name.endsWith(".class") && !entry.isDirectory()) {
                                String className = name.replace("/", ".").replace(".class", "");
                                scanClassAndMethods(className, listeControllers, urlMappings);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            ctx.log("[FRAMEWORK] Erreur lors du scan global", e);
        }

        // Sauvegarde des deux Sprints dans le ServletContext
        ctx.setAttribute("listeContro", listeControllers);
        ctx.setAttribute("urlMappings", urlMappings);
        
        System.out.println("[FRAMEWORK] Scan terminé : " + listeControllers.size() + " contrôleur(s), " + urlMappings.size() + " URL(s) chargée(s).");
    }

    /**
     * Centralisation des Sprints 1 et 2 pour éviter la duplication de code.
     */
    private void scanClassAndMethods(String className, List<String> controllers, HashMap<String, Mapping> mappings) {
        try {
            Class<?> clazz = Class.forName(className);
            
            // Sprint 1 : Validation du contrôleur
            if (clazz.isAnnotationPresent(Controller.class)) {
                controllers.add(className);

                // Sprint 2 : Scan des méthodes de ce contrôleur
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Url.class)) {
                        Url urlAnnotation = method.getAnnotation(Url.class);
                        String urlValue = urlAnnotation.value();
                        
                        // Enregistrement de la route
                        mappings.put(urlValue, new Mapping(className, method.getName()));
                        System.out.println(" URL mappée : [" + urlValue + "] -> " + method.getName() + "()");
                    }
                }
            }
        } catch (ClassNotFoundException ignored) {}
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}