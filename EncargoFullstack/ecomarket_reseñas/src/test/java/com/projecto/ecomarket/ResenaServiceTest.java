package com.projecto.ecomarket;

import com.projecto.ecomarket.client.ProductoClient;
import com.projecto.ecomarket.client.UsuarioClient;
import com.projecto.ecomarket.dto.ResenaRequestDTO;
import com.projecto.ecomarket.dto.ResenaResponseDTO;
import com.projecto.ecomarket.dto.UsuarioDTO;
import com.projecto.ecomarket.model.Resena;
import com.projecto.ecomarket.repository.ResenaRepository;
import com.projecto.ecomarket.service.ResenaService;

import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("ResenaService - Pruebas Unitarias")
class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private ProductoClient productoClient;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private ResenaService resenaService;

    @Test
    @DisplayName("crearResena: guarda y retorna el DTO cuando producto y usuario existen")
    void crearResena_datosValidos_guardaYRetornaDTO() {
        ResenaRequestDTO request = TestDataFactory.unaResenaRequest();
        UsuarioDTO usuarioInfo = TestDataFactory.unUsuarioDTO();
        Resena resenaGuardada = TestDataFactory.unaResena();

        // Simulamos que ambos clientes validan correctamente
        when(productoClient.obtenerProducto(request.getProductoId())).thenReturn(new Object());
        when(usuarioClient.obtenerUsuario(request.getUsuarioId())).thenReturn(usuarioInfo);
        when(resenaRepository.save(any(Resena.class))).thenReturn(resenaGuardada);

        ResenaResponseDTO resultado = resenaService.crearResena(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCalificacion()).isEqualTo(resenaGuardada.getCalificacion());
        assertThat(resultado.getNombreUsuario()).isEqualTo(usuarioInfo.getNombre());
        verify(resenaRepository).save(any(Resena.class));
    }

    @Test
    @DisplayName("crearResena: lanza excepcion cuando el producto o usuario no existe (Falla Feign)")
    void crearResena_entidadNoExiste_lanzaExcepcion() {
        ResenaRequestDTO request = TestDataFactory.unaResenaRequest();

        // Simulamos que el microservicio de productos responde un 404
        when(productoClient.obtenerProducto(request.getProductoId()))
                .thenThrow(mock(FeignException.NotFound.class));

        assertThatThrownBy(() -> resenaService.crearResena(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El Producto o Usuario no existen");

        verify(usuarioClient, never()).obtenerUsuario(anyLong()); // Nunca llega a verificar el usuario
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    @Test
    @DisplayName("obtenerTodas: mapea correctamente y maneja el fallback si UsuarioClient falla al enriquecer DTO")
    void obtenerTodas_fallaUsuarioClient_retornaNombrePorDefecto() {
        Resena resena = TestDataFactory.unaResena();
        
        when(resenaRepository.findAll()).thenReturn(List.of(resena));
        // Simulamos que al hacer el mapeo, el microservicio de usuarios falla
        when(usuarioClient.obtenerUsuario(resena.getUsuarioId())).thenThrow(new RuntimeException("Timeout"));

        List<ResenaResponseDTO> resultados = resenaService.obtenerTodas();

        assertThat(resultados).hasSize(1);
        // Validamos la regla de negocio de resiliencia del Try/Catch en mapToDTO()
        assertThat(resultados.get(0).getNombreUsuario()).isEqualTo("Usuario no disponible");
    }

    @Test
    @DisplayName("obtenerPorProducto: retorna lista de reseñas de un producto")
    void obtenerPorProducto_existente_retornaLista() {
        Long productoId = 15L;
        Resena resena = TestDataFactory.unaResena();
        UsuarioDTO usuario = TestDataFactory.unUsuarioDTO();

        when(resenaRepository.findByProductoId(productoId)).thenReturn(List.of(resena));
        when(usuarioClient.obtenerUsuario(anyLong())).thenReturn(usuario);

        List<ResenaResponseDTO> resultados = resenaService.obtenerPorProducto(productoId);

        assertThat(resultados).isNotEmpty();
        verify(resenaRepository).findByProductoId(productoId);
    }

    @Test
    @DisplayName("obtenerPromedio: retorna el valor del repositorio cuando hay calificaciones")
    void obtenerPromedio_conCalificaciones_retornaPromedio() {
        Long productoId = 1L;
        when(resenaRepository.getPromedioCalificacion(productoId)).thenReturn(4.5);

        Double promedio = resenaService.obtenerPromedio(productoId);

        assertThat(promedio).isEqualTo(4.5);
    }

    @Test
    @DisplayName("obtenerPromedio: retorna 0.0 cuando el repositorio devuelve nulo")
    void obtenerPromedio_sinCalificaciones_retornaCero() {
        Long productoId = 1L;
        when(resenaRepository.getPromedioCalificacion(productoId)).thenReturn(null);

        Double promedio = resenaService.obtenerPromedio(productoId);

        assertThat(promedio).isEqualTo(0.0);
    }

    @Test
    @DisplayName("eliminarResena: elimina correctamente cuando el id existe")
    void eliminarResena_existe_elimina() {
        Long resenaId = 10L;
        when(resenaRepository.existsById(resenaId)).thenReturn(true);

        resenaService.eliminarResena(resenaId);

        verify(resenaRepository).deleteById(resenaId);
    }

    @Test
    @DisplayName("eliminarResena: lanza excepcion cuando la reseña no existe")
    void eliminarResena_noExiste_lanzaExcepcion() {
        Long resenaId = 10L;
        when(resenaRepository.existsById(resenaId)).thenReturn(false);

        assertThatThrownBy(() -> resenaService.eliminarResena(resenaId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("La reseña no existe");

        verify(resenaRepository, never()).deleteById(anyLong());
    }
}