package com.projecto.ecomarket;

import com.projecto.ecomarket.client.CatalogoClient;
import com.projecto.ecomarket.dto.PedidoRequestDTO;
import com.projecto.ecomarket.dto.PedidoResponseDTO;
import com.projecto.ecomarket.dto.ProductoDTO;
import com.projecto.ecomarket.model.Pedido;
import com.projecto.ecomarket.repository.PedidoRepository;
import com.projecto.ecomarket.service.PedidoService;

import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CatalogoClient catalogoClient;

    @InjectMocks
    private PedidoService pedidoService;

    @Nested
    @DisplayName("Pruebas para Crear Pedidos")
    class CrearPedidoTests {

        @Test
        @DisplayName("Debe crear un pedido exitosamente comunicándose con el catálogo")
        void crearPedidoExitoso() {
            // Arrange
            Long productoId = 101L;
            String cliente = "Ana Gomez";
            Integer cantidad = 2;
            
            PedidoRequestDTO request = TestDataFactory.unPedidoRequestDTO(productoId, cliente, cantidad);
            ProductoDTO productoMock = TestDataFactory.unProductoDTO(productoId);
            
            when(catalogoClient.obtenerProducto(productoId)).thenReturn(productoMock);
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> {
                Pedido p = i.getArgument(0);
                p.setId(1L); 
                return p;
            });

            // Act
            PedidoResponseDTO response = pedidoService.crear(request);

            // Assert
            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals(productoId, response.getProductoId());
            assertEquals(cliente, response.getCliente());
            assertEquals(cantidad, response.getCantidad());
            assertEquals(productoMock.getNombre(), response.getNombreProducto());
            assertEquals(productoMock.getPrecio(), response.getPrecioUnitario());
            
            // Verificamos que el total se haya calculado correctamente (Precio * Cantidad)
            BigDecimal totalEsperado = productoMock.getPrecio().multiply(BigDecimal.valueOf(cantidad));
            assertEquals(totalEsperado, response.getTotal());
            
            verify(catalogoClient, times(1)).obtenerProducto(productoId);
            verify(pedidoRepository, times(1)).save(any(Pedido.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción si el producto no existe en el catálogo (404)")
        void crearPedidoProductoNoEncontrado() {
            // Arrange
            Long productoId = 99L;
            PedidoRequestDTO request = TestDataFactory.unPedidoRequestDTO(productoId, "Juan Perez", 1);
            
            when(catalogoClient.obtenerProducto(productoId)).thenThrow(mock(FeignException.NotFound.class));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                pedidoService.crear(request);
            });

            assertEquals("El producto con id 99 no existe en el catalogo", exception.getMessage());
            verify(pedidoRepository, never()).save(any(Pedido.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción si el microservicio de catálogo falla (500)")
        void crearPedidoCatalogoCaido() {
            // Arrange
            Long productoId = 99L;
            PedidoRequestDTO request = TestDataFactory.unPedidoRequestDTO(productoId, "Juan Perez", 1);
            
            when(catalogoClient.obtenerProducto(productoId)).thenThrow(mock(FeignException.InternalServerError.class));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                pedidoService.crear(request);
            });

            assertEquals("No se pudo verificar el libro. Verifique que codigoms_catalogo este corriendo.", exception.getMessage());
            verify(pedidoRepository, never()).save(any(Pedido.class));
        }
    }

    @Nested
    @DisplayName("Pruebas para Consultar y Eliminar Pedidos")
    class ConsultarYEliminarTests {

        @Test
        @DisplayName("Debe obtener todos los pedidos mapeados a DTO")
        void obtenerTodosExitoso() {
            // Arrange
            Pedido pedido1 = TestDataFactory.unPedido("Cliente A");
            Pedido pedido2 = TestDataFactory.unPedido("Cliente B");
            when(pedidoRepository.findAll()).thenReturn(List.of(pedido1, pedido2));

            // Act
            List<PedidoResponseDTO> resultados = pedidoService.obtenerTodos();

            // Assert
            assertNotNull(resultados);
            assertEquals(2, resultados.size());
            verify(pedidoRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Debe obtener un pedido por ID si existe")
        void obtenerPorIdExitoso() {
            // Arrange
            Pedido pedido = TestDataFactory.unPedido("Cliente A");
            when(pedidoRepository.findById(pedido.getId())).thenReturn(Optional.of(pedido));

            // Act
            Optional<PedidoResponseDTO> resultado = pedidoService.obtenerPorId(pedido.getId());

            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(pedido.getId(), resultado.get().getId());
            assertEquals(pedido.getCliente(), resultado.get().getCliente());
            verify(pedidoRepository, times(1)).findById(pedido.getId());
        }

        @Test
        @DisplayName("Debe retornar una lista de pedidos filtrados por cliente")
        void obtenerPorClienteExitoso() {
            // Arrange
            String cliente = "Cliente A";
            Pedido pedido = TestDataFactory.unPedido(cliente);
            when(pedidoRepository.findByCliente(cliente)).thenReturn(List.of(pedido));

            // Act
            List<PedidoResponseDTO> resultados = pedidoService.obtenerPorCliente(cliente);

            // Assert
            assertFalse(resultados.isEmpty());
            assertEquals(1, resultados.size());
            assertEquals(cliente, resultados.get(0).getCliente());
            verify(pedidoRepository, times(1)).findByCliente(cliente);
        }

        @Test
        @DisplayName("Debe eliminar un pedido por su ID")
        void eliminarExitoso() {
            // Arrange
            Long pedidoId = 1L;
            doNothing().when(pedidoRepository).deleteById(pedidoId);

            // Act
            pedidoService.eliminar(pedidoId);

            // Assert
            verify(pedidoRepository, times(1)).deleteById(pedidoId);
        }
    }
}