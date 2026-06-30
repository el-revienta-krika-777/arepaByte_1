package com.projecto.ecomarket;

import com.projecto.ecomarket.TestDataFactory;
import com.projecto.ecomarket.dto.CuponRequestDTO;
import com.projecto.ecomarket.dto.CuponResponseDTO;
import com.projecto.ecomarket.model.Cupon;
import com.projecto.ecomarket.repository.CuponRepository;
import com.projecto.ecomarket.service.CuponService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
public class CuponServiceTest {

    @Mock
    private CuponRepository cuponRepository;

    @InjectMocks
    private CuponService cuponService;

    @Captor
    private ArgumentCaptor<Cupon> cuponCaptor;

    @Nested
    @DisplayName("Pruebas para Obtener Cupones")
    public class ObtenerCuponesTests {

        @Test
        @DisplayName("Debe obtener todos los cupones mapeados con sus estadísticas")
        public void obtenerTodosExitoso() {
            // Arrange
            Cupon cupon1 = TestDataFactory.unCuponPorcentajeValido();
            Cupon cupon2 = TestDataFactory.unCuponFijoValido();
            when(cuponRepository.findAll()).thenReturn(List.of(cupon1, cupon2));

            // Act
            List<CuponResponseDTO> resultado = cuponService.obtenerTodos();

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            assertEquals(cupon1.getCodigo(), resultado.get(0).getCodigo());
            assertTrue(resultado.get(0).getDescripcion().contains(String.valueOf(cupon1.getUsosActuales())));
            
            verify(cuponRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Debe lanzar excepción si se busca un código que no existe")
        public void obtenerPorCodigoNoEncontrado() {
            // Arrange
            String codigoInvalido = "FALSO2026";
            when(cuponRepository.findByCodigo(codigoInvalido)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                cuponService.obtenerPorCodigo(codigoInvalido);
            });

            assertEquals("Cupón no encontrado", exception.getMessage());
            verify(cuponRepository, times(1)).findByCodigo(codigoInvalido);
        }
    }

    @Nested
    @DisplayName("Pruebas para Procesar Validación de Cupón")
    public class ProcesarValidacionTests {

        @Test
        @DisplayName("Debe validar un cupón exitosamente y devolver el descuento calculado")
        public void procesarValidacionExitoso() {
            // Arrange
            Cupon cupon = TestDataFactory.unCuponPorcentajeValido();
            cupon.setValor(10.0); // 10% de descuento
            
            CuponRequestDTO request = TestDataFactory.unCuponRequestDTO(cupon.getCodigo(), 1000.0);
            
            when(cuponRepository.findByCodigo(request.getCodigo())).thenReturn(Optional.of(cupon));

            // Act
            CuponResponseDTO response = cuponService.procesarValidacion(request);

            // Assert
            assertNotNull(response);
            assertTrue(response.getValido());
            assertEquals(cupon.getCodigo(), response.getCodigo());
            assertEquals(100.0, response.getMontoDescuento()); // 10% de 1000 es 100
        }

        @Test
        @DisplayName("Debe capturar la excepción de cupón expirado y devolver valido=false")
        public void procesarValidacionFallaPorExpiracion() {
            // Arrange
            Cupon cuponExpirado = TestDataFactory.unCuponExpirado();
            CuponRequestDTO request = TestDataFactory.unCuponRequestDTO(cuponExpirado.getCodigo(), 500.0);

            when(cuponRepository.findByCodigo(request.getCodigo())).thenReturn(Optional.of(cuponExpirado));

            // Act
            CuponResponseDTO response = cuponService.procesarValidacion(request);

            // Assert - Fíjate que no usamos assertThrows porque tu catch bloquea la excepción y devuelve un DTO
            assertNotNull(response);
            assertFalse(response.getValido());
            assertEquals("El cupón ha expirado", response.getDescripcion());
            assertEquals(0.0, response.getMontoDescuento());
        }
    }

    @Nested
    @DisplayName("Pruebas para Calcular Descuento (Reglas de Negocio)")
    public class CalcularDescuentoTests {

        @Test
        @DisplayName("Debe calcular descuento fijo correctamente sin superar el total del pedido")
        public void calcularDescuentoFijo() {
            // Arrange
            Cupon cuponFijo = TestDataFactory.unCuponFijoValido();
            cuponFijo.setValor(50.0); // Descuento de $50
            when(cuponRepository.findByCodigo(cuponFijo.getCodigo())).thenReturn(Optional.of(cuponFijo));

            // Act
            Double descuento = cuponService.calcularDescuento(cuponFijo.getCodigo(), 200.0);

            // Assert
            assertEquals(50.0, descuento);
        }

        @Test
        @DisplayName("El descuento fijo no debe ser mayor al total del carrito")
        public void calcularDescuentoFijoTope() {
            // Arrange
            Cupon cuponFijo = TestDataFactory.unCuponFijoValido();
            cuponFijo.setValor(100.0); // Descuento de $100
            when(cuponRepository.findByCodigo(cuponFijo.getCodigo())).thenReturn(Optional.of(cuponFijo));

            // Act: El pedido total es solo de $30
            Double descuento = cuponService.calcularDescuento(cuponFijo.getCodigo(), 30.0);

            // Assert: El descuento máximo aplicable es $30 (Math.min en tu código)
            assertEquals(30.0, descuento);
        }
    }

    @Nested
    @DisplayName("Pruebas para Crear y Aplicar Cupones")
    public class CrearYAplicarCuponesTests {

        @Test
        @DisplayName("Debe crear un cupón nuevo e inicializar sus valores por defecto")
        public void crearCuponExitoso() {
            // Arrange
            CuponRequestDTO request = TestDataFactory.unCuponRequestDTO("NUEVO20", 0.0);
            request.setTipo("PORCENTAJE");
            request.setValor(20.0);

            when(cuponRepository.findByCodigo(request.getCodigo())).thenReturn(Optional.empty());
            when(cuponRepository.save(any(Cupon.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            CuponResponseDTO response = cuponService.crearCupon(request);

            // Assert
            assertNotNull(response);
            assertTrue(response.getValido());
            
            // Verificamos con el Captor que se setearon usosActuales en 0 y activo en true
            verify(cuponRepository).save(cuponCaptor.capture());
            Cupon cuponGuardado = cuponCaptor.getValue();
            
            assertEquals(0, cuponGuardado.getUsosActuales());
            assertTrue(cuponGuardado.getActivo());
            assertEquals("NUEVO20", cuponGuardado.getCodigo());
        }

        @Test
        @DisplayName("Debe lanzar excepción si se intenta crear un cupón con código existente")
        public void crearCuponCodigoDuplicado() {
            // Arrange
            CuponRequestDTO request = TestDataFactory.unCuponRequestDTO("DUPLICADO", 0.0);
            when(cuponRepository.findByCodigo(request.getCodigo())).thenReturn(Optional.of(new Cupon()));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                cuponService.crearCupon(request);
            });

            assertEquals("El código de cupón 'DUPLICADO' ya existe.", exception.getMessage());
            verify(cuponRepository, never()).save(any(Cupon.class));
        }

        @Test
        @DisplayName("Debe sumar 1 al contador de usos al aplicar el cupón")
        public void aplicarCuponExitoso() {
            // Arrange
            Cupon cupon = TestDataFactory.unCuponPorcentajeValido();
            Integer usosIniciales = cupon.getUsosActuales();
            when(cuponRepository.findByCodigo(cupon.getCodigo())).thenReturn(Optional.of(cupon));

            // Act
            cuponService.aplicarCupon(cupon.getCodigo());

            // Assert
            verify(cuponRepository).save(cuponCaptor.capture());
            Cupon cuponActualizado = cuponCaptor.getValue();
            
            assertEquals(usosIniciales + 1, cuponActualizado.getUsosActuales());
        }
    }
}