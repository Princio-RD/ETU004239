package com.passerelle.core;

import java.util.Objects;

public class Route {
    private String url;
    private String method;

    public Route(String url, String method) {
        this.url = url;
        this.method = method.toUpperCase();
    }

    public String getUrl() { return url; }
    public String getMethod() { return method; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route route = (Route) o;
        return Objects.equals(url, route.url) && Objects.equals(method, route.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method);
    }
}