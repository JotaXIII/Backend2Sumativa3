package com.minimarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

/** Entrada validada para credenciales y roles de un usuario */
@Schema(description = "Datos para crear o actualizar un usuario del sistema")
public record UsuarioRequest(
        @Schema(description = "Nombre unico de inicio de sesion", example = "cliente01")
        @NotBlank String username,
        @Schema(description = "Contrasena en texto plano recibida para ser cifrada por el backend", example = "clave123")
        @NotBlank @Size(min = 6) String password,
        @Schema(description = "Identificadores de roles asignados al usuario", example = "[1, 2]")
        Set<Long> roleIds
) {
}
