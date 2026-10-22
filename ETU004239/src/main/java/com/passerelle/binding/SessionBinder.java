package com.passerelle.binding;

import com.passerelle.session.Session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Enumeration;
import java.util.Map;

public class SessionBinder {

    /**
     * Cree une Session (copie de HttpSession) pour l'injection.
     */
    public static Session bind(HttpServletRequest request) {
        Session session = new Session();
        HttpSession hs = request.getSession(true);

        Enumeration<String> names = hs.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            session.getData().put(name, hs.getAttribute(name));
        }
        return session;
    }

    /**
     * Synchronise les modifications de Session vers HttpSession.
     */
    public static void update(Session session, HttpServletRequest request) {
        if (!session.isModified()) return;

        HttpSession hs = request.getSession(true);

        // 1. Ajouter / mettre a jour
        for (Map.Entry<String, Object> e : session.getData().entrySet()) {
            hs.setAttribute(e.getKey(), e.getValue());
        }

        // 2. Supprimer les cles retirees
        Enumeration<String> existing = hs.getAttributeNames();
        while (existing.hasMoreElements()) {
            String key = existing.nextElement();
            if (!session.getData().containsKey(key)) {
                hs.removeAttribute(key);
            }
        }
    }
}