package com.minimarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Date;

/** Entrada validada para registrar cantidad, tipo y producto del movimiento */
@Schema(description = "Datos para registrar o actualizar un movimiento de inventario")
public record InventarioRequest(
        @Schema(description = "Identificador del producto afectado", example = "10")
        @NotNull Long productoId,
        @Schema(description = "Cantidad de unidades del movimiento", example = "15")
        @NotNull @Positive Integer cantidad,
        @Schema(description = "Tipo de movimiento de stock", example = "ENTRADA")
        @NotBlank String tipoMovimiento,
        @Schema(description = "Fecha y hora del movimiento", example = "2026-07-12T13:30:00.000+00:00")
        @NotNull Date fechaMovimiento
) {
}
