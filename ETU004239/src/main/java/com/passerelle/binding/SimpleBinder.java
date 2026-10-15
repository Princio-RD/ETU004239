package com.passerelle.binding;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.ConvertUtils;

public class SimpleBinder {

    public static Object bind(String name, Class<?> type, HttpServletRequest request) {
        String value = request.getParameter(name);
        if (value == null || value.isEmpty()) return null;
        return ConvertUtils.convert(value, type);
    }
}