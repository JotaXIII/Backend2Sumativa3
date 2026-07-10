package com.minimarket.security.model;

import jakarta.validation.constraints.NotBlank;

/** Entrada validada para autenticar credenciales de usuario */
public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
