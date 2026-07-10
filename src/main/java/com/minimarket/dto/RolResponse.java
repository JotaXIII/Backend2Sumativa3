package com.minimarket.dto;

/** Respuesta publica de rol asignable a usuarios del sistema */
public record RolResponse(
        Long id,
        String nombre
) {
}
