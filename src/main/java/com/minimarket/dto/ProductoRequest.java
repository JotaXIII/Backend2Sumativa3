package com.minimarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/** Entrada validada para crear o actualizar producto, precio, stock y categoria */
@Schema(description = "Datos requeridos para crear o actualizar un producto del minimarket")
public record ProductoRequest(
        @Schema(description = "Nombre comercial del producto", example = "Arroz grado 1 1kg")
        @NotBlank String nombre,
        @Schema(description = "Precio unitario de venta", example = "1590.0")
        @NotNull @Positive Double precio,
        @Schema(description = "Stock disponible en unidades", example = "25")
        @NotNull @PositiveOrZero Integer stock,
        @Schema(description = "Identificador de la categoria asociada", example = "1")
        @NotNull Long categoriaId
) {
}
