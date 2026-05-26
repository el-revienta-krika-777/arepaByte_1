package com.projecto.ecomarket.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.projecto.ecomarket.model.Producto;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoriaId(Long categoriaId);
}

