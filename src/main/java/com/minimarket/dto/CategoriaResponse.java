package com.minimarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Respuesta publica con identificador y nombre de categoria */
@Schema(description = "Categoria disponible para clasificar productos")
public record CategoriaResponse(
        @Schema(description = "Identificador unico de la categoria", example = "1")
        Long id,
        @Schema(description = "Nombre visible de la categoria", example = "Abarrotes")
        String nombre
) {
}
