package com.projecto.ecomarket;

import java.time.LocalDateTime;
import net.datafaker.Faker;
import com.projecto.ecomarket.dto.FavoritoRequestDTO;
import com.projecto.ecomarket.dto.UsuarioDTO;
import com.projecto.ecomarket.model.Favorito;

public class TestDataFactory {

    private static final Faker faker = new Faker();

    public static Favorito unFavorito(Long id, Long productoId, String usuario) {
        Favorito favorito = new Favorito();
        favorito.setId(id != null ? id : faker.number().numberBetween(1L, 999L));
        favorito.setProductoId(productoId);
        favorito.setNombreProducto(faker.commerce().productName());
        favorito.setUsuario(usuario);
        favorito.setFechaAgregado(LocalDateTime.now());
        return favorito;
    }

    public static FavoritoRequestDTO unFavoritoRequest(Long productoId, String usuario) {
        FavoritoRequestDTO dto = new FavoritoRequestDTO();
        dto.setProductoId(productoId);
        dto.setUsuario(usuario);
        return dto;
    }

    public static UsuarioDTO unUsuarioDTO(String nombre) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(faker.number().numberBetween(1L, 999L));
        dto.setNombre(nombre);
        dto.setEmail(faker.internet().emailAddress());
        dto.setActivo(true);
        return dto;
    }
}