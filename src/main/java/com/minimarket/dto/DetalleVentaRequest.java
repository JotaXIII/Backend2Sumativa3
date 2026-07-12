package com.minimarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Entrada validada para asociar venta, producto, cantidad y precio */
@Schema(description = "Datos para registrar o actualizar una linea de detalle de venta")
public record DetalleVentaRequest(
        @Schema(description = "Identificador de la venta asociada", example = "5")
        @NotNull Long ventaId,
        @Schema(description = "Identificador del producto vendido", example = "10")
        @NotNull Long productoId,
        @Schema(description = "Cantidad vendida", example = "2")
        @NotNull @Positive Integer cantidad,
        @Schema(description = "Precio unitario aplicado a la linea", example = "1590.0")
        @NotNull @Positive Double precio
) {
}
