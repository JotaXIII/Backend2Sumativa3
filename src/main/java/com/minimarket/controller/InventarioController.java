package com.minimarket.controller;

import com.minimarket.dto.InventarioRequest;
import com.minimarket.dto.InventarioResponse;
import com.minimarket.entity.Inventario;
import com.minimarket.service.InventarioService;
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

/** Registra y consulta movimientos de stock con enlaces HATEOAS */
@RestController
@RequestMapping("/api/inventario")
@Tag(name = "Inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private ProductoService productoService;

    @GetMapping
    @Operation(summary = "Lista los movimientos de inventario", description = "Retorna todos los movimientos de stock con enlaces a producto y coleccion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public CollectionModel<EntityModel<InventarioResponse>> listarMovimientosDeInventario() {
        List<EntityModel<InventarioResponse>> movimientos = inventarioService.findAll().stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(movimientos,
                linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario()).withSelfRel());
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Lista movimientos de inventario por producto", description = "Filtra los movimientos de stock asociados a un producto especifico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public CollectionModel<EntityModel<InventarioResponse>> listarMovimientosPorProducto(
            @Parameter(description = "Identificador del producto", example = "10") @PathVariable Long productoId) {
        List<EntityModel<InventarioResponse>> movimientos = inventarioService.findByProductoId(productoId).stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(movimientos,
                linkTo(methodOn(InventarioController.class).listarMovimientosPorProducto(productoId)).withSelfRel());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un movimiento de inventario por ID", description = "Retorna un movimiento individual con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    public ResponseEntity<EntityModel<InventarioResponse>> obtenerMovimientoPorId(
            @Parameter(description = "Identificador del movimiento de inventario", example = "12") @PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        if (inventario == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toModel(inventario));
    }

    @PostMapping
    @Operation(summary = "Registra un movimiento de inventario", description = "Crea una entrada o salida de stock para un producto existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movimiento registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<EntityModel<InventarioResponse>> registrarMovimiento(@Valid @RequestBody InventarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(inventarioService.save(toEntity(request))));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un movimiento de inventario", description = "Reemplaza los datos de un movimiento de inventario existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    public ResponseEntity<EntityModel<InventarioResponse>> actualizarMovimiento(
            @Parameter(description = "Identificador del movimiento de inventario", example = "12") @PathVariable Long id,
            @Valid @RequestBody InventarioRequest request) {
        Inventario existente = inventarioService.findById(id);
        if (existente != null) {
            Inventario inventario = toEntity(request);
            inventario.setId(id);
            return ResponseEntity.ok(toModel(inventarioService.save(inventario)));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un movimiento de inventario", description = "Elimina un movimiento de inventario existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movimiento eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    public ResponseEntity<Void> eliminarMovimiento(
            @Parameter(description = "Identificador del movimiento de inventario", example = "12") @PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        if (inventario != null) {
            inventarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private EntityModel<InventarioResponse> toModel(Inventario inventario) {
        EntityModel<InventarioResponse> model = EntityModel.of(toResponse(inventario));
        model.add(linkTo(methodOn(InventarioController.class).obtenerMovimientoPorId(inventario.getId())).withSelfRel());
        model.add(linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario()).withRel("inventario"));

        if (inventario.getProducto() != null && inventario.getProducto().getId() != null) {
            model.add(linkTo(methodOn(ProductoController.class)
                    .obtenerProductoPorId(inventario.getProducto().getId())).withRel("producto"));
        }

        return model;
    }

    private InventarioResponse toResponse(Inventario inventario) {
        Long productoId = inventario.getProducto() != null ? inventario.getProducto().getId() : null;
        String productoNombre = inventario.getProducto() != null ? inventario.getProducto().getNombre() : null;

        return new InventarioResponse(
                inventario.getId(),
                inventario.getCantidad(),
                inventario.getTipoMovimiento(),
                inventario.getFechaMovimiento(),
                productoId,
                productoNombre
        );
    }

    private Inventario toEntity(InventarioRequest request) {
        Inventario inventario = new Inventario();
        inventario.setProducto(productoService.findById(request.productoId()));
        inventario.setCantidad(request.cantidad());
        inventario.setTipoMovimiento(request.tipoMovimiento());
        inventario.setFechaMovimiento(request.fechaMovimiento());
        return inventario;
    }
}
