package com.passerelle.mapping;

import java.lang.reflect.Method;

public class Mapping {

    private Class<?> clazz;
    private Method method;

    public Mapping(Class<?> clazz, Method method) {
        this.clazz = clazz;
        this.method = method;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return clazz.getName() + "#" + method.getName();
    }
}