package com.minimarket.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/** Configura la documentacion OpenAPI y el esquema JWT usado por Swagger UI */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI minimarketOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Minimarket Plus API")
                        .description("""
                                Documentacion avanzada del backend Minimarket Plus.
                                Incluye contratos REST, seguridad JWT Bearer y respuestas navegables con HATEOAS para productos,
                                carrito de compra, inventario, ventas, usuarios y recursos asociados.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo Backend II")
                                .email("soporte@minimarketplus.local")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor local de desarrollo")
                ))
                .tags(List.of(
                        new Tag().name("Autenticacion").description("Obtencion de token JWT para consumir endpoints protegidos"),
                        new Tag().name("Productos").description("Gestion de productos y navegacion hacia categoria e inventario"),
                        new Tag().name("Carrito").description("Administracion de productos agregados al carrito por usuario"),
                        new Tag().name("Inventario").description("Registro y consulta de movimientos de stock"),
                        new Tag().name("Usuarios").description("Administracion de usuarios, roles y relaciones con ventas"),
                        new Tag().name("Categorias").description("Catalogo de categorias de productos"),
                        new Tag().name("Ventas").description("Registro y consulta de ventas"),
                        new Tag().name("Detalle de ventas").description("Lineas de detalle asociadas a ventas y productos")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtenido desde /api/auth/login")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
