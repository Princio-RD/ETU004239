package com.passerelle.core;

import java.util.Objects;

public class Route {
    private String url, method;

    public Route(String u, String m) { 
        this.url = u; 
        this.method = m.toUpperCase(); 
    }

    public String getUrl() { return url; }
    public String getMethod() { return method; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route r = (Route) o;
        return Objects.equals(url, r.url) && Objects.equals(method, r.method);
    }

    @Override
    public int hashCode() { 
        return Objects.hash(url, method); 
    }
}