package com.passerelle.core;
public class Mapping {
    private String className, methodName, view;
    public Mapping(String c, String m, String v) { 
        this.className = c; this.methodName = m; this.view = v; 
    }
    public String getClassName() { return className; }
    public String getMethodName() { return methodName; }
    public String getView() { return view; }
}