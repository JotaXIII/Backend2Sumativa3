package com.minimarket.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Entrada validada para asociar usuario, producto y cantidad en el carrito */
public record CarritoRequest(
        @NotNull Long usuarioId,
        @NotNull Long productoId,
        @NotNull @Positive Integer cantidad
) {
}
