package com.minimarket.controller;

import com.minimarket.dto.DetalleVentaResponse;
import com.minimarket.dto.DetalleVentaRequest;
import com.minimarket.entity.DetalleVenta;
import com.minimarket.service.ProductoService;
import com.minimarket.service.DetalleVentaService;
import com.minimarket.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Administra lineas de venta y relaciona productos con ventas registradas */
@RestController
@RequestMapping("/api/detalle-ventas")
public class DetalleVentaController {

    @Autowired
    private DetalleVentaService detalleVentaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private VentaService ventaService;

    @GetMapping
    public List<DetalleVentaResponse> listarDetalleVentas() {
        return detalleVentaService.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/venta/{ventaId}")
    public List<DetalleVentaResponse> listarDetallesPorVenta(@PathVariable Long ventaId) {
        return detalleVentaService.findByVentaId(ventaId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleVentaResponse> obtenerDetalleVentaPorId(@PathVariable Long id) {
        DetalleVenta detalleVenta = detalleVentaService.findById(id);
        return (detalleVenta != null) ? ResponseEntity.ok(toResponse(detalleVenta)) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public DetalleVentaResponse guardarDetalleVenta(@Valid @RequestBody DetalleVentaRequest request) {
        return toResponse(detalleVentaService.save(toEntity(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleVentaResponse> actualizarDetalleVenta(@PathVariable Long id, @Valid @RequestBody DetalleVentaRequest request) {
        DetalleVenta existente = detalleVentaService.findById(id);
        if (existente != null) {
            DetalleVenta detalleVenta = toEntity(request);
            detalleVenta.setId(id);
            return ResponseEntity.ok(toResponse(detalleVentaService.save(detalleVenta)));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalleVenta(@PathVariable Long id) {
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
