package com.passerelle.binding;

import com.passerelle.mapping.MapFile;
import com.passerelle.mapping.MultiFile;

import jakarta.servlet.http.HttpServletRequest;

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

        String name = BinderUtils.resolveName(param);
        Class<?> type = param.getType();

        if (type == MultiFile.class) {
            return MultiFileBinder.bind(name, request);
        }

        if (BinderUtils.isSession(type)) {
            return SessionBinder.bind(request);
        }

        if (type == MapFile.class) {
            return FileBinder.bind(name, request);
        }

        if (BinderUtils.isMapFileMap(param)) {
            return MapFileBinder.bind(request);
        }

        if (BinderUtils.isSimple(type)) {
            return SimpleBinder.bind(name, type, request);
        }

        return ObjectBinder.bind(type, request,
                BinderUtils.hasParamAnnotation(param) ? name : null);
    }
}