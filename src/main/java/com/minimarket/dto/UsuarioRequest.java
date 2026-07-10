package com.minimarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

/** Entrada validada para credenciales y roles de un usuario */
public record UsuarioRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 6) String password,
        Set<Long> roleIds
) {
}
