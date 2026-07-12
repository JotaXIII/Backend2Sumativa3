package com.minimarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

/** Entrada validada para crear ventas asociadas a un usuario */
@Schema(description = "Datos para registrar una venta asociada a un usuario")
public record VentaRequest(
        @Schema(description = "Identificador del usuario que realiza la compra", example = "3")
        @NotNull Long usuarioId,
        @Schema(description = "Fecha y hora de la venta", example = "2026-07-12T14:10:00.000+00:00")
        @NotNull Date fecha
) {
}
