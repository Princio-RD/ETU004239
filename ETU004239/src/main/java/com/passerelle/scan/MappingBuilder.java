package com.passerelle.scan;

import com.passerelle.annotation.Public;
import com.passerelle.annotation.Role;
import com.passerelle.annotation.Url;
import com.passerelle.mapping.Mapping;

import jakarta.servlet.ServletContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public class MappingBuilder {

    @SuppressWarnings("unchecked")
    public static HashMap<String, Mapping> build(ServletContext context) {
        List<Class<?>> controllers = (List<Class<?>>) context.getAttribute("controllerClasses");
        if (controllers == null) {
            throw new RuntimeException("controllerClasses non initialise");
        }

        String defaultRole = context.getInitParameter("security.default.role");
        HashMap<String, Mapping> mappings = new HashMap<>();

        for (Class<?> clazz : controllers) {
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Url.class)) {
                    Url url = m.getAnnotation(Url.class);
                    String key = url.method().toUpperCase() + ":" + url.value();

                    boolean isPublic = m.isAnnotationPresent(Public.class) 
                                    || clazz.isAnnotationPresent(Public.class);

                    String requiredRole = null;
                    if (!isPublic) {
                        Role roleAnn = m.getAnnotation(Role.class);
                        if (roleAnn == null) roleAnn = clazz.getAnnotation(Role.class);
                        
                        if (roleAnn != null) {
                            requiredRole = roleAnn.value();
                            if (requiredRole.isEmpty()) {
                                requiredRole = defaultRole;
                            }
                        }
                    }

                    mappings.put(key, new Mapping(clazz, m, isPublic, requiredRole));
                }
            }
        }

        context.setAttribute("urlMappings", mappings);
        System.out.println("Mappings : " + mappings.size());
        return mappings;
    }
}