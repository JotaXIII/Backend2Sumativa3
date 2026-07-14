package com.minimarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Respuesta publica de rol asignable a usuarios del sistema */
@Schema(description = "Rol de seguridad asignable a usuarios")
public record RolResponse(
        @Schema(description = "Identificador unico del rol", example = "1")
        Long id,
        @Schema(description = "Nombre tecnico del rol usado por Spring Security", example = "ADMIN")
        String nombre
) {
}
