package com.projecto.ecomarket.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.projecto.ecomarket.dto.UsuarioDTO;

@FeignClient(name = "usuario-service", url = "http://localhost:8080") 
public interface UsuarioClient {

    @GetMapping("/api/usuarios/{id}")
    UsuarioDTO obtenerUsuario(@PathVariable("id") Long id); 
}