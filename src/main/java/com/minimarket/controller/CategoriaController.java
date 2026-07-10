package com.minimarket.controller;

import com.minimarket.dto.CategoriaRequest;
import com.minimarket.dto.CategoriaResponse;
import com.minimarket.entity.Categoria;
import com.minimarket.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Administra categorias usando DTOs para evitar exponer entidades JPA */
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public List<CategoriaResponse> listarCategorias() {
        return categoriaService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> obtenerCategoriaPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        return (categoria != null) ? ResponseEntity.ok(toResponse(categoria)) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public CategoriaResponse guardarCategoria(@Valid @RequestBody CategoriaRequest request) {
        return toResponse(categoriaService.save(toEntity(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> actualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        Categoria categoriaExistente = categoriaService.findById(id);
        if (categoriaExistente != null) {
            Categoria categoria = toEntity(request);
            categoria.setId(id);
            return ResponseEntity.ok(toResponse(categoriaService.save(categoria)));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
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

    private CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNombre());
    }
}
