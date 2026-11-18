package com.gros.modules.auth.dto;

import com.gros.modules.auth.entity.Role;

public record UtilisateurResponse(Long id, String nom, String email, Role role, boolean actif) {}