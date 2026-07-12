package com.minimarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Respuesta publica de carrito con producto, usuario y cantidad confirmada */
@Schema(description = "Item del carrito con datos del usuario y producto relacionado")
public record CarritoResponse(
        @Schema(description = "Identificador unico del item de carrito", example = "7")
        Long id,
        @Schema(description = "Cantidad agregada al carrito", example = "2")
        Integer cantidad,
        @Schema(description = "Identificador del usuario", example = "3")
        Long usuarioId,
        @Schema(description = "Nombre de usuario", example = "cliente01")
        String username,
        @Schema(description = "Identificador del producto", example = "10")
        Long productoId,
        @Schema(description = "Nombre del producto", example = "Arroz grado 1 1kg")
        String productoNombre
) {
}
