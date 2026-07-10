package com.minimarket.dto;

import jakarta.validation.constraints.NotBlank;

/** Entrada validada para crear o actualizar el nombre de una categoria */
public record CategoriaRequest(
        @NotBlank String nombre
) {
}
