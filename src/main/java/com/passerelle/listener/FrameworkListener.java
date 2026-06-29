package com.passerelle.listener;

import java.io.File;
import java.net.URL;
import java.lang.reflect.Method;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.GetMapping;
import com.passerelle.annotation.PostMapping;
import com.passerelle.annotation.Url;
import com.passerelle.core.Mapping;
import com.passerelle.core.Route;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class FrameworkListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        String packageToScan = ctx.getInitParameter("packageToScan");
        
        List<String> listeControllers = new ArrayList<>();
        HashMap<String, Mapping> urlMappingsOld = new HashMap<>(); // Sprint 1 & 2
        HashMap<Route, Mapping> urlMappings = new HashMap<>(); // Sprint 3

        // Si aucun package n'est configuré, on s'arrête proprement
        if (packageToScan == null || packageToScan.isEmpty()) {
            ctx.setAttribute("listeContro", listeControllers);
            ctx.setAttribute("urlMappingsOld", urlMappingsOld);
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
                        scanDirectory(directory, packageToScan, listeControllers, urlMappingsOld, urlMappings);
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
                                scanClassAndMethods(className, listeControllers, urlMappingsOld, urlMappings);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            ctx.log("[FRAMEWORK] Erreur lors du scan global", e);
        }

        // Sauvegarde dans le ServletContext
        ctx.setAttribute("listeContro", listeControllers);
        ctx.setAttribute("urlMappingsOld", urlMappingsOld);
        ctx.setAttribute("urlMappings", urlMappings);
        
        System.out.println("[FRAMEWORK] Scan terminé : " + listeControllers.size() + " contrôleur(s)");
        System.out.println("[FRAMEWORK] Routes Sprint 2 (@Url) : " + urlMappingsOld.size());
        System.out.println("[FRAMEWORK] Routes Sprint 3 (@GetMapping/@PostMapping) : " + urlMappings.size());
    }

    private void scanDirectory(File directory, String packageName, List<String> controllers, 
                               HashMap<String, Mapping> urlMappingsOld, HashMap<Route, Mapping> urlMappings) {
        File[] files = directory.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), controllers, urlMappingsOld, urlMappings);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                scanClassAndMethods(className, controllers, urlMappingsOld, urlMappings);
            }
        }
    }

    private void scanClassAndMethods(String className, List<String> controllers, 
                                     HashMap<String, Mapping> urlMappingsOld, 
                                     HashMap<Route, Mapping> urlMappings) {
        try {
            Class<?> clazz = Class.forName(className);
            
            // Sprint 1 : Validation du contrôleur
            if (clazz.isAnnotationPresent(Controller.class)) {
                controllers.add(className);
                System.out.println("✅ Contrôleur détecté : " + className);

                // Scan des méthodes de ce contrôleur
                for (Method method : clazz.getDeclaredMethods()) {
                    
                    // Sprint 2 : Annotation @Url
                    if (method.isAnnotationPresent(Url.class)) {
                        Url urlAnnotation = method.getAnnotation(Url.class);
                        String urlValue = urlAnnotation.value();
                        urlMappingsOld.put(urlValue, new Mapping(className, method.getName()));
                        System.out.println("  🔗 @Url : [" + urlValue + "] -> " + method.getName() + "()");
                    }
                    
                    // Sprint 3 : Annotation @GetMapping
                    if (method.isAnnotationPresent(GetMapping.class)) {
                        GetMapping getMapping = method.getAnnotation(GetMapping.class);
                        String urlValue = getMapping.value();
                        urlMappings.put(new Route(urlValue, "GET"), new Mapping(className, method.getName()));
                        System.out.println("  🔗 @GetMapping : [" + urlValue + "] GET -> " + method.getName() + "()");
                    }
                    
                    // Sprint 3 : Annotation @PostMapping
                    if (method.isAnnotationPresent(PostMapping.class)) {
                        PostMapping postMapping = method.getAnnotation(PostMapping.class);
                        String urlValue = postMapping.value();
                        urlMappings.put(new Route(urlValue, "POST"), new Mapping(className, method.getName()));
                        System.out.println("  🔗 @PostMapping : [" + urlValue + "] POST -> " + method.getName() + "()");
                    }
                }
            }
        } catch (ClassNotFoundException ignored) {}
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}