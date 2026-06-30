package com.projecto.ecomarket;

import com.projecto.ecomarket.client.PedidoClient;
import com.projecto.ecomarket.client.UsuarioClient;
import com.projecto.ecomarket.dto.EnvioResponseDTO;
import com.projecto.ecomarket.dto.PedidoDTO;
import com.projecto.ecomarket.model.Envio;
import com.projecto.ecomarket.repository.EnvioRepository;
import com.projecto.ecomarket.service.EnvioService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private PedidoClient pedidoClient;

    @Mock
    private UsuarioClient usuarioClient; 

    @InjectMocks
    private EnvioService envioService;

    @Nested
    @DisplayName("Pruebas para Crear Envío")
    class CrearEnvioTests {

        @Test
        @DisplayName("Debe crear un envío exitosamente cuando el pedido existe")
        void crearEnvioExitoso() {
            // Arrange (Preparar)
            Envio envioParaCrear = TestDataFactory.unEnvioParaCrear();
            PedidoDTO pedidoMock = TestDataFactory.unPedidoDTO(envioParaCrear.getPedidoId());

            when(pedidoClient.buscarPedido(envioParaCrear.getPedidoId()))
                    .thenReturn(Optional.of(pedidoMock));
            
            // Simulamos que la base de datos le asigna un ID al guardar

            when(envioRepository.save(any(Envio.class))).thenAnswer(invocation -> {
                Envio envioGuardado = invocation.getArgument(0);
                envioGuardado.setId(1L); 
                return envioGuardado;
            });

            // Act (Ejecutar)
            EnvioResponseDTO respuesta = envioService.crearEnvio(envioParaCrear);

            // Assert (Verificar)
            assertNotNull(respuesta);
            assertEquals(1L, respuesta.getEnvioId());
            assertEquals(envioParaCrear.getPedidoId(), respuesta.getPedidoId());
            assertEquals("PREPARANDO", respuesta.getEstadoEnvio());
            assertNotNull(respuesta.getFechaEntregaEstimada());
            
            verify(pedidoClient, times(1)).buscarPedido(envioParaCrear.getPedidoId());
            verify(envioRepository, times(1)).save(any(Envio.class));
        }

        @Test
        @DisplayName("Debe lanzar RuntimeException cuando el pedido no existe")
        void crearEnvioErrorPedidoNoExiste() {
            // Arrange
            Envio envioParaCrear = TestDataFactory.unEnvioParaCrear();
            when(pedidoClient.buscarPedido(envioParaCrear.getPedidoId()))
                    .thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                envioService.crearEnvio(envioParaCrear);
            });

            assertEquals("Error: El pedido no existe", exception.getMessage());
            verify(envioRepository, never()).save(any(Envio.class));
        }
    }

    @Nested
    @DisplayName("Pruebas para Obtener Envíos")
    class ObtenerEnviosTests {

        @Test
        @DisplayName("Debe retornar la lista de todos los envíos mapeados a DTO")
        void obtenerTodosLosEnvios() {
            // Arrange
            Envio envio1 = TestDataFactory.unEnvioCompleto();
            Envio envio2 = TestDataFactory.unEnvioCompleto();
            when(envioRepository.findAll()).thenReturn(List.of(envio1, envio2));

            // Act
            List<EnvioResponseDTO> resultado = envioService.obtenerTodosLosEnvios();

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            assertEquals(envio1.getId(), resultado.get(0).getEnvioId());
            assertEquals(envio2.getId(), resultado.get(1).getEnvioId());
            verify(envioRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Debe retornar una lista vacía si no hay envíos en BD")
        void obtenerTodosLosEnviosVacio() {
            // Arrange
            when(envioRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<EnvioResponseDTO> resultado = envioService.obtenerTodosLosEnvios();

            // Assert
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("Pruebas para Actualizar Estado")
    class ActualizarEstadoTests {

        @Test
        @DisplayName("Debe actualizar el estado y el número de seguimiento con éxito")
        void actualizarEstadoExitoso() {
            // Arrange
            Envio envioExistente = TestDataFactory.unEnvioCompleto();
            Long envioId = envioExistente.getId();
            String nuevoEstado = "EN_TRANSITO";
            String nuevoSeguimiento = "TRK-9999999999";

            when(envioRepository.findById(envioId)).thenReturn(Optional.of(envioExistente));
            when(envioRepository.save(any(Envio.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            EnvioResponseDTO respuesta = envioService.actualizarEstado(envioId, nuevoEstado, nuevoSeguimiento);

            // Assert
            assertNotNull(respuesta);
            assertEquals(nuevoEstado, respuesta.getEstadoEnvio());
            assertEquals(nuevoSeguimiento, respuesta.getNumeroSeguimiento());
            verify(envioRepository, times(1)).findById(envioId);
            verify(envioRepository, times(1)).save(envioExistente);
        }

        @Test
        @DisplayName("Debe lanzar RuntimeException si el envío a actualizar no existe")
        void actualizarEstadoErrorNoEncontrado() {
            // Arrange
            when(envioRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                envioService.actualizarEstado(1L, "ENTREGADO", "TRK-123");
            });

            assertEquals("Envío no encontrado", exception.getMessage());
            verify(envioRepository, never()).save(any(Envio.class));
        }
    }

    @Nested
    @DisplayName("Pruebas para Consultar por Pedido")
    class ConsultarPorPedidoTests {

        @Test
        @DisplayName("Debe retornar el envío correspondiente a un ID de pedido")
        void consultarPorPedidoExitoso() {
            // Arrange
            Envio envioExistente = TestDataFactory.unEnvioCompleto();
            Long pedidoId = envioExistente.getPedidoId();
            when(envioRepository.findByPedidoId(pedidoId)).thenReturn(Optional.of(envioExistente));

            // Act
            EnvioResponseDTO respuesta = envioService.consultarPorPedido(pedidoId);

            // Assert
            assertNotNull(respuesta);
            assertEquals(pedidoId, respuesta.getPedidoId());
            assertEquals(envioExistente.getId(), respuesta.getEnvioId());
            verify(envioRepository, times(1)).findByPedidoId(pedidoId);
        }

        @Test
        @DisplayName("Debe lanzar RuntimeException si el pedido no tiene envíos asociados")
        void consultarPorPedidoErrorNoEncontrado() {
            // Arrange
            when(envioRepository.findByPedidoId(1L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                envioService.consultarPorPedido(1L);
            });

            assertEquals("No hay envío registrado para este pedido", exception.getMessage());
        }
    }
}