package com.minimarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

/** Respuesta publica de venta con fecha y usuario asociado */
@Schema(description = "Venta registrada con usuario y detalles asociados")
public record VentaResponse(
        @Schema(description = "Identificador unico de la venta", example = "5")
        Long id,
        @Schema(description = "Fecha y hora de la venta", example = "2026-07-12T14:10:00.000+00:00")
        Date fecha,
        @Schema(description = "Identificador del usuario comprador", example = "3")
        Long usuarioId,
        @Schema(description = "Nombre de usuario comprador", example = "cliente01")
        String username,
        @Schema(description = "Identificadores de los detalles de venta", example = "[21, 22]")
        List<Long> detalleIds
) {
}
