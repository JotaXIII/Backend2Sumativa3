package com.minimarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Date;

/** Entrada validada para registrar cantidad, tipo y producto del movimiento */
public record InventarioRequest(
        @NotNull Long productoId,
        @NotNull @Positive Integer cantidad,
        @NotBlank String tipoMovimiento,
        @NotNull Date fechaMovimiento
) {
}
