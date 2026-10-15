package com.passerelle.scan;

import com.passerelle.annotation.Url;
import com.passerelle.mapping.Mapping;

import jakarta.servlet.ServletContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MappingBuilder {

    @SuppressWarnings("unchecked")
    public static HashMap<String, Mapping> build(ServletContext context) {
        List<Class<?>> controllers =
                (List<Class<?>>) context.getAttribute("controllerClasses");

        if (controllers == null) {
            throw new RuntimeException("controllerClasses non initialise");
        }

        HashMap<String, Mapping> mappings = new HashMap<>();

        for (Class<?> clazz : controllers) {
            for (Method m : getAnnotatedMethods(clazz)) {
                Url url = m.getAnnotation(Url.class);
                String key = url.method().toUpperCase() + ":" + url.value();
                mappings.put(key, new Mapping(clazz, m));
            }
        }

        context.setAttribute("urlMappings", mappings);

        System.out.println("Mappings : " + mappings.size());

        return mappings;
    }

    private static List<Method> getAnnotatedMethods(Class<?> clazz) {
        List<Method> result = new ArrayList<>();
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Url.class)) {
                result.add(m);
            }
        }
        return result;
    }
}