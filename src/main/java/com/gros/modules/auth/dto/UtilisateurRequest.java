package com.gros.modules.auth.dto;

import com.gros.modules.auth.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UtilisateurRequest(@NotBlank String nom, @NotBlank @Email String email,
                                 @NotBlank @Size(min = 8) String motDePasse, @NotNull Role role) {}