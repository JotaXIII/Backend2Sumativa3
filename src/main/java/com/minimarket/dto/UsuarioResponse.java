package com.minimarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

/** Respuesta publica de usuario sin exponer la contrasena almacenada */
@Schema(description = "Usuario registrado sin exponer la contrasena almacenada")
public record UsuarioResponse(
        @Schema(description = "Identificador unico del usuario", example = "3")
        Long id,
        @Schema(description = "Nombre de inicio de sesion", example = "cliente01")
        String username,
        @Schema(description = "Roles asignados", example = "[\"USER\"]")
        Set<String> roles
) {
}
