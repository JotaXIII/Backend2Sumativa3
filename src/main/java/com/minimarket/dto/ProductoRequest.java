package com.minimarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/** Entrada validada para crear o actualizar producto, precio, stock y categoria */
public record ProductoRequest(
        @NotBlank String nombre,
        @NotNull @Positive Double precio,
        @NotNull @PositiveOrZero Integer stock,
        @NotNull Long categoriaId
) {
}
