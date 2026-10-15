package com.passerelle.binding;

import com.passerelle.mapping.MapFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MapFileBinder {

    public static Map<String, MapFile> bind(HttpServletRequest request) throws Exception {
        Map<String, MapFile> result = new HashMap<>();

        Collection<Part> parts;
        try {
            parts = request.getParts();
        } catch (Exception e) {
            return result;
        }

        for (Part part : parts) {
            if (part.getSubmittedFileName() == null) continue;
            if (part.getSize() == 0) continue;
            result.put(part.getName(), BinderUtils.toMapFile(part));
        }
        return result;
    }
}