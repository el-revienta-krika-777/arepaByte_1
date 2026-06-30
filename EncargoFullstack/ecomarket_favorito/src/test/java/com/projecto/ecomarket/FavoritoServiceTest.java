package com.projecto.ecomarket;

import com.projecto.ecomarket.client.CatalogoClient;
import com.projecto.ecomarket.client.UsuarioClient;
import com.projecto.ecomarket.dto.*;
import com.projecto.ecomarket.model.Favorito;
import com.projecto.ecomarket.repository.FavoritoRepository;
import com.projecto.ecomarket.service.FavoritoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FavoritoService - Pruebas Unitarias")
class FavoritoServiceTest {

    @Mock private FavoritoRepository favoritoRepository;
    @Mock private CatalogoClient catalogoClient;
    @Mock private UsuarioClient usuarioClient;

    @InjectMocks private FavoritoService favoritoService;

    @Test
    @DisplayName("agregar: crea el favorito si los datos son válidos")
    void agregar_datosValidos_retornaDTO() {
        Long productoId = 1L;
        String usuario = "testUser";
        
        FavoritoRequestDTO request = TestDataFactory.unFavoritoRequest(productoId, usuario);
        UsuarioDTO user = TestDataFactory.unUsuarioDTO(usuario);
        ProductoDTO prod = new ProductoDTO();
        prod.setNombre("Producto Test");
        
        when(usuarioClient.buscarPorNombre(usuario)).thenReturn(Optional.of(user));
        when(catalogoClient.buscarProducto(productoId)).thenReturn(Optional.of(prod));
        when(favoritoRepository.existsByProductoIdAndUsuario(productoId, usuario)).thenReturn(false);
        
        Favorito saved = TestDataFactory.unFavorito(1L, productoId, usuario);
        when(favoritoRepository.save(any(Favorito.class))).thenReturn(saved);

        FavoritoResponseDTO resultado = favoritoService.agregar(request);

        assertThat(resultado.getUsuario()).isEqualTo(usuario);
        verify(favoritoRepository).save(any(Favorito.class));
    }

    @Test
    @DisplayName("agregar: lanza excepción si el usuario no existe")
    void agregar_usuarioInexistente_lanzaExcepcion() {
        FavoritoRequestDTO request = TestDataFactory.unFavoritoRequest(1L, "noExiste");
        when(usuarioClient.buscarPorNombre("noExiste")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoritoService.agregar(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no existe");
    }

    @Test
    @DisplayName("eliminar: borra el registro correctamente")
    void eliminar_idExistente_borraFavorito() {
        Long id = 10L;
        Favorito fav = new Favorito();
        when(favoritoRepository.findById(id)).thenReturn(Optional.of(fav));

        favoritoService.eliminar(id);

        verify(favoritoRepository).delete(fav);
    }
}
