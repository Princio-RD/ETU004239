package com.passerelle.binding;

import com.passerelle.annotation.Param;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ArgumentBinder {

    public static Object[] bind(Method method, HttpServletRequest request) throws Exception {
        Parameter[] params = method.getParameters();
        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            args[i] = bindParameter(params[i], request);
        }
        return args;
    }

    private static Object bindParameter(Parameter param, HttpServletRequest request)
            throws Exception {

        Param annotation = param.getAnnotation(Param.class);
        String name = (annotation != null) ? annotation.value() : param.getName();
        Class<?> type = param.getType();

        if (isSimple(type)) {
            return bindSimple(name, type, request);
        }
        return bindObject(type, request, annotation != null ? name : null);
    }

    private static Object bindSimple(String name, Class<?> type,
                                     HttpServletRequest request) {
        String value = request.getParameter(name);
        if (value == null || value.isEmpty()) return null;
        return ConvertUtils.convert(value, type);
    }

    private static Object bindObject(Class<?> type, HttpServletRequest request, String prefix)
            throws Exception {

        Object obj = type.getDeclaredConstructor().newInstance();

        for (Field f : type.getDeclaredFields()) {
            String fieldPath = (prefix == null) ? f.getName() : prefix + "." + f.getName();

            if (isSimple(f.getType())) {
                String value = request.getParameter(fieldPath);
                if (value != null && !value.isEmpty()) {
                    BeanUtils.setProperty(obj, f.getName(),
                            ConvertUtils.convert(value, f.getType()));
                }
            } else {
                Object sub = bindObject(f.getType(), request, fieldPath);
                BeanUtils.setProperty(obj, f.getName(), sub);
            }
        }
        return obj;
    }

    private static boolean isSimple(Class<?> type) {
        return type.isPrimitive()
                || type == String.class
                || Number.class.isAssignableFrom(type)
                || type == Boolean.class
                || type == java.util.Date.class;
    }
}