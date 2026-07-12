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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<DetalleVentaResponse> listarDetalleVentas() {
        return detalleVentaService.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/venta/{ventaId}")
    @Operation(summary = "Lista detalles por venta", description = "Filtra las lineas de detalle asociadas a una venta especifica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public List<DetalleVentaResponse> listarDetallesPorVenta(
            @Parameter(description = "Identificador de la venta", example = "5") @PathVariable Long ventaId) {
        return detalleVentaService.findByVentaId(ventaId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un detalle de venta por ID", description = "Retorna una linea de detalle individual si existe.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Detalle no encontrado")
    })
    public ResponseEntity<DetalleVentaResponse> obtenerDetalleVentaPorId(
            @Parameter(description = "Identificador del detalle de venta", example = "21") @PathVariable Long id) {
        DetalleVenta detalleVenta = detalleVentaService.findById(id);
        return (detalleVenta != null) ? ResponseEntity.ok(toResponse(detalleVenta)) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Crea un detalle de venta", description = "Registra una linea de detalle asociando venta, producto, cantidad y precio.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public DetalleVentaResponse guardarDetalleVenta(@Valid @RequestBody DetalleVentaRequest request) {
        return toResponse(detalleVentaService.save(toEntity(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un detalle de venta", description = "Reemplaza los datos de una linea de detalle existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Detalle no encontrado")
    })
    public ResponseEntity<DetalleVentaResponse> actualizarDetalleVenta(
            @Parameter(description = "Identificador del detalle de venta", example = "21") @PathVariable Long id,
            @Valid @RequestBody DetalleVentaRequest request) {
        DetalleVenta existente = detalleVentaService.findById(id);
        if (existente != null) {
            DetalleVenta detalleVenta = toEntity(request);
            detalleVenta.setId(id);
            return ResponseEntity.ok(toResponse(detalleVentaService.save(detalleVenta)));
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

    private DetalleVenta toEntity(DetalleVentaRequest request) {
        DetalleVenta detalleVenta = new DetalleVenta();
        detalleVenta.setVenta(ventaService.findById(request.ventaId()));
        detalleVenta.setProducto(productoService.findById(request.productoId()));
        detalleVenta.setCantidad(request.cantidad());
        detalleVenta.setPrecio(request.precio());
        return detalleVenta;
    }
}
