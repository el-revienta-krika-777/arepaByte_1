package com.projecto.ecomarket.config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.projecto.ecomarket.model.Categoria;
import com.projecto.ecomarket.model.Producto;
import com.projecto.ecomarket.repository.CategoriaRepository;
import com.projecto.ecomarket.repository.ProductoRepository;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

@Override
public void run(String... args) {

    if (categoriaRepository.count() > 0) {
        log.info(">>> DataInitializer: datos ya existentes, se omite carga.");
        return;
    }

    Categoria reutilizables = categoriaRepository.save(new Categoria(null, "Productos Reutilizables", "Articulos reutilizables para reducir residuos"));

    Categoria biodegradables = categoriaRepository.save(new Categoria(null, "Productos Biodegradables", "Productos amigables con el medio ambiente"));

    Categoria organicos = categoriaRepository.save(
            new Categoria(null, "Alimentos Organicos",
                    "Alimentos naturales y sustentables"));

    Categoria higiene = categoriaRepository.save(
            new Categoria(null, "Higiene Sustentable",
                    "Productos de cuidado personal ecologicos"));

    productoRepository.save(new Producto(
            null,
            "Botella Reutilizable de Acero",
            "ECO-001",
            new BigDecimal("12.99"),
            reutilizables));

    productoRepository.save(new Producto(
            null,
            "Set de Bombillas Metalicas",
            "ECO-002",
            new BigDecimal("8.50"),
            reutilizables));

    productoRepository.save(new Producto(
            null,
            "Bolsas Biodegradables",
            "ECO-003",
            new BigDecimal("5.99"),
            biodegradables));

    productoRepository.save(new Producto(
            null,
            "Cepillo Dental de Bambu",
            "ECO-004",
            new BigDecimal("3.75"),
            higiene));

    productoRepository.save(new Producto(
            null,
            "Shampoo Solido Natural",
            "ECO-005",
            new BigDecimal("9.90"),
            higiene));

    productoRepository.save(new Producto(
            null,
            "Cafe Organico Premium",
            "ECO-006",
            new BigDecimal("14.50"),
            organicos));

    productoRepository.save(new Producto(
            null,
            "Miel Organica Natural",
            "ECO-007",
            new BigDecimal("11.25"),
            organicos));

    productoRepository.save(new Producto(
            null,
            "Contenedor Compostable",
            "ECO-008",
            new BigDecimal("6.40"),
            biodegradables));

    log.info(">>> DataInitializer: {} categorias y {} productos insertados.",
            categoriaRepository.count(),
            productoRepository.count());
}

}
