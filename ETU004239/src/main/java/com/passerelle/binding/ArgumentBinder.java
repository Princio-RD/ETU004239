package com.passerelle.binding;

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
            String name = params[i].getName();
            Class<?> type = params[i].getType();

            if (isSimple(type)) {
                args[i] = bindSimple(name, type, request);
            } else {
                args[i] = bindObject(type, request);
            }
        }
        return args;
    }

    private static Object bindSimple(String name, Class<?> type,
                                     HttpServletRequest request) {
        String value = request.getParameter(name);
        if (value == null || value.isEmpty()) return null;
        return ConvertUtils.convert(value, type);
    }

    private static Object bindObject(Class<?> type, HttpServletRequest request)
            throws Exception {

        Object obj = type.getDeclaredConstructor().newInstance();

        for (Field f : type.getDeclaredFields()) {
            String value = request.getParameter(f.getName());
            if (value != null) {
                BeanUtils.setProperty(obj, f.getName(),
                        ConvertUtils.convert(value, f.getType()));
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