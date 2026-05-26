package com.projecto.ecomarket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projecto.ecomarket.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
