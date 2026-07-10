package com.minimarket.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Date;

/** Entrada validada para crear ventas asociadas a un usuario */
public record VentaRequest(
        @NotNull Long usuarioId,
        @NotNull Date fecha
) {
}
