package com.passerelle.binding;

import com.passerelle.annotation.Param;
import com.passerelle.mapping.MapFile;
import com.passerelle.mapping.MultiFile;
import com.passerelle.session.Session;

import jakarta.servlet.http.Part;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;


public class BinderUtils {

    public static boolean isSimple(Class<?> type) {
        return type.isPrimitive()
                || type == String.class
                || Number.class.isAssignableFrom(type)
                || type == Boolean.class
                || type == Date.class;
    }

    public static boolean isFile(Class<?> type) {
        return type == MapFile.class || type == MultiFile.class;
    }

    public static boolean isMapFileMap(Parameter param) {
        if (!Map.class.isAssignableFrom(param.getType())) return false;
        Type generic = param.getParameterizedType();
        if (generic instanceof ParameterizedType pt) {
            Type[] args = pt.getActualTypeArguments();
            if (args.length == 2 && args[1] instanceof Class<?> c) {
                return c == MapFile.class;
            }
        }
        return false;
    }

    public static String resolveName(Parameter param) {
        Param annotation = param.getAnnotation(Param.class);
        return (annotation != null) ? annotation.value() : param.getName();
    }

    public static boolean hasParamAnnotation(Parameter param) {
        return param.isAnnotationPresent(Param.class);
    }

    public static MapFile toMapFile(Part part) throws IOException {
        byte[] bytes = part.getInputStream().readAllBytes();
        return new MapFile(part.getSubmittedFileName(),
                part.getContentType(), part.getSize(), bytes);
    }

    public static boolean isSession(Class<?> type) {
        return type == Session.class;
    }
}