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
        String pkg = ctx.getInitParameter("packageToScan");

        List<String> controllers = new ArrayList<>();
        HashMap<String, Mapping> oldMappings = new HashMap<>();
        HashMap<Route, Mapping> newMappings = new HashMap<>();
        Map<Class<?>, Object> instances = new ConcurrentHashMap<>();

        if (pkg != null && !pkg.isEmpty()) {
            try {
                String path = pkg.replace('.', '/');
                Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();
                    if ("file".equals(url.getProtocol())) {
                        File dir = new File(url.toURI());
                        if (dir.exists() && dir.isDirectory()) {
                            scanDir(dir, pkg, controllers, oldMappings, newMappings, instances);
                        }
                    } else if ("jar".equals(url.getProtocol())) {
                        String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
                        try (JarFile jar = new JarFile(jarPath)) {
                            Enumeration<JarEntry> entries = jar.entries();
                            while (entries.hasMoreElements()) {
                                JarEntry entry = entries.nextElement();
                                String name = entry.getName();
                                if (name.startsWith(path) && name.endsWith(".class") && !entry.isDirectory()) {
                                    String cls = name.replace("/", ".").replace(".class", "");
                                    scanClass(cls, controllers, oldMappings, newMappings, instances);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                ctx.log("[Framework] Erreur scan", e);
            }
        }

        ctx.setAttribute("listeContro", controllers);
        ctx.setAttribute("urlMappingsOld", oldMappings);
        ctx.setAttribute("urlMappings", newMappings);
        ctx.setAttribute("controllerInstances", instances);

        System.out.println("[Framework] Scan ok : " + controllers.size() + " controleurs, " +
                           oldMappings.size() + " @Url, " + newMappings.size() + " GET/POST");
    }

    private void scanDir(File dir, String pkg, List<String> controllers,
                         HashMap<String, Mapping> oldMappings,
                         HashMap<Route, Mapping> newMappings,
                         Map<Class<?>, Object> instances) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                scanDir(f, pkg + "." + f.getName(), controllers, oldMappings, newMappings, instances);
            } else if (f.getName().endsWith(".class")) {
                String cls = pkg + "." + f.getName().replace(".class", "");
                scanClass(cls, controllers, oldMappings, newMappings, instances);
            }
        }
    }

    private void scanClass(String className, List<String> controllers,
                           HashMap<String, Mapping> oldMappings,
                           HashMap<Route, Mapping> newMappings,
                           Map<Class<?>, Object> instances) {
        try {
            Class<?> clazz = Class.forName(className);
            if (!clazz.isAnnotationPresent(Controller.class)) return;

            controllers.add(className);
            try {
                instances.put(clazz, clazz.getDeclaredConstructor().newInstance());
            } catch (Exception ignored) {}

            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Url.class)) {
                    String val = m.getAnnotation(Url.class).value();
                    if (oldMappings.containsKey(val))
                        throw new IllegalStateException("[Conflit] @Url " + val);
                    oldMappings.put(val, new Mapping(className, m.getName()));
                }

                if (m.isAnnotationPresent(GetMapping.class)) {
                    String val = m.getAnnotation(GetMapping.class).value();
                    Route r = new Route(val, HttpMethod.GET);
                    if (newMappings.containsKey(r))
                        throw new IllegalStateException("[Conflit] GET " + val);
                    newMappings.put(r, new Mapping(className, m.getName()));
                }

                if (m.isAnnotationPresent(PostMapping.class)) {
                    String val = m.getAnnotation(PostMapping.class).value();
                    Route r = new Route(val, HttpMethod.POST);
                    if (newMappings.containsKey(r))
                        throw new IllegalStateException("[Conflit] POST " + val);
                    newMappings.put(r, new Mapping(className, m.getName()));
                }
            }
        } catch (ClassNotFoundException ignored) {}
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Map<Class<?>, Object> instances = (Map<Class<?>, Object>) sce.getServletContext().getAttribute("controllerInstances");
        if (instances != null) instances.clear();
        sce.getServletContext().removeAttribute("controllerInstances");
    }
}