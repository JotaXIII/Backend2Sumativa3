package com.minimarket.dto;

/** Respuesta publica con identificador y nombre de categoria */
public record CategoriaResponse(
        Long id,
        String nombre
) {
}
