package com.passerelle.listener;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.Url;
import com.passerelle.database.Database;
import com.passerelle.mapping.Mapping;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FrameListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        try {
            initDatabase(context);
            initControllers(context);
        } catch (Exception e) {
            throw new RuntimeException("Erreur init : " + e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    private void initDatabase(ServletContext context) throws Exception {
        String driver = context.getInitParameter("db.driver");
        String url = context.getInitParameter("db.url");
        String user = context.getInitParameter("db.user");
        String password = context.getInitParameter("db.password");

        if (driver == null || url == null) {
            System.out.println("=== [Passerelle] Pas de config BDD ===");
            return;
        }

        Database.init(driver, url, user, password);

        System.out.println("=== [Passerelle] BDD initialisee ===");
        System.out.println("Driver : " + driver);
        System.out.println("URL : " + url);
    }

    private void initControllers(ServletContext context) throws Exception {
        String packageScan = context.getInitParameter("packageToScan");

        if (packageScan == null || packageScan.isEmpty()) {
            throw new RuntimeException("parametre packageToScan non trouve");
        }

        List<Class<?>> controllers = getClassPackage(packageScan);
        HashMap<String, Mapping> mappings = buildMappings(controllers);

        context.setAttribute("controllerClasses", controllers);
        context.setAttribute("urlMappings", mappings);

        System.out.println("=== [Passerelle] Scan termine ===");
        System.out.println("Controleurs : " + controllers.size());
        System.out.println("Mappings : " + mappings.size());
    }

    private ArrayList<Class<?>> getClassPackage(String packageName) {
        org.reflections.Reflections reflect = new org.reflections.Reflections(packageName);
        return new ArrayList<>(reflect.getTypesAnnotatedWith(Controller.class));
    }

    private List<Method> getAnnotatedMethods(Class<?> clazz) {
        List<Method> result = new ArrayList<>();
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Url.class)) {
                result.add(m);
            }
        }
        return result;
    }

    private HashMap<String, Mapping> buildMappings(List<Class<?>> controllers) {
        HashMap<String, Mapping> map = new HashMap<>();

        for (Class<?> clazz : controllers) {
            for (Method m : getAnnotatedMethods(clazz)) {
                Url url = m.getAnnotation(Url.class);
                String key = url.method().toUpperCase() + ":" + url.value();
                map.put(key, new Mapping(clazz.getName(), m.getName()));
            }
        }
        return map;
    }
}