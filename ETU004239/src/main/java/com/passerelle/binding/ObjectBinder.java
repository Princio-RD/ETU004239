package com.passerelle.binding;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;

import java.lang.reflect.Field;

public class ObjectBinder {

    public static Object bind(Class<?> type, HttpServletRequest request, String prefix)
            throws Exception {

        Object obj = type.getDeclaredConstructor().newInstance();

        for (Field f : type.getDeclaredFields()) {
            String fieldPath = (prefix == null) ? f.getName() : prefix + "." + f.getName();

            if (BinderUtils.isSimple(f.getType())) {
                String value = request.getParameter(fieldPath);
                if (value != null && !value.isEmpty()) {
                    BeanUtils.setProperty(obj, f.getName(),
                            ConvertUtils.convert(value, f.getType()));
                }
            } else {
                Object sub = bind(f.getType(), request, fieldPath);
                BeanUtils.setProperty(obj, f.getName(), sub);
            }
        }
        return obj;
    }
}