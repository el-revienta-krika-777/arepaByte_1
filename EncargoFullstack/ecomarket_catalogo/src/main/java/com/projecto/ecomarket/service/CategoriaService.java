package com.projecto.ecomarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.projecto.ecomarket.model.Categoria;
import com.projecto.ecomarket.repository.CategoriaRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<Categoria> obtenerTodas() { return categoriaRepository.findAll(); }
    public Optional<Categoria> obtenerPorId(Long id) { return categoriaRepository.findById(id); }
    public Categoria guardar(Categoria c) { return categoriaRepository.save(c); }
    public void eliminar(Long id) { categoriaRepository.deleteById(id); }
}
