package com.gros.modules.auth.dto;

public record LoginResponse(String token, String nom, String role) {}