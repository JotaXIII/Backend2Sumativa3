package com.minimarket.controller;

import com.minimarket.dto.CarritoRequest;
import com.minimarket.dto.CarritoResponse;
import com.minimarket.entity.Carrito;
import com.minimarket.service.ProductoService;
import com.minimarket.service.CarritoService;
import com.minimarket.service.UsuarioService;
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

/** Administra items del carrito y publica enlaces HATEOAS de navegacion */
@RestController
@RequestMapping("/api/carrito")
@Tag(name = "Carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Lista los productos del carrito", description = "Retorna los items del carrito con enlaces hacia producto, usuario y coleccion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public CollectionModel<EntityModel<CarritoResponse>> listarCarrito() {
        List<EntityModel<CarritoResponse>> carrito = carritoService.findAll().stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(carrito,
                linkTo(methodOn(CarritoController.class).listarCarrito()).withSelfRel());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un item del carrito por ID", description = "Retorna un item individual del carrito con enlaces HATEOAS relacionados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item del carrito obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Item del carrito no encontrado")
    })
    public ResponseEntity<EntityModel<CarritoResponse>> obtenerCarritoPorId(
            @Parameter(description = "Identificador del item de carrito", example = "7") @PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toModel(carrito));
    }

    @PostMapping
    @Operation(summary = "Agrega un producto al carrito", description = "Asocia un producto y una cantidad al carrito de un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto agregado al carrito"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<CarritoResponse> agregarProductoAlCarrito(@Valid @RequestBody CarritoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(carritoService.save(toEntity(request))));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un item del carrito", description = "Actualiza usuario, producto o cantidad de un item existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item del carrito actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Item del carrito no encontrado")
    })
    public ResponseEntity<CarritoResponse> actualizarCarrito(
            @Parameter(description = "Identificador del item de carrito", example = "7") @PathVariable Long id,
            @Valid @RequestBody CarritoRequest request) {
        Carrito existente = carritoService.findById(id);
        if (existente != null) {
            Carrito carrito = toEntity(request);
            carrito.setId(id);
            return ResponseEntity.ok(toResponse(carritoService.save(carrito)));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un producto del carrito", description = "Quita un item existente del carrito.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado del carrito"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Item del carrito no encontrado")
    })
    public ResponseEntity<Void> eliminarProductoDelCarrito(
            @Parameter(description = "Identificador del item de carrito", example = "7") @PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito != null) {
            carritoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private EntityModel<CarritoResponse> toModel(Carrito carrito) {
        EntityModel<CarritoResponse> model = EntityModel.of(toResponse(carrito));
        model.add(linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(carrito.getId())).withSelfRel());
        model.add(linkTo(methodOn(CarritoController.class).listarCarrito()).withRel("carrito"));

        if (carrito.getProducto() != null && carrito.getProducto().getId() != null) {
            model.add(linkTo(methodOn(ProductoController.class)
                    .obtenerProductoPorId(carrito.getProducto().getId())).withRel("producto"));
        }

        if (carrito.getUsuario() != null && carrito.getUsuario().getId() != null) {
            model.add(linkTo(methodOn(UsuarioController.class)
                    .obtenerUsuarioPorId(carrito.getUsuario().getId())).withRel("usuario"));
        }

        return model;
    }

    private CarritoResponse toResponse(Carrito carrito) {
        Long usuarioId = carrito.getUsuario() != null ? carrito.getUsuario().getId() : null;
        String username = carrito.getUsuario() != null ? carrito.getUsuario().getUsername() : null;
        Long productoId = carrito.getProducto() != null ? carrito.getProducto().getId() : null;
        String productoNombre = carrito.getProducto() != null ? carrito.getProducto().getNombre() : null;

        return new CarritoResponse(
                carrito.getId(),
                carrito.getCantidad(),
                usuarioId,
                username,
                productoId,
                productoNombre
        );
    }

    private Carrito toEntity(CarritoRequest request) {
        Carrito carrito = new Carrito();
        carrito.setUsuario(usuarioService.findById(request.usuarioId()).orElse(null));
        carrito.setProducto(productoService.findById(request.productoId()));
        carrito.setCantidad(request.cantidad());
        return carrito;
    }
}
