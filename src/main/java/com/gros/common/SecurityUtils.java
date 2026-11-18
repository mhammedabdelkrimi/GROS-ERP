package com.gros.common;

import org.springframework.security.core.context.SecurityContextHolder;

import com.gros.modules.auth.entity.Utilisateur;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static Utilisateur utilisateurCourant() {
        var a = SecurityContextHolder.getContext().getAuthentication();
        return (a != null && a.getPrincipal() instanceof Utilisateur u) ? u : null;
    }

    public static Long idCourant() {
        Utilisateur u = utilisateurCourant();
        return u == null ? null : u.getId();
    }
}