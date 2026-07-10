package com.minimarket.security.model;

/** Respuesta de autenticacion con token JWT para solicitudes protegidas */
public record LoginResponse(String token) {
}
