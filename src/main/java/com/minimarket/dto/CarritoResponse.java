package com.minimarket.dto;

/** Respuesta publica de carrito con producto, usuario y cantidad confirmada */
public record CarritoResponse(
        Long id,
        Integer cantidad,
        Long usuarioId,
        String username,
        Long productoId,
        String productoNombre
) {
}
