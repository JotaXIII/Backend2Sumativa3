package com.minimarket.controller;

import com.minimarket.dto.RolResponse;
import com.minimarket.dto.UsuarioRequest;
import com.minimarket.dto.UsuarioResponse;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/** Administra usuarios y roles mediante DTOs de entrada y respuesta */
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolRepository rolRepository;

    @GetMapping
    @Operation(summary = "Lista los usuarios registrados", description = "Retorna usuarios sin exponer contrasenas y con enlaces hacia roles y ventas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public CollectionModel<EntityModel<UsuarioResponse>> listarUsuarios() {
        List<EntityModel<UsuarioResponse>> usuarios = usuarioService.findAll().stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withSelfRel());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un usuario por ID", description = "Retorna un usuario individual con enlaces HATEOAS a roles, ventas y coleccion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<EntityModel<UsuarioResponse>> obtenerUsuarioPorId(
            @Parameter(description = "Identificador del usuario", example = "3") @PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toModel(usuario.get()));
    }

    @GetMapping("/{id}/roles")
    @Operation(summary = "Lista los roles de un usuario", description = "Permite revisar los roles asignados a un usuario especifico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<CollectionModel<RolResponse>> listarRolesPorUsuario(
            @Parameter(description = "Identificador del usuario", example = "3") @PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Set<RolResponse> roles = usuario.get().getRoles() != null
                ? usuario.get().getRoles().stream().map(this::toRolResponse).collect(Collectors.toSet())
                : Set.of();

        return ResponseEntity.ok(CollectionModel.of(roles,
                linkTo(methodOn(UsuarioController.class).listarRolesPorUsuario(id)).withSelfRel()));
    }

    @PostMapping
    @Operation(summary = "Crea un usuario", description = "Registra un usuario con contrasena cifrada y roles existentes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<EntityModel<UsuarioResponse>> guardarUsuario(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(usuarioService.save(toEntity(request))));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un usuario", description = "Actualiza credenciales y roles de un usuario existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<EntityModel<UsuarioResponse>> actualizarUsuario(
            @Parameter(description = "Identificador del usuario", example = "3") @PathVariable Long id,
            @Valid @RequestBody UsuarioRequest request) {
        Optional<Usuario> usuarioExistente = usuarioService.findById(id);
        if (usuarioExistente.isPresent()) {
            Usuario usuario = toEntity(request);
            usuario.setId(id);
            return ResponseEntity.ok(toModel(usuarioService.save(usuario)));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un usuario", description = "Elimina un usuario existente si no existen dependencias que lo impidan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> eliminarUsuario(
            @Parameter(description = "Identificador del usuario", example = "3") @PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isPresent()) {
            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private EntityModel<UsuarioResponse> toModel(Usuario usuario) {
        EntityModel<UsuarioResponse> model = EntityModel.of(toResponse(usuario));
        model.add(linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(usuario.getId())).withSelfRel());
        model.add(linkTo(methodOn(UsuarioController.class).listarUsuarios()).withRel("usuarios"));
        model.add(linkTo(methodOn(UsuarioController.class).listarRolesPorUsuario(usuario.getId())).withRel("roles"));
        model.add(linkTo(methodOn(VentaController.class).listarVentasPorUsuario(usuario.getId())).withRel("ventas"));

        return model;
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        Set<String> roles = usuario.getRoles() != null
                ? usuario.getRoles().stream().map(Rol::getNombre).collect(Collectors.toSet())
                : Set.of();

        return new UsuarioResponse(usuario.getId(), usuario.getUsername(), roles);
    }

    private RolResponse toRolResponse(Rol rol) {
        return new RolResponse(rol.getId(), rol.getNombre());
    }

    private Usuario toEntity(UsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setUsername(request.username());
        usuario.setPassword(request.password());
        if (request.roleIds() != null) {
            usuario.setRoles(request.roleIds().stream()
                    .map(rolRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet()));
        }
        return usuario;
    }
}
