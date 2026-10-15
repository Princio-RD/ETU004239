package com.passerelle.scan;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.RestApi;

import jakarta.servlet.ServletContext;

import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControllerScanner {

    public static List<Class<?>> scan(ServletContext context) {
        String packageScan = context.getInitParameter("packageToScan");

        if (packageScan == null || packageScan.isEmpty()) {
            throw new RuntimeException("parametre packageToScan non trouve");
        }

        Reflections reflect = new Reflections(packageScan);

        Set<Class<?>> set = new HashSet<>();
        set.addAll(reflect.getTypesAnnotatedWith(Controller.class));
        set.addAll(reflect.getTypesAnnotatedWith(RestApi.class));

        List<Class<?>> controllers = new ArrayList<>(set);

        context.setAttribute("controllerClasses", controllers);

        System.out.println("=== [Passerelle] Scan termine ===");
        System.out.println("Controleurs : " + controllers.size());

        return controllers;
    }
}