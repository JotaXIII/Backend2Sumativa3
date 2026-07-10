package com.minimarket.dto;

import java.util.Date;
import java.util.List;

/** Respuesta publica de venta con fecha y usuario asociado */
public record VentaResponse(
        Long id,
        Date fecha,
        Long usuarioId,
        String username,
        List<Long> detalleIds
) {
}
