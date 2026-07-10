package com.minimarket.dto;

import java.util.Date;

/** Respuesta publica del movimiento de stock con fecha y producto asociado */
public record InventarioResponse(
        Long id,
        Integer cantidad,
        String tipoMovimiento,
        Date fechaMovimiento,
        Long productoId,
        String productoNombre
) {
}
