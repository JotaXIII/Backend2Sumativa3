package com.minimarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/** Respuesta publica del movimiento de stock con fecha y producto asociado */
@Schema(description = "Movimiento de inventario con producto relacionado")
public record InventarioResponse(
        @Schema(description = "Identificador unico del movimiento", example = "12")
        Long id,
        @Schema(description = "Cantidad de unidades movidas", example = "15")
        Integer cantidad,
        @Schema(description = "Tipo de movimiento registrado", example = "ENTRADA")
        String tipoMovimiento,
        @Schema(description = "Fecha y hora del movimiento", example = "2026-07-12T13:30:00.000+00:00")
        Date fechaMovimiento,
        @Schema(description = "Identificador del producto", example = "10")
        Long productoId,
        @Schema(description = "Nombre del producto", example = "Arroz grado 1 1kg")
        String productoNombre
) {
}
