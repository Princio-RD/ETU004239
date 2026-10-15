package com.passerelle.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultiFile {

    private Map<String, MapFile> files = new HashMap<>();

    public void add(String name, MapFile file) {
        files.put(name, file);
    }

    public MapFile get(String name) {
        return files.get(name);
    }

    public boolean has(String name) {
        return files.containsKey(name);
    }

    public Set<String> getNames() {
        return files.keySet();
    }

    public Map<String, MapFile> getFiles() {
        return files;
    }

    public int size() {
        return files.size();
    }

    @Override
    public String toString() {
        return "MultiFile" + files.keySet();
    }
}