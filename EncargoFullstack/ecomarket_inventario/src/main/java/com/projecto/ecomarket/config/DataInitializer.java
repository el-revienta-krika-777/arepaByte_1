package com.projecto.ecomarket.config; // O el paquete donde guardes tus configs

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
        // 1. Verificamos si ya hay datos para no duplicar en cada reinicio
        if (inventarioRepository.count() > 0) {
            log.info(">>> InventarioDataInitializer: datos ya existentes, se omite carga.");
            return;
        }

        // 2. Insertamos datos de prueba usando el Builder del modelo
        // Nota: El productoId debe coincidir con IDs de productos que existan en tu otro micro
        inventarioRepository.save(Inventario.builder()
                .productoId(1L)
                .stock(50)
                .stockMinimo(10)
                .disponible(true)
                .build());

        inventarioRepository.save(Inventario.builder()
                .productoId(2L)
                .stock(5)
                .stockMinimo(10) // Este producto saldría con stock bajo
                .disponible(true)
                .build());

        inventarioRepository.save(Inventario.builder()
                .productoId(3L)
                .stock(0)
                .stockMinimo(5)
                .disponible(false) // Sin stock
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