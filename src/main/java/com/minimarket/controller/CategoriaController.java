package com.minimarket.controller;

import com.minimarket.dto.CategoriaRequest;
import com.minimarket.dto.CategoriaResponse;
import com.minimarket.entity.Categoria;
import com.minimarket.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/** Administra categorias usando DTOs para evitar exponer entidades JPA */
@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    @Operation(summary = "Lista las categorias", description = "Retorna el catalogo de categorias usadas para clasificar productos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categorias obtenidas correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado por rol")
    })
    public CollectionModel<EntityModel<CategoriaResponse>> listarCategorias() {
        List<EntityModel<CategoriaResponse>> categorias = categoriaService.findAll().stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(categorias,
                linkTo(methodOn(CategoriaController.class).listarCategorias()).withSelfRel());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una categoria por ID", description = "Retorna una categoria individual si existe.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria obtenida correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado por rol"),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada")
    })
    public ResponseEntity<EntityModel<CategoriaResponse>> obtenerCategoriaPorId(
            @Parameter(description = "Identificador de la categoria", example = "1") @PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        return (categoria != null) ? ResponseEntity.ok(toModel(categoria)) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Crea una categoria", description = "Registra una nueva categoria para clasificar productos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado por rol")
    })
    public ResponseEntity<EntityModel<CategoriaResponse>> guardarCategoria(@Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(categoriaService.save(toEntity(request))));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza una categoria", description = "Reemplaza el nombre de una categoria existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado por rol"),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada")
    })
    public ResponseEntity<EntityModel<CategoriaResponse>> actualizarCategoria(
            @Parameter(description = "Identificador de la categoria", example = "1") @PathVariable Long id,
            @Valid @RequestBody CategoriaRequest request) {
        Categoria categoriaExistente = categoriaService.findById(id);
        if (categoriaExistente != null) {
            Categoria categoria = toEntity(request);
            categoria.setId(id);
            return ResponseEntity.ok(toModel(categoriaService.save(categoria)));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina una categoria", description = "Elimina una categoria existente si no existen productos asociados que lo impidan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoria eliminada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado por rol"),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada")
    })
    public ResponseEntity<Void> eliminarCategoria(
            @Parameter(description = "Identificador de la categoria", example = "1") @PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        if (categoria != null) {
            categoriaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private Categoria toEntity(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNombre(request.nombre());
        return categoria;
    }

    private EntityModel<CategoriaResponse> toModel(Categoria categoria) {
        EntityModel<CategoriaResponse> model = EntityModel.of(toResponse(categoria));
        model.add(linkTo(methodOn(CategoriaController.class).obtenerCategoriaPorId(categoria.getId())).withSelfRel());
        model.add(linkTo(methodOn(CategoriaController.class).listarCategorias()).withRel("categorias"));
        return model;
    }

    private CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNombre());
    }
}
