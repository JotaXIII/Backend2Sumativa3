package com.minimarket.dto;

/** Respuesta publica de una linea de venta con producto y valores calculables */
public record DetalleVentaResponse(
        Long id,
        Long ventaId,
        Long productoId,
        String productoNombre,
        Integer cantidad,
        Double precio
) {
}
