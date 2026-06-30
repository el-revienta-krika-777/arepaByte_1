package com.projecto.ecomarket.config;

import com.projecto.ecomarket.model.Inventario;
import com.projecto.ecomarket.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final InventarioRepository inventarioRepository;

    @Override
    public void run(String... args) {
        if (inventarioRepository.count() > 0) {
            log.info(">>> InventarioDataInitializer: datos ya existentes, se omite carga.");
            return;
        }

        inventarioRepository.save(Inventario.builder()
                .productoId(1L)
                .stock(50)
                .stockMinimo(10)
                .disponible(true)
                .build());

        inventarioRepository.save(Inventario.builder()
                .productoId(2L)
                .stock(5)
                .stockMinimo(10) 
                .disponible(true)
                .build());

        inventarioRepository.save(Inventario.builder()
                .productoId(3L)
                .stock(0)
                .stockMinimo(5)
                .disponible(false) 
                .build());

        inventarioRepository.save(Inventario.builder()
                .productoId(4L)
                .stock(100)
                .stockMinimo(20)
                .disponible(true)
                .build());

        log.info(">>> InventarioDataInitializer: {} registros de inventario insertados.", inventarioRepository.count());
    }
}