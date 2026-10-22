package com.passerelle.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private Map<String, Object> data = new HashMap<>();
    private boolean modified = false;

    public Object get(String key) {
        return data.get(key);
    }

    public void add(String key, Object value) {
        data.put(key, value);
        modified = true;
    }

    public void remove(String key) {
        data.remove(key);
        modified = true;
    }

    public void clear() {
        data.clear();
        modified = true;
    }

    public boolean contains(String key) {
        return data.containsKey(key);
    }

    public Map<String, Object> getData() {
        return data;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }
}