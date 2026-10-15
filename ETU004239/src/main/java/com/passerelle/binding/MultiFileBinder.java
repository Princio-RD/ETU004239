package com.passerelle.binding;

import com.passerelle.mapping.MultiFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import java.util.Collection;

public class MultiFileBinder {

    public static MultiFile bind(String prefix, HttpServletRequest request) throws Exception {
        MultiFile mf = new MultiFile();

        Collection<Part> parts;
        try {
            parts = request.getParts();
        } catch (Exception e) {
            return mf;
        }

        String pattern = prefix + ".";

        for (Part part : parts) {
            String partName = part.getName();
            if (!partName.startsWith(pattern)) continue;
            if (part.getSubmittedFileName() == null) continue;
            if (part.getSize() == 0) continue;

            String key = partName.substring(pattern.length());
            mf.add(key, BinderUtils.toMapFile(part));
        }

        return mf;
    }
}