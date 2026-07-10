package com.minimarket.dto;

import java.util.Set;

/** Respuesta publica de usuario sin exponer la contrasena almacenada */
public record UsuarioResponse(
        Long id,
        String username,
        Set<String> roles
) {
}
