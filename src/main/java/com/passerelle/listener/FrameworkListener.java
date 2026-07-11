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
        
        // SPRINT 1 : Liste des contrôleurs
        List<String> listeControllers = new ArrayList<>();
        
        // SPRINT 2 : Mappage URL → Méthode
        HashMap<String, Mapping> urlMappings = new HashMap<>();

        if (packageToScan == null || packageToScan.isEmpty()) {
            ctx.setAttribute("listeContro", listeControllers);
            ctx.setAttribute("urlMappings", urlMappings);
            return;
        }

        try {
            String path = packageToScan.replace('.', '/');
            Enumeration<URL> resources = Thread.currentThread()
                .getContextClassLoader().getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                if ("file".equals(resource.getProtocol())) {
                    File directory = new File(resource.toURI());
                    if (directory.exists() && directory.isDirectory()) {
                        scanDirectory(directory, packageToScan, listeControllers, urlMappings);
                    }
                } else if ("jar".equals(resource.getProtocol())) {
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
                                scanClass(className, listeControllers, urlMappings);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            ctx.log("[SPRINT2] Erreur lors du scan", e);
        }

        // Stockage dans le contexte
        ctx.setAttribute("listeContro", listeControllers);
        ctx.setAttribute("urlMappings", urlMappings);
        
        System.out.println("[SPRINT2] Scan terminé :");
        System.out.println("  - Contrôleurs : " + listeControllers.size());
        System.out.println("  - Routes @Url : " + urlMappings.size());
        
        for (String url : urlMappings.keySet()) {
            Mapping mapping = urlMappings.get(url);
            System.out.println("  🔗 " + url + " → " + mapping.getMethodName() + "()");
        }
    }

    private void scanDirectory(File directory, String packageName, 
                               List<String> controllers,
                               HashMap<String, Mapping> urlMappings) {
        File[] files = directory.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), 
                            controllers, urlMappings);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + 
                    file.getName().replace(".class", "");
                scanClass(className, controllers, urlMappings);
            }
        }
    }

    private void scanClass(String className, List<String> controllers,
                           HashMap<String, Mapping> urlMappings) {
        try {
            Class<?> clazz = Class.forName(className);
            
            if (clazz.isAnnotationPresent(Controller.class)) {
                controllers.add(className);
                
                // SPRINT 2 : Scanner les méthodes @Url
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Url.class)) {
                        Url urlAnnotation = method.getAnnotation(Url.class);
                        String urlValue = urlAnnotation.value();
                        
                        // Détection des conflits
                        if (urlMappings.containsKey(urlValue)) {
                            throw new IllegalStateException(
                                "[CONFLIT] URL '" + urlValue + "' déjà utilisée !"
                            );
                        }
                        
                        urlMappings.put(urlValue, new Mapping(className, method.getName()));
                        System.out.println("  🔗 @Url : " + urlValue + " → " + 
                                         method.getName() + "()");
                    }
                }
            }
        } catch (ClassNotFoundException ignored) {}
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().removeAttribute("listeContro");
        sce.getServletContext().removeAttribute("urlMappings");
    }
}