package com.gros.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gros.modules.auth.entity.Utilisateur;
import com.gros.modules.auth.repository.UtilisateurRepository;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final UtilisateurRepository repo;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String h = req.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) {
            try {
                String email = jwt.extraireEmail(h.substring(7));
                repo.findByEmail(email).filter(Utilisateur::isActif).ifPresent(u -> {
                    var auth = new UsernamePasswordAuthenticationToken(u, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name())));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                });
            } catch (JwtException | IllegalArgumentException e) {
                // jeton invalide ou expiré : l'utilisateur reste non authentifié
            }
        }
        chain.doFilter(req, res);
    }
}