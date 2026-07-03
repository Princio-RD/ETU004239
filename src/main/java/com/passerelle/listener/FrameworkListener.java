package com.passerelle.listener;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.GetMapping;
import com.passerelle.annotation.PostMapping;
import com.passerelle.annotation.Url;
import com.passerelle.constant.HttpMethod;
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
        HashMap<String, Mapping> urlMappingsOld = new HashMap<>();
        HashMap<Route, Mapping> urlMappings = new HashMap<>();
        
        // Map pour stocker les instances uniques des contrôleurs (Singleton)
        Map<Class<?>, Object> controllerInstances = new ConcurrentHashMap<>();

        if (packageToScan == null || packageToScan.isEmpty()) {
            ctx.setAttribute("listeContro", listeControllers);
            ctx.setAttribute("urlMappingsOld", urlMappingsOld);
            ctx.setAttribute("urlMappings", urlMappings);
            ctx.setAttribute("controllerInstances", controllerInstances);
            return;
        }

        try {
            String path = packageToScan.replace('.', '/');
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                if ("file".equals(resource.getProtocol())) {
                    File directory = new File(resource.toURI());
                    if (directory.exists() && directory.isDirectory()) {
                        scanDirectory(directory, packageToScan, listeControllers, 
                                    urlMappingsOld, urlMappings, controllerInstances);
                    }
                } else if ("jar".equals(resource.getProtocol())) {
                    String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
                    try (JarFile jarFile = new JarFile(jarPath)) {
                        Enumeration<JarEntry> entries = jarFile.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            
                            if (name.startsWith(path) && name.endsWith(".class") && !entry.isDirectory()) {
                                String className = name.replace("/", ".").replace(".class", "");
                                scanClassAndMethods(className, listeControllers, urlMappingsOld, urlMappings, controllerInstances);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (e instanceof IllegalStateException || (e.getCause() != null && e.getCause() instanceof IllegalStateException)) {
                throw (e instanceof IllegalStateException ? (IllegalStateException) e : (IllegalStateException) e.getCause());
            }
            ctx.log("[FRAMEWORK] Erreur lors du scan global", e);
        }

        // Stocker toutes les données dans le contexte de l'application
        ctx.setAttribute("listeContro", listeControllers);
        ctx.setAttribute("urlMappingsOld", urlMappingsOld);
        ctx.setAttribute("urlMappings", urlMappings);
        ctx.setAttribute("controllerInstances", controllerInstances);
        
        System.out.println("[FRAMEWORK] Scan terminé : " + listeControllers.size() + " contrôleur(s)");
        System.out.println("[FRAMEWORK] Routes Sprint 2 (@Url) : " + urlMappingsOld.size());
        System.out.println("[FRAMEWORK] Routes Sprint 3 (@GetMapping/@PostMapping) : " + urlMappings.size());
        System.out.println("[FRAMEWORK] Instances de contrôleurs créées : " + controllerInstances.size());
        
        // Afficher toutes les routes pour le debug
        for (Route route : urlMappings.keySet()) {
            System.out.println("  Route: [" + route.getMethod() + "] " + route.getUrl());
        }
    }

    private void scanDirectory(File directory, String packageName, List<String> controllers, 
                               HashMap<String, Mapping> urlMappingsOld, 
                               HashMap<Route, Mapping> urlMappings,
                               Map<Class<?>, Object> controllerInstances) {
        File[] files = directory.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), 
                            controllers, urlMappingsOld, urlMappings, controllerInstances);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                scanClassAndMethods(className, controllers, urlMappingsOld, 
                                   urlMappings, controllerInstances);
            }
        }
    }

    private void scanClassAndMethods(String className, List<String> controllers, 
                                     HashMap<String, Mapping> urlMappingsOld, 
                                     HashMap<Route, Mapping> urlMappings,
                                     Map<Class<?>, Object> controllerInstances) {
        try {
            Class<?> clazz = Class.forName(className);
            
            if (clazz.isAnnotationPresent(Controller.class)) {
                controllers.add(className);
                System.out.println(" Contrôleur détecté : " + className);
                
                try {
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    controllerInstances.put(clazz, instance);
                    System.out.println("Instance singleton créée pour : " + className);
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'instanciation de " + className + " : " + e.getMessage());
                }

                // Scanner les méthodes du contrôleur
                for (Method method : clazz.getDeclaredMethods()) {
                    
                    // Sprint 2 : Annotation @Url
                    if (method.isAnnotationPresent(Url.class)) {
                        Url urlAnnotation = method.getAnnotation(Url.class);
                        String urlValue = urlAnnotation.value();

                        if (urlMappingsOld.containsKey(urlValue)) {
                            throw new IllegalStateException(" [CONFLIT DE ROUTE CRITIQUE] L'URL @Url '" + urlValue + "' est déjà associée à une méthode dans le framework !");
                        }
                        
                        urlMappingsOld.put(urlValue, new Mapping(className, method.getName()));
                        System.out.println("  🔗 @Url : [" + urlValue + "] -> " + method.getName() + "()");
                    }
                    
                    // Sprint 3 : Annotation @GetMapping
                    if (method.isAnnotationPresent(GetMapping.class)) {
                        GetMapping getMapping = method.getAnnotation(GetMapping.class);
                        String urlValue = getMapping.value();
                        Route newRoute = new Route(urlValue, HttpMethod.GET);

                        if (urlMappings.containsKey(newRoute)) {
                            throw new IllegalStateException(" [CONFLIT DE ROUTE CRITIQUE] La route [GET] '" + urlValue + "' est déjà associée à une autre méthode !");
                        }
                        
                        urlMappings.put(newRoute, new Mapping(className, method.getName()));
                        System.out.println("@GetMapping : [" + urlValue + "] GET -> " + method.getName() + "()");
                    }
                    
                    // Sprint 3 : Annotation @PostMapping
                    if (method.isAnnotationPresent(PostMapping.class)) {
                        PostMapping postMapping = method.getAnnotation(PostMapping.class);
                        String urlValue = postMapping.value();
                        Route newRoute = new Route(urlValue, HttpMethod.POST);
                        
                        if (urlMappings.containsKey(newRoute)) {
                            throw new IllegalStateException("[CONFLIT DE ROUTE CRITIQUE] La route [POST] '" + urlValue + "' est déjà associée à une autre méthode !");
                        }
                        
                        urlMappings.put(newRoute, new Mapping(className, method.getName()));
                        System.out.println(" @PostMapping : [" + urlValue + "] POST -> " + method.getName() + "()");
                    }
                }
            }
        } catch (ClassNotFoundException ignored) {}
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        @SuppressWarnings("unchecked")
        Map<Class<?>, Object> controllerInstances = 
            (Map<Class<?>, Object>) sce.getServletContext().getAttribute("controllerInstances");
        if (controllerInstances != null) {
            controllerInstances.clear();
            System.out.println("[FRAMEWORK] Instances de contrôleurs nettoyées");
        }
        sce.getServletContext().removeAttribute("controllerInstances");
    }
}
