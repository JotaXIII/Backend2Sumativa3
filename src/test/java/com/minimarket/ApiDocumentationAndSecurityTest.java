package com.minimarket;

import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.repository.CategoriaRepository;
import com.minimarket.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Valida disponibilidad de OpenAPI, enlaces HATEOAS y reglas de seguridad */
@SpringBootTest
@AutoConfigureMockMvc
class ApiDocumentationAndSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void apiDocsEstaDisponibleSinAutenticacion() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Minimarket Plus API"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.bearerFormat").value("JWT"));
    }

    @Test
    void endpointProtegidoRetornaUnauthorizedSinToken() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "USER")
    void usuarioSinRolAdminNoPuedeCrearProductos() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void productoIndividualIncluyeEnlacesHateoas() throws Exception {
        Producto producto = guardarProducto("Arroz");

        mockMvc.perform(get("/api/productos/{id}", producto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/productos/" + producto.getId())))
                .andExpect(jsonPath("$._links.productos.href", containsString("/api/productos")))
                .andExpect(jsonPath("$._links.inventario.href", containsString("/api/inventario/producto/" + producto.getId())))
                .andExpect(jsonPath("$._links.categoria.href", containsString("/api/categorias/" + producto.getCategoria().getId())));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void listadoProductosIncluyeEnlacesHateoas() throws Exception {
        guardarProducto("Azucar");

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/productos")))
                .andExpect(jsonPath("$._embedded.productoResponseList[0]._links.self.href", containsString("/api/productos/")));
    }

    private Producto guardarProducto(String nombre) {
        Categoria categoria = new Categoria();
        categoria.setNombre("Categoria " + nombre);
        Categoria categoriaGuardada = categoriaRepository.save(categoria);

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecio(1000.0);
        producto.setStock(10);
        producto.setCategoria(categoriaGuardada);

        return productoRepository.save(producto);
    }
}
