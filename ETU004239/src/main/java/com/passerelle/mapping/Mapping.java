package com.passerelle.mapping;

import java.lang.reflect.Method;

public class Mapping {

    private Class<?> clazz;
    private Method method;
    private boolean isPublic;
    private String requiredRole;

    public Mapping(Class<?> clazz, Method method, boolean isPublic, String requiredRole) {
        this.clazz = clazz;
        this.method = method;
        this.isPublic = isPublic;
        this.requiredRole = requiredRole;
    }

    public Class<?> getClazz() { return clazz; }
    public Method getMethod() { return method; }
    public boolean isPublic() { return isPublic; }
    public String getRequiredRole() { return requiredRole; }

    @Override
    public String toString() {
        return clazz.getName() + "#" + method.getName();
    }
}