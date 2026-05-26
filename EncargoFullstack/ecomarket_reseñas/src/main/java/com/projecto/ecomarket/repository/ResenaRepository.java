package com.projecto.ecomarket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.projecto.ecomarket.model.Resena;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByProductoId(Long productoId);

    List<Resena> findByUsuarioId(Long usuarioId);
    
    // Para calcular el promedio de estrellas de un producto
    @Query("SELECT AVG(r.calificacion) FROM Resena r WHERE r.productoId = :productoId")
    Double getPromedioCalificacion(Long productoId);
}
