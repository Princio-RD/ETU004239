package com.passerelle.core;

public class Mapping {
    private final String className;
    private final String methodName;

    public Mapping(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    public String getClassName() { return className; }
    public String getMethodName() { return methodName; }
}