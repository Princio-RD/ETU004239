package com.passerelle.binding;

import com.passerelle.mapping.MapFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

public class FileBinder {

    public static MapFile bind(String name, HttpServletRequest request) throws Exception {
        Part part = request.getPart(name);
        if (part == null || part.getSize() == 0) return null;
        return BinderUtils.toMapFile(part);
    }
}