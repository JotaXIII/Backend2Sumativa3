package com.minimarket.controller;

import com.minimarket.dto.VentaRequest;
import com.minimarket.dto.VentaResponse;
import com.minimarket.entity.Venta;
import com.minimarket.service.UsuarioService;
import com.minimarket.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Collections;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/** Administra ventas asociadas a usuarios y publica enlaces de navegacion */
@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Lista las ventas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ventas obtenidas correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public CollectionModel<EntityModel<VentaResponse>> listarVentas() {
        List<EntityModel<VentaResponse>> ventas = ventaService.findAll().stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(ventas,
                linkTo(methodOn(VentaController.class).listarVentas()).withSelfRel());
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Lista ventas por usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ventas obtenidas correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public CollectionModel<EntityModel<VentaResponse>> listarVentasPorUsuario(@PathVariable Long usuarioId) {
        List<EntityModel<VentaResponse>> ventas = ventaService.findByUsuarioId(usuarioId).stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(ventas,
                linkTo(methodOn(VentaController.class).listarVentasPorUsuario(usuarioId)).withSelfRel());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una venta por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta obtenida correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    public ResponseEntity<EntityModel<VentaResponse>> obtenerVentaPorId(@PathVariable Long id) {
        Venta venta = ventaService.findById(id);
        if (venta == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toModel(venta));
    }

    @PostMapping
    @Operation(summary = "Crea una venta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venta creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<VentaResponse> guardarVenta(@Valid @RequestBody VentaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(ventaService.save(toEntity(request))));
    }

    private EntityModel<VentaResponse> toModel(Venta venta) {
        EntityModel<VentaResponse> model = EntityModel.of(toResponse(venta));
        model.add(linkTo(methodOn(VentaController.class).obtenerVentaPorId(venta.getId())).withSelfRel());
        model.add(linkTo(methodOn(VentaController.class).listarVentas()).withRel("ventas"));
        model.add(linkTo(methodOn(DetalleVentaController.class)
                .listarDetallesPorVenta(venta.getId())).withRel("detalles"));

        if (venta.getUsuario() != null && venta.getUsuario().getId() != null) {
            model.add(linkTo(methodOn(UsuarioController.class)
                    .obtenerUsuarioPorId(venta.getUsuario().getId())).withRel("usuario"));
        }

        return model;
    }

    private VentaResponse toResponse(Venta venta) {
        Long usuarioId = venta.getUsuario() != null ? venta.getUsuario().getId() : null;
        String username = venta.getUsuario() != null ? venta.getUsuario().getUsername() : null;
        List<Long> detalleIds = venta.getDetalles() != null
                ? venta.getDetalles().stream().map(detalle -> detalle.getId()).toList()
                : Collections.emptyList();

        return new VentaResponse(venta.getId(), venta.getFecha(), usuarioId, username, detalleIds);
    }

    private Venta toEntity(VentaRequest request) {
        Venta venta = new Venta();
        venta.setUsuario(usuarioService.findById(request.usuarioId()).orElse(null));
        venta.setFecha(request.fecha());
        return venta;
    }
}
