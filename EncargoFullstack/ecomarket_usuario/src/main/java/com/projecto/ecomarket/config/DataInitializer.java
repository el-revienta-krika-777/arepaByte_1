package com.projecto.ecomarket.config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.projecto.ecomarket.model.Usuario;
import com.projecto.ecomarket.repository.UsuarioRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            log.info(">>> DataInitializer: datos ya existentes, se omite carga.");
            return;
        }

        usuarioRepository.save(new Usuario(null, "adonis",    "adonis@ecomarktet.com",    "555-0001", true));
        usuarioRepository.save(new Usuario(null, "pablo", "pablo@ecomarktet.com", "555-0002", true));
        usuarioRepository.save(new Usuario(null, "diego",  "diego@ecomarktet.com",  "555-0003", true));
        usuarioRepository.save(new Usuario(null, "sting",   "sting@ecomarktet.com",   "555-0004", true));

        log.info(">>> DataInitializer: {} usuarios insertados.", usuarioRepository.count());
    }
}