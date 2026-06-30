package com.projecto.ecomarket;

import com.projecto.ecomarket.client.EnvioClient;
import com.projecto.ecomarket.dto.EnvioDTO;
import com.projecto.ecomarket.dto.PagoResponseDTO;
import com.projecto.ecomarket.model.Pago;
import com.projecto.ecomarket.repository.PagoRepository;
import com.projecto.ecomarket.service.PagoService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private EnvioClient envioClient;

    @InjectMocks
    private PagoService pagoService;

    @Captor
    private ArgumentCaptor<EnvioDTO> envioCaptor; 

    @Nested
    @DisplayName("Pruebas para Procesar Pago")
    class ProcesarPagoTests {

        @Test
        @DisplayName("Debe procesar el pago, cambiar el estado, guardar y lanzar la creación del envío")
        void procesarPagoExitoso() {
            // Arrange
            Pago pagoParaProcesar = TestDataFactory.unPagoParaProcesar();
            Long pedidoIdOriginal = pagoParaProcesar.getPedidoId();
            Double montoOriginal = pagoParaProcesar.getMonto();

            // Usamos thenAnswer porque el método procesarPago altera el estado y genera el transaccionId
            when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> {
                Pago pagoGuardado = invocation.getArgument(0);
                pagoGuardado.setId(10L); // Simulamos el ID autogenerado por la BD
                return pagoGuardado;
            });

            // Simulamos la respuesta de EnvioClient (aunque en tu service no se guarda en variable, evita nulos)
            when(envioClient.crearEnvio(any(EnvioDTO.class)))
                    .thenReturn(TestDataFactory.unEnvioDTO(pedidoIdOriginal));

            // Act
            PagoResponseDTO respuesta = pagoService.procesarPago(pagoParaProcesar);

            // Assert de la respuesta
            assertNotNull(respuesta);
            assertEquals(10L, respuesta.getPagoId());
            assertEquals(pedidoIdOriginal, respuesta.getPedidoId());
            assertEquals(montoOriginal, respuesta.getMonto());
            assertEquals("COMPLETADO", respuesta.getEstado());

            // Verificamos que se llamó al repositorio
            verify(pagoRepository, times(1)).save(any(Pago.class));

            // Verificamos que se llamó a EnvioClient y atrapamos el argumento que se envió
            verify(envioClient, times(1)).crearEnvio(envioCaptor.capture());
            EnvioDTO envioEnviadoAlCliente = envioCaptor.getValue();
            
            assertEquals(pedidoIdOriginal, envioEnviadoAlCliente.getPedidoId());
            assertEquals("Dirección de prueba", envioEnviadoAlCliente.getDireccionEntrega());
            assertEquals("prueba", envioEnviadoAlCliente.getTransportista());
        }

        @Test
        @DisplayName("Debe propagar excepción si EnvioClient falla al crear el envío")
        void procesarPagoFallaEnvioClient() {
            // Arrange
            Pago pagoParaProcesar = TestDataFactory.unPagoParaProcesar();

            when(pagoRepository.save(any(Pago.class))).thenAnswer(i -> {
                Pago pago = i.getArgument(0);
                pago.setId(10L);
                return pago;
            });

            // Simulamos que el WebClient tira la excepción definida en tu EnvioClient
            when(envioClient.crearEnvio(any(EnvioDTO.class)))
                    .thenThrow(new RuntimeException("No se pudo registrar el envío"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                pagoService.procesarPago(pagoParaProcesar);
            });

            assertEquals("No se pudo registrar el envío", exception.getMessage());
            verify(pagoRepository, times(1)).save(any(Pago.class));
            verify(envioClient, times(1)).crearEnvio(any(EnvioDTO.class));
        }
    }

    @Nested
    @DisplayName("Pruebas para Obtener Pago por Pedido")
    class ObtenerPagoPorPedidoTests {

        @Test
        @DisplayName("Debe retornar el PagoResponseDTO correctamente cuando existe")
        void obtenerPagoPorPedidoExitoso() {
            // Arrange
            Pago pagoEnBd = TestDataFactory.unPagoCompleto();
            Long pedidoId = pagoEnBd.getPedidoId();

            when(pagoRepository.findByPedidoId(pedidoId)).thenReturn(Optional.of(pagoEnBd));

            // Act
            PagoResponseDTO respuesta = pagoService.obtenerPagoPorPedido(pedidoId);

            // Assert
            assertNotNull(respuesta);
            assertEquals(pagoEnBd.getId(), respuesta.getPagoId());
            assertEquals(pedidoId, respuesta.getPedidoId());
            assertEquals(pagoEnBd.getEstado(), respuesta.getEstado());

            verify(pagoRepository, times(1)).findByPedidoId(pedidoId);
        }

        @Test
        @DisplayName("Debe lanzar RuntimeException cuando el pago no existe")
        void obtenerPagoPorPedidoNoEncontrado() {
            // Arrange
            Long pedidoId = 99L;
            when(pagoRepository.findByPedidoId(pedidoId)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                pagoService.obtenerPagoPorPedido(pedidoId);
            });

            assertEquals("Pago no encontrado para este pedido", exception.getMessage());
            verify(pagoRepository, times(1)).findByPedidoId(pedidoId);
        }
    }
}