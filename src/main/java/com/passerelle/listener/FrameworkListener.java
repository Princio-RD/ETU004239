package com.passerelle.listener;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import jakarta.servlet.*;
import com.passerelle.annotation.*;
import com.passerelle.core.*;

public class FrameworkListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        String pkg = ctx.getInitParameter("packageToScan");

        System.out.println("=== DEBUT SCAN ===");
        System.out.println("Package: " + pkg);

        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(
                ctx.getInitParameter("db_url"), 
                ctx.getInitParameter("db_user"), 
                ctx.getInitParameter("db_password")
            );
            ctx.setAttribute("dbConnection", conn);
            System.out.println("DB Connected");
        } catch (Exception e) {
            System.err.println("Erreur DB: " + e.getMessage());
        }

        HashMap<Route, Mapping> urlMappings = new HashMap<>();
        Map<Class<?>, Object> instances = new ConcurrentHashMap<>();
        
        try {
            String path = pkg.replace('.', '/');
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
            
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                System.out.println("Resource: " + resource);
                System.out.println("Protocol: " + resource.getProtocol());
                
                if (resource.getProtocol().equals("file")) {
                    File dir = new File(resource.toURI());
                    scanDir(dir, pkg, urlMappings, instances);
                } else if (resource.getProtocol().equals("jar")) {
                    scanJar(resource, pkg, urlMappings, instances);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur Scan: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Mappings enregistrés: " + urlMappings.size());
        for (Route r : urlMappings.keySet()) {
            System.out.println("  " + r.getMethod() + " " + r.getUrl());
        }
        System.out.println("=== FIN SCAN ===");
        
        ctx.setAttribute("urlMappings", urlMappings);
        ctx.setAttribute("controllerInstances", instances);
    }

    private void scanJar(URL jarUrl, String pkg, HashMap<Route, Mapping> mappings, Map<Class<?>, Object> instances) {
        try {
            String jarPath = jarUrl.getPath();
            jarPath = URLDecoder.decode(jarPath, "UTF-8");
            jarPath = jarPath.substring(5, jarPath.indexOf("!"));
            System.out.println("Scan JAR: " + jarPath);
            
            try (JarInputStream jarStream = new JarInputStream(new java.io.FileInputStream(jarPath))) {
                JarEntry entry;
                String packagePath = pkg.replace('.', '/');
                while ((entry = jarStream.getNextJarEntry()) != null) {
                    String name = entry.getName();
                    if (name.endsWith(".class") && name.startsWith(packagePath)) {
                        String className = name.replace('/', '.').replace(".class", "");
                        try {
                            Class<?> clazz = Class.forName(className);
                            if (clazz.isAnnotationPresent(Controller.class)) {
                                System.out.println("  Controleur: " + className);
                                instances.putIfAbsent(clazz, clazz.getDeclaredConstructor().newInstance());
                                for (Method m : clazz.getDeclaredMethods()) {
                                    if (m.isAnnotationPresent(GetMapping.class)) {
                                        GetMapping a = m.getAnnotation(GetMapping.class);
                                        mappings.put(new Route(a.value(), "GET"), 
                                                    new Mapping(clazz.getName(), m.getName(), a.view()));
                                        System.out.println("    GET " + a.value());
                                    } else if (m.isAnnotationPresent(PostMapping.class)) {
                                        PostMapping a = m.getAnnotation(PostMapping.class);
                                        mappings.put(new Route(a.value(), "POST"), 
                                                    new Mapping(clazz.getName(), m.getName(), a.view()));
                                        System.out.println("    POST " + a.value());
                                    } else if (m.isAnnotationPresent(Url.class)) {
                                        Url a = m.getAnnotation(Url.class);
                                        mappings.put(new Route(a.value(), "GET"), 
                                                    new Mapping(clazz.getName(), m.getName(), ""));
                                        System.out.println("    URL " + a.value());
                                    }
                                }
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur scan JAR: " + e.getMessage());
        }
    }

    private void scanDir(File dir, String pkg, HashMap<Route, Mapping> mappings, Map<Class<?>, Object> instances) {
        File[] files = dir.listFiles();
        if (files == null) return;
        
        for (File f : files) {
            if (f.isDirectory()) {
                scanDir(f, pkg + "." + f.getName(), mappings, instances);
            } else if (f.getName().endsWith(".class")) {
                try {
                    String className = pkg + "." + f.getName().replace(".class", "");
                    Class<?> clazz = Class.forName(className);
                    
                    if (!clazz.isAnnotationPresent(Controller.class)) continue;
                    
                    instances.putIfAbsent(clazz, clazz.getDeclaredConstructor().newInstance());
                    
                    for (Method m : clazz.getDeclaredMethods()) {
                        if (m.isAnnotationPresent(GetMapping.class)) {
                            GetMapping a = m.getAnnotation(GetMapping.class);
                            mappings.put(new Route(a.value(), "GET"), new Mapping(clazz.getName(), m.getName(), a.view()));
                        } else if (m.isAnnotationPresent(PostMapping.class)) {
                            PostMapping a = m.getAnnotation(PostMapping.class);
                            mappings.put(new Route(a.value(), "POST"), new Mapping(clazz.getName(), m.getName(), a.view()));
                        } else if (m.isAnnotationPresent(Url.class)) {
                            Url a = m.getAnnotation(Url.class);
                            mappings.put(new Route(a.value(), "GET"), new Mapping(clazz.getName(), m.getName(), ""));
                        }
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            Connection conn = (Connection) sce.getServletContext().getAttribute("dbConnection");
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception e) {
            sce.getServletContext().log("Erreur fermeture DB", e);
        }
        sce.getServletContext().removeAttribute("urlMappings");
        sce.getServletContext().removeAttribute("controllerInstances");
    }
}