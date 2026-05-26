package com.projecto.ecomarket.repository;

import com.projecto.ecomarket.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    List<Favorito> findByUsuario(String usuario);

    List<Favorito> findByProductoId(Long productoId);

    boolean existsByProductoIdAndUsuario(Long productoId, String usuario);
}
