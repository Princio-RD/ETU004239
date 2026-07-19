package com.passerelle.listener;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.GetMapping;
import com.passerelle.annotation.PostMapping;
import com.passerelle.core.Mapping;
import com.passerelle.core.Route;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class FrameworkListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        String pkg = ctx.getInitParameter("packageToScan");

        // 1. Initialisation des structures du conteneur (Le "ctx" du schéma)
        HashMap<Route, Mapping> urlMappings = new HashMap<>();
        Map<Class<?>, Object> instances = new ConcurrentHashMap<>();

        // 2. Initialisation de la DataSource (Base de données)
        String dbUrl = ctx.getInitParameter("db_url");
        String dbUser = ctx.getInitParameter("db_user");
        String dbPwd = ctx.getInitParameter("db_password");

        if (dbUrl != null && !dbUrl.isEmpty()) {
            try {
                if (dbUrl.contains("mysql")) Class.forName("com.mysql.cj.jdbc.Driver");
                else if (dbUrl.contains("postgresql")) Class.forName("org.postgresql.Driver");
                else if (dbUrl.contains("sqlite")) Class.forName("org.sqlite.JDBC");
                
                Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPwd);
                
                // INJECTION DE LA CONNEXION DANS LE CONTENEUR IoC
                instances.put(Connection.class, conn);
                System.out.println("[Framework] DataSource initialisée et ajoutée au conteneur.");
            } catch (Exception e) {
                ctx.log("[Framework] Erreur d'initialisation de la base de données", e);
            }
        }

        // 3. Scan des contrôleurs
        if (pkg != null && !pkg.isEmpty()) {
            try {
                String path = pkg.replace('.', '/');
                Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();
                    if ("file".equals(url.getProtocol())) {
                        File dir = new File(url.toURI());
                        if (dir.exists() && dir.isDirectory()) {
                            scanDir(dir, pkg, urlMappings, instances);
                        }
                    } else if ("jar".equals(url.getProtocol())) {
                        String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
                        try (JarFile jar = new JarFile(jarPath)) {
                            Enumeration<JarEntry> entries = jar.entries();
                            while (entries.hasMoreElements()) {
                                JarEntry entry = entries.nextElement();
                                String name = entry.getName();
                                if (name.startsWith(path) && name.endsWith(".class") && !entry.isDirectory()) {
                                    scanClass(name.replace("/", ".").replace(".class", ""), urlMappings, instances);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                ctx.log("[Framework] Erreur lors du scan du package", e);
            }
        }

        // 4. Sauvegarde dans le ServletContext
        ctx.setAttribute("urlMappings", urlMappings);
        ctx.setAttribute("controllerInstances", instances);
        System.out.println("[Framework] Démarrage terminé. " + urlMappings.size() + " routes prêtes.");
    }

    private void scanDir(File dir, String pkg, HashMap<Route, Mapping> mappings, Map<Class<?>, Object> instances) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                scanDir(f, pkg + "." + f.getName(), mappings, instances);
            } else if (f.getName().endsWith(".class")) {
                scanClass(pkg + "." + f.getName().replace(".class", ""), mappings, instances);
            }
        }
    }

    private void scanClass(String className, HashMap<Route, Mapping> mappings, Map<Class<?>, Object> instances) {
        try {
            Class<?> clazz = Class.forName(className);
            if (!clazz.isAnnotationPresent(Controller.class)) return;

            try {
                instances.putIfAbsent(clazz, clazz.getDeclaredConstructor().newInstance());
            } catch (Exception ignored) {}

            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(GetMapping.class)) {
                    mappings.put(new Route(m.getAnnotation(GetMapping.class).value(), "GET"), new Mapping(className, m.getName()));
                }
                if (m.isAnnotationPresent(PostMapping.class)) {
                    mappings.put(new Route(m.getAnnotation(PostMapping.class).value(), "POST"), new Mapping(className, m.getName()));
                }
            }
        } catch (ClassNotFoundException ignored) {}
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        
        // Nettoyage propre de la connexion base de données
        @SuppressWarnings("unchecked")
        Map<Class<?>, Object> instances = (Map<Class<?>, Object>) ctx.getAttribute("controllerInstances");
        if (instances != null && instances.containsKey(Connection.class)) {
            Connection conn = (Connection) instances.get(Connection.class);
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    System.out.println("[Framework] Connexion base de données fermée proprement.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        ctx.removeAttribute("urlMappings");
        ctx.removeAttribute("controllerInstances");
    }
}