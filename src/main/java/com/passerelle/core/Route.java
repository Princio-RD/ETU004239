package com.passerelle.core;

import java.util.Objects;
import com.passerelle.constant.HttpMethod;

public class Route {
    private String url;
    private HttpMethod method;

    public Route(String url, HttpMethod method) {
        this.url = url;
        this.method = method;
    }

    public Route(String url, String method) {
        this.url = url;
        this.method = HttpMethod.fromString(method);
        if (this.method == null) {
            throw new IllegalArgumentException("Méthode HTTP invalide : " + method);
        }
    }

    public String getUrl() {
        return url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getMethodAsString() {
        return method != null ? method.name() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equals(url, route.url) && 
               Objects.equals(method, route.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method);
    }

    @Override
    public String toString() {
        return "Route{" +
               "url='" + url + '\'' +
               ", method=" + method +
               '}';
    }
}