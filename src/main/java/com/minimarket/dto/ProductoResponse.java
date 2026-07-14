package com.minimarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Respuesta publica de producto con categoria, precio y stock disponible */
@Schema(description = "Producto registrado con informacion resumida de su categoria")
public record ProductoResponse(
        @Schema(description = "Identificador unico del producto", example = "10")
        Long id,
        @Schema(description = "Nombre comercial del producto", example = "Arroz grado 1 1kg")
        String nombre,
        @Schema(description = "Precio unitario de venta", example = "1590.0")
        Double precio,
        @Schema(description = "Stock disponible en unidades", example = "25")
        Integer stock,
        @Schema(description = "Identificador de la categoria", example = "1")
        Long categoriaId,
        @Schema(description = "Nombre de la categoria", example = "Abarrotes")
        String categoriaNombre
) {
}
