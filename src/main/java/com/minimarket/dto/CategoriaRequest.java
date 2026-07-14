package com.minimarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/** Entrada validada para crear o actualizar el nombre de una categoria */
@Schema(description = "Datos para crear o actualizar una categoria")
public record CategoriaRequest(
        @Schema(description = "Nombre visible de la categoria", example = "Abarrotes")
        @NotBlank String nombre
) {
}
