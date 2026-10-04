package com.passerelle.mapping;

import java.util.HashMap;

public class ModelView {

    private String view;
    private HashMap<String, Object> data = new HashMap<>();

    public ModelView(String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void addItem(String key, Object value) {
        data.put(key, value);
    }
}