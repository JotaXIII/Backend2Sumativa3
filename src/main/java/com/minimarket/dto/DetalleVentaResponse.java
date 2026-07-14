package com.minimarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Respuesta publica de una linea de venta con producto y valores calculables */
@Schema(description = "Linea de detalle asociada a una venta")
public record DetalleVentaResponse(
        @Schema(description = "Identificador unico del detalle", example = "21")
        Long id,
        @Schema(description = "Identificador de la venta asociada", example = "5")
        Long ventaId,
        @Schema(description = "Identificador del producto vendido", example = "10")
        Long productoId,
        @Schema(description = "Nombre del producto vendido", example = "Arroz grado 1 1kg")
        String productoNombre,
        @Schema(description = "Cantidad vendida", example = "2")
        Integer cantidad,
        @Schema(description = "Precio unitario aplicado", example = "1590.0")
        Double precio
) {
}
