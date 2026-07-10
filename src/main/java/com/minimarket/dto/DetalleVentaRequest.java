package com.minimarket.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Entrada validada para asociar venta, producto, cantidad y precio */
public record DetalleVentaRequest(
        @NotNull Long ventaId,
        @NotNull Long productoId,
        @NotNull @Positive Integer cantidad,
        @NotNull @Positive Double precio
) {
}
