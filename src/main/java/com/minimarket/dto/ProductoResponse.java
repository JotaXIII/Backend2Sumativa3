package com.minimarket.dto;

/** Respuesta publica de producto con categoria, precio y stock disponible */
public record ProductoResponse(
        Long id,
        String nombre,
        Double precio,
        Integer stock,
        Long categoriaId,
        String categoriaNombre
) {
}
