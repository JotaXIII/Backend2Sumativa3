package com.minimarket.config;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/** Crea credenciales minimas para ejecutar Swagger, Postman y smoke tests locales. */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedSecurityData(RolRepository rolRepository, UsuarioService usuarioService) {
        return args -> {
            Rol admin = findOrCreateRole(rolRepository, "ADMIN");
            findOrCreateRole(rolRepository, "ENCARGADO");
            findOrCreateRole(rolRepository, "USER");

            usuarioService.findByUsername("admin").orElseGet(() -> {
                Usuario usuario = new Usuario();
                usuario.setUsername("admin");
                usuario.setPassword("admin123");
                usuario.setRoles(Set.of(admin));
                return usuarioService.save(usuario);
            });
        };
    }

    private Rol findOrCreateRole(RolRepository rolRepository, String nombre) {
        return rolRepository.findByNombre(nombre).orElseGet(() -> rolRepository.save(new Rol(nombre)));
    }
}
