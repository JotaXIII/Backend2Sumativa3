package com.minimarket.controller;

import com.minimarket.dto.DetalleVentaResponse;
import com.minimarket.dto.DetalleVentaRequest;
import com.minimarket.entity.DetalleVenta;
import com.minimarket.service.ProductoService;
import com.minimarket.service.DetalleVentaService;
import com.minimarket.service.VentaService;
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

/** Administra lineas de venta y relaciona productos con ventas registradas */
@RestController
@RequestMapping("/api/detalle-ventas")
@Tag(name = "Detalle de ventas")
public class DetalleVentaController {

    @Autowired
    private DetalleVentaService detalleVentaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private VentaService ventaService;

    @GetMapping
    @Operation(summary = "Lista los detalles de venta", description = "Retorna todas las lineas de detalle registradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public CollectionModel<EntityModel<DetalleVentaResponse>> listarDetalleVentas() {
        List<EntityModel<DetalleVentaResponse>> detalles = detalleVentaService.findAll().stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(detalles,
                linkTo(methodOn(DetalleVentaController.class).listarDetalleVentas()).withSelfRel());
    }

    @GetMapping("/venta/{ventaId}")
    @Operation(summary = "Lista detalles por venta", description = "Filtra las lineas de detalle asociadas a una venta especifica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public CollectionModel<EntityModel<DetalleVentaResponse>> listarDetallesPorVenta(
            @Parameter(description = "Identificador de la venta", example = "5") @PathVariable Long ventaId) {
        List<EntityModel<DetalleVentaResponse>> detalles = detalleVentaService.findByVentaId(ventaId).stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(detalles,
                linkTo(methodOn(DetalleVentaController.class).listarDetallesPorVenta(ventaId)).withSelfRel());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un detalle de venta por ID", description = "Retorna una linea de detalle individual si existe.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Detalle no encontrado")
    })
    public ResponseEntity<EntityModel<DetalleVentaResponse>> obtenerDetalleVentaPorId(
            @Parameter(description = "Identificador del detalle de venta", example = "21") @PathVariable Long id) {
        DetalleVenta detalleVenta = detalleVentaService.findById(id);
        return (detalleVenta != null) ? ResponseEntity.ok(toModel(detalleVenta)) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Crea un detalle de venta", description = "Registra una linea de detalle asociando venta, producto, cantidad y precio.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<EntityModel<DetalleVentaResponse>> guardarDetalleVenta(@Valid @RequestBody DetalleVentaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(detalleVentaService.save(toEntity(request))));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un detalle de venta", description = "Reemplaza los datos de una linea de detalle existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Detalle no encontrado")
    })
    public ResponseEntity<EntityModel<DetalleVentaResponse>> actualizarDetalleVenta(
            @Parameter(description = "Identificador del detalle de venta", example = "21") @PathVariable Long id,
            @Valid @RequestBody DetalleVentaRequest request) {
        DetalleVenta existente = detalleVentaService.findById(id);
        if (existente != null) {
            DetalleVenta detalleVenta = toEntity(request);
            detalleVenta.setId(id);
            return ResponseEntity.ok(toModel(detalleVentaService.save(detalleVenta)));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un detalle de venta", description = "Elimina una linea de detalle existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Detalle eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Detalle no encontrado")
    })
    public ResponseEntity<Void> eliminarDetalleVenta(
            @Parameter(description = "Identificador del detalle de venta", example = "21") @PathVariable Long id) {
        DetalleVenta detalleVenta = detalleVentaService.findById(id);
        if (detalleVenta != null) {
            detalleVentaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private DetalleVenta toEntity(DetalleVentaRequest request) {
        DetalleVenta detalleVenta = new DetalleVenta();
        detalleVenta.setVenta(ventaService.findById(request.ventaId()));
        detalleVenta.setProducto(productoService.findById(request.productoId()));
        detalleVenta.setCantidad(request.cantidad());
        detalleVenta.setPrecio(request.precio());
        return detalleVenta;
    }

    private EntityModel<DetalleVentaResponse> toModel(DetalleVenta detalleVenta) {
        EntityModel<DetalleVentaResponse> model = EntityModel.of(toResponse(detalleVenta));
        model.add(linkTo(methodOn(DetalleVentaController.class).obtenerDetalleVentaPorId(detalleVenta.getId())).withSelfRel());
        model.add(linkTo(methodOn(DetalleVentaController.class).listarDetalleVentas()).withRel("detalles"));

        if (detalleVenta.getVenta() != null && detalleVenta.getVenta().getId() != null) {
            model.add(linkTo(methodOn(VentaController.class)
                    .obtenerVentaPorId(detalleVenta.getVenta().getId())).withRel("venta"));
        }

        if (detalleVenta.getProducto() != null && detalleVenta.getProducto().getId() != null) {
            model.add(linkTo(methodOn(ProductoController.class)
                    .obtenerProductoPorId(detalleVenta.getProducto().getId())).withRel("producto"));
        }

        return model;
    }

    private DetalleVentaResponse toResponse(DetalleVenta detalleVenta) {
        Long ventaId = detalleVenta.getVenta() != null ? detalleVenta.getVenta().getId() : null;
        Long productoId = detalleVenta.getProducto() != null ? detalleVenta.getProducto().getId() : null;
        String productoNombre = detalleVenta.getProducto() != null ? detalleVenta.getProducto().getNombre() : null;

        return new DetalleVentaResponse(
                detalleVenta.getId(),
                ventaId,
                productoId,
                productoNombre,
                detalleVenta.getCantidad(),
                detalleVenta.getPrecio()
        );
    }
}
