package com.projecto.ecomarket;

import com.projecto.ecomarket.client.ProductoClient;
import com.projecto.ecomarket.dto.InventarioRequestDTO;
import com.projecto.ecomarket.dto.InventarioResponseDTO;
import com.projecto.ecomarket.dto.ProductoDTO;
import com.projecto.ecomarket.model.Inventario;
import com.projecto.ecomarket.repository.InventarioRepository;
import com.projecto.ecomarket.service.InventarioService;

import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventarioService - Pruebas Unitarias")
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private InventarioService inventarioService;

    @Test
    @DisplayName("obtenerTodoStock: retorna la lista completa mapeada a DTO")
    void obtenerTodoStock_conDatos_retornaListaDTO() {
        Inventario inv = TestDataFactory.unInventario(1L);
        when(inventarioRepository.findAll()).thenReturn(List.of(inv));

        List<InventarioResponseDTO> resultado = inventarioService.obtenerTodoStock();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getProductoId()).isEqualTo(inv.getProductoId());
        assertThat(resultado.get(0).getStock()).isEqualTo(inv.getStock());
    }

    @Test
    @DisplayName("obtenerPorProductoId: retorna DTO cuando el inventario existe")
    void obtenerPorProductoId_existente_retornaDTO() {
        Long productoId = 5L;
        Inventario inv = TestDataFactory.unInventario(productoId);
        when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(inv));

        InventarioResponseDTO resultado = inventarioService.obtenerPorProductoId(productoId);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getProductoId()).isEqualTo(productoId);
    }

    @Test
    @DisplayName("hayStock: retorna true cuando el stock es mayor o igual a lo solicitado")
    void hayStock_stockSuficiente_retornaTrue() {
        Long productoId = 5L;
        Inventario inv = TestDataFactory.unInventario(productoId);
        inv.setStock(10); // Aseguramos un stock específico
        when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(inv));

        boolean resultado = inventarioService.hayStock(productoId, 5);

        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("hayStock: retorna false cuando lo solicitado supera el stock")
    void hayStock_stockInsuficiente_retornaFalse() {
        Long productoId = 5L;
        Inventario inv = TestDataFactory.unInventario(productoId);
        inv.setStock(2); // Asignamos stock menor al solicitado
        when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(inv));

        boolean resultado = inventarioService.hayStock(productoId, 5);

        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("crearInventario: crea exitosamente cuando el producto existe y no tiene inventario previo")
    void crearInventario_datosValidos_creaYRetornaDTO() {
        Long productoId = 10L;
        InventarioRequestDTO request = TestDataFactory.unInventarioRequest(productoId);
        ProductoDTO producto = TestDataFactory.unProductoDTO(productoId);
        Inventario inventarioGuardado = TestDataFactory.unInventario(productoId);
        inventarioGuardado.setStock(request.getStock());

        when(productoClient.obtenerProductoPorId(productoId)).thenReturn(producto);
        when(inventarioRepository.existsByProductoId(productoId)).thenReturn(false);
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventarioGuardado);

        InventarioResponseDTO resultado = inventarioService.crearInventario(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getProductoId()).isEqualTo(productoId);
        verify(inventarioRepository).save(any(Inventario.class));
    }

    @Test
    @DisplayName("crearInventario: lanza excepción si el producto no existe en el catálogo")
    void crearInventario_productoNoExiste_lanzaExcepcion() {
        Long productoId = 99L;
        InventarioRequestDTO request = TestDataFactory.unInventarioRequest(productoId);

        when(productoClient.obtenerProductoPorId(productoId))
                .thenThrow(mock(FeignException.NotFound.class));

        assertThatThrownBy(() -> inventarioService.crearInventario(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El producto no existe en el Catálogo");

        verify(inventarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("crearInventario: lanza excepción si el producto ya tiene inventario creado")
    void crearInventario_inventarioDuplicado_lanzaExcepcion() {
        Long productoId = 10L;
        InventarioRequestDTO request = TestDataFactory.unInventarioRequest(productoId);

        when(productoClient.obtenerProductoPorId(productoId)).thenReturn(new Object());
        when(inventarioRepository.existsByProductoId(productoId)).thenReturn(true);

        assertThatThrownBy(() -> inventarioService.crearInventario(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ya tiene un registro de inventario");

        verify(inventarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("disminuirStock: resta el stock y actualiza disponibilidad si alcanza para suplir la cantidad")
    void disminuirStock_stockSuficiente_actualizaYRetornaDTO() {
        Long productoId = 1L;
        Inventario inv = TestDataFactory.unInventario(productoId);
        inv.setStock(10);
        
        when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(inv));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inv);

        InventarioResponseDTO resultado = inventarioService.disminuirStock(productoId, 3);

        // Validamos la matemática interna
        assertThat(inv.getStock()).isEqualTo(7);
        assertThat(inv.getDisponible()).isTrue();
        assertThat(resultado.getStock()).isEqualTo(7);
        verify(inventarioRepository).save(inv);
    }

    @Test
    @DisplayName("disminuirStock: lanza excepción si la cantidad solicitada supera el stock actual")
    void disminuirStock_stockInsuficiente_lanzaExcepcion() {
        Long productoId = 1L;
        Inventario inv = TestDataFactory.unInventario(productoId);
        inv.setStock(2);
        
        when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(inv));

        assertThatThrownBy(() -> inventarioService.disminuirStock(productoId, 5))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Stock insuficiente");

        verify(inventarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("aumentarStock: suma el stock y asegura que quede marcado como disponible")
    void aumentarStock_existe_actualizaYRetornaDTO() {
        Long productoId = 1L;
        Inventario inv = TestDataFactory.unInventario(productoId);
        inv.setStock(0);
        inv.setDisponible(false);

        when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(inv));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inv);

        InventarioResponseDTO resultado = inventarioService.aumentarStock(productoId, 15);

        assertThat(inv.getStock()).isEqualTo(15);
        assertThat(inv.getDisponible()).isTrue();
        assertThat(resultado.getStock()).isEqualTo(15);
        assertThat(resultado.getDisponible()).isTrue();
        
        verify(inventarioRepository).save(inv);
    }

    @Test
    @DisplayName("eliminar: borra el registro si el id existe")
    void eliminar_existe_eliminaInventario() {
        Long invId = 20L;
        when(inventarioRepository.existsById(invId)).thenReturn(true);

        inventarioService.eliminar(invId);

        verify(inventarioRepository).deleteById(invId);
    }

    @Test
    @DisplayName("eliminar: lanza excepción si el inventario no se encuentra por su id")
    void eliminar_noExiste_lanzaExcepcion() {
        Long invId = 99L;
        when(inventarioRepository.existsById(invId)).thenReturn(false);

        assertThatThrownBy(() -> inventarioService.eliminar(invId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no existe");

        verify(inventarioRepository, never()).deleteById(anyLong());
    }
}