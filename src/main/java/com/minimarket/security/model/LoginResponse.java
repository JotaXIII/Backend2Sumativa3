package com.minimarket.security.model;

import io.swagger.v3.oas.annotations.media.Schema;

/** Respuesta de autenticacion con token JWT para solicitudes protegidas */
@Schema(description = "Token JWT emitido luego de autenticar credenciales validas")
public record LoginResponse(
        @Schema(description = "Token JWT que debe enviarse como Bearer token", example = "eyJhbGciOiJIUzI1NiJ9.ejemplo.firma")
        String token
) {
}
