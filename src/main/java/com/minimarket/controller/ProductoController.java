package com.minimarket.controller;

import com.minimarket.dto.ProductoRequest;
import com.minimarket.dto.ProductoResponse;
import com.minimarket.entity.Producto;
import com.minimarket.service.CategoriaService;
import com.minimarket.service.ProductoService;
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

/** Administra productos, categorias asociadas y enlaces REST documentados */
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    @Operation(summary = "Lista todos los productos", description = "Retorna la coleccion de productos con enlaces HATEOAS hacia cada recurso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public CollectionModel<EntityModel<ProductoResponse>> listarProductos() {
        List<EntityModel<ProductoResponse>> productos = productoService.findAll().stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(productos,
                linkTo(methodOn(ProductoController.class).listarProductos()).withSelfRel());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un producto por ID", description = "Retorna un producto individual con enlaces a si mismo, coleccion, inventario y categoria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<EntityModel<ProductoResponse>> obtenerProductoPorId(
            @Parameter(description = "Identificador del producto", example = "10") @PathVariable Long id) {
        Producto producto = productoService.findById(id);
        if (producto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toModel(producto));
    }

    @PostMapping
    @Operation(summary = "Crea un producto", description = "Registra un nuevo producto asociado a una categoria existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<ProductoResponse> guardarProducto(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(productoService.save(toEntity(request))));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un producto", description = "Reemplaza los datos de un producto existente manteniendo su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoResponse> actualizarProducto(
            @Parameter(description = "Identificador del producto", example = "10") @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {
        Producto productoExistente = productoService.findById(id);
        if (productoExistente != null) {
            Producto producto = toEntity(request);
            producto.setId(id);
            return ResponseEntity.ok(toResponse(productoService.save(producto)));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un producto", description = "Elimina un producto existente si no existen restricciones de integridad.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Void> eliminarProducto(
            @Parameter(description = "Identificador del producto", example = "10") @PathVariable Long id) {
        Producto producto = productoService.findById(id);
        if (producto != null) {
            productoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private EntityModel<ProductoResponse> toModel(Producto producto) {
        EntityModel<ProductoResponse> model = EntityModel.of(toResponse(producto));
        model.add(linkTo(methodOn(ProductoController.class).obtenerProductoPorId(producto.getId())).withSelfRel());
        model.add(linkTo(methodOn(ProductoController.class).listarProductos()).withRel("productos"));
        model.add(linkTo(methodOn(InventarioController.class)
                .listarMovimientosPorProducto(producto.getId())).withRel("inventario"));

        if (producto.getCategoria() != null && producto.getCategoria().getId() != null) {
            model.add(linkTo(methodOn(CategoriaController.class)
                    .obtenerCategoriaPorId(producto.getCategoria().getId())).withRel("categoria"));
        }

        return model;
    }

    private ProductoResponse toResponse(Producto producto) {
        Long categoriaId = producto.getCategoria() != null ? producto.getCategoria().getId() : null;
        String categoriaNombre = producto.getCategoria() != null ? producto.getCategoria().getNombre() : null;

        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock(),
                categoriaId,
                categoriaNombre
        );
    }

    private Producto toEntity(ProductoRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.nombre());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setCategoria(categoriaService.findById(request.categoriaId()));
        return producto;
    }
}
