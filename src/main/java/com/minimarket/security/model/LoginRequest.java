package com.minimarket.security.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/** Entrada validada para autenticar credenciales de usuario */
@Schema(description = "Credenciales para obtener un token JWT")
public record LoginRequest(
        @Schema(description = "Nombre de usuario registrado", example = "admin")
        @NotBlank String username,
        @Schema(description = "Contrasena del usuario", example = "admin123")
        @NotBlank String password
) {
}
