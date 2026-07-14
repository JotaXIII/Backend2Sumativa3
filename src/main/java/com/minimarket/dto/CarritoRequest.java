package com.minimarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Entrada validada para asociar usuario, producto y cantidad en el carrito */
@Schema(description = "Datos necesarios para agregar o actualizar un producto en el carrito")
public record CarritoRequest(
        @Schema(description = "Identificador del usuario propietario del carrito", example = "3")
        @NotNull Long usuarioId,
        @Schema(description = "Identificador del producto agregado", example = "10")
        @NotNull Long productoId,
        @Schema(description = "Cantidad solicitada del producto", example = "2")
        @NotNull @Positive Integer cantidad
) {
}
