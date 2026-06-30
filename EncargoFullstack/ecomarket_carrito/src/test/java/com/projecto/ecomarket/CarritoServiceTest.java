package com.projecto.ecomarket;

import com.projecto.ecomarket.client.CatalogoClient;
import com.projecto.ecomarket.dto.CarritoResponseDTO;
import com.projecto.ecomarket.dto.ItemCarritoRequestDTO;
import com.projecto.ecomarket.dto.ProductoDTO;
import com.projecto.ecomarket.model.Carrito;
import com.projecto.ecomarket.model.ItemCarrito;
import com.projecto.ecomarket.repository.CarritoRepository;
import com.projecto.ecomarket.repository.ItemCarritoRepository;
import com.projecto.ecomarket.service.CarritoService;

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
public class CarritoServiceTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ItemCarritoRepository itemCarritoRepository;

    @Mock
    private CatalogoClient catalogoClient;

    @InjectMocks
    private CarritoService carritoService;

    @Nested
    @DisplayName("Pruebas para Crear y Obtener Carrito")
    class CrearYObtenerTests {

        @Test
        @DisplayName("Debe crear un carrito nuevo para el usuario")
        void crearCarritoExitoso() {
            // Arrange
            String usuario = "testuser";
            when(carritoRepository.save(any(Carrito.class))).thenAnswer(i -> {
                Carrito c = i.getArgument(0);
                c.setId(1L);
                return c;
            });

            // Act
            CarritoResponseDTO response = carritoService.crear(usuario);

            // Assert
            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals(usuario, response.getUsuario());
            assertTrue(response.getItems().isEmpty());
            assertEquals(BigDecimal.ZERO, response.getTotal());
            verify(carritoRepository, times(1)).save(any(Carrito.class));
        }

        @Test
        @DisplayName("Debe obtener un carrito por ID existente")
        void obtenerPorIdExitoso() {
            // Arrange
            Carrito carrito = TestDataFactory.unCarritoConItems("testuser");
            when(carritoRepository.findById(carrito.getId())).thenReturn(Optional.of(carrito));

            // Act
            Optional<CarritoResponseDTO> response = carritoService.obtenerPorId(carrito.getId());

            // Assert
            assertTrue(response.isPresent());
            assertEquals(carrito.getId(), response.get().getId());
            assertEquals(2, response.get().getItems().size());
            // Verifica que el total no sea nulo ni cero porque tiene items
            assertTrue(response.get().getTotal().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("Debe retornar una lista de carritos por usuario")
        void obtenerPorUsuarioExitoso() {
            // Arrange
            String usuario = "testuser";
            Carrito carrito = TestDataFactory.unCarritoVacio(usuario);
            when(carritoRepository.findByUsuario(usuario)).thenReturn(List.of(carrito));

            // Act
            List<CarritoResponseDTO> carritos = carritoService.obtenerPorUsuario(usuario);

            // Assert
            assertFalse(carritos.isEmpty());
            assertEquals(1, carritos.size());
            assertEquals(usuario, carritos.get(0).getUsuario());
        }
    }

    @Nested
    @DisplayName("Pruebas para Agregar Items al Carrito")
    class AgregarItemTests {

        @Test
        @DisplayName("Debe agregar un item nuevo llamando al catálogo")
        void agregarItemNuevoExitoso() {
            // Arrange
            Carrito carrito = TestDataFactory.unCarritoVacio("user");
            ProductoDTO productoMock = TestDataFactory.unProductoDTO(99L);
            ItemCarritoRequestDTO request = TestDataFactory.unItemCarritoRequestDTO(99L, 2);

            when(carritoRepository.findById(carrito.getId())).thenReturn(Optional.of(carrito));
            when(catalogoClient.obtenerProducto(99L)).thenReturn(productoMock);
            when(carritoRepository.save(any(Carrito.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            CarritoResponseDTO response = carritoService.agregarItem(carrito.getId(), request);

            // Assert
            assertEquals(1, response.getItems().size());
            assertEquals(99L, response.getItems().get(0).getProductoId());
            assertEquals(2, response.getItems().get(0).getCantidad());
            
            // El subtotal debe ser Precio * Cantidad
            BigDecimal subtotalEsperado = productoMock.getPrecio().multiply(BigDecimal.valueOf(2));
            assertEquals(subtotalEsperado, response.getItems().get(0).getSubtotal());
            
            verify(catalogoClient, times(1)).obtenerProducto(99L);
        }

        @Test
        @DisplayName("Debe incrementar la cantidad si el item ya existe en el carrito")
        void agregarItemExistenteIncrementaCantidad() {
            // Arrange
            Carrito carrito = TestDataFactory.unCarritoConItems("user");
            Long productoIdExistente = carrito.getItems().get(0).getProductoId();
            Integer cantidadOriginal = carrito.getItems().get(0).getCantidad();
            
            ItemCarritoRequestDTO request = TestDataFactory.unItemCarritoRequestDTO(productoIdExistente, 3);
            ProductoDTO productoMock = TestDataFactory.unProductoDTO(productoIdExistente);

            when(carritoRepository.findById(carrito.getId())).thenReturn(Optional.of(carrito));
            when(catalogoClient.obtenerProducto(productoIdExistente)).thenReturn(productoMock);
            when(carritoRepository.save(any(Carrito.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            CarritoResponseDTO response = carritoService.agregarItem(carrito.getId(), request);

            // Assert - El tamaño de la lista de items sigue siendo el mismo, pero la cantidad de uno subió
            assertEquals(2, response.getItems().size());
            
            // Verificamos que la cantidad nueva sea Original + 3
            Integer cantidadNueva = response.getItems().stream()
                    .filter(i -> i.getProductoId().equals(productoIdExistente))
                    .findFirst().get().getCantidad();
                    
            assertEquals(cantidadOriginal + 3, cantidadNueva);
        }

        @Test
        @DisplayName("Debe lanzar excepción si el producto no se encuentra en el catálogo (404)")
        void agregarItemProductoNoEncontrado() {
            // Arrange
            Carrito carrito = TestDataFactory.unCarritoVacio("user");
            ItemCarritoRequestDTO request = TestDataFactory.unItemCarritoRequestDTO(99L, 1);

            when(carritoRepository.findById(carrito.getId())).thenReturn(Optional.of(carrito));
            
            // Simulamos que el FeignClient lanza un NotFound
            when(catalogoClient.obtenerProducto(99L)).thenThrow(mock(FeignException.NotFound.class));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                carritoService.agregarItem(carrito.getId(), request);
            });

            assertEquals("El libro con id 99 no existe en el catalogo", exception.getMessage());
            verify(carritoRepository, never()).save(any(Carrito.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción si el servicio de catálogo falla (500)")
        void agregarItemCatalogoCaido() {
            // Arrange
            Carrito carrito = TestDataFactory.unCarritoVacio("user");
            ItemCarritoRequestDTO request = TestDataFactory.unItemCarritoRequestDTO(99L, 1);

            when(carritoRepository.findById(carrito.getId())).thenReturn(Optional.of(carrito));
            
            // Simulamos un error genérico de Feign
            when(catalogoClient.obtenerProducto(99L)).thenThrow(mock(FeignException.InternalServerError.class));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                carritoService.agregarItem(carrito.getId(), request);
            });

            assertEquals("No se pudo verificar el libro. Verifique que codigoms_catalogo este corriendo.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Pruebas para Quitar, Vaciar y Eliminar")
    class ModificarYEliminarTests {

        @Test
        @DisplayName("Debe quitar un item del carrito exitosamente")
        void quitarItemExitoso() {
            // Arrange
            Carrito carrito = TestDataFactory.unCarritoConItems("user");
            ItemCarrito itemAQuitar = carrito.getItems().get(0);
            Long itemId = itemAQuitar.getId();

            when(carritoRepository.findById(carrito.getId())).thenReturn(Optional.of(carrito));
            when(itemCarritoRepository.findById(itemId)).thenReturn(Optional.of(itemAQuitar));
            when(carritoRepository.save(any(Carrito.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            CarritoResponseDTO response = carritoService.quitarItem(carrito.getId(), itemId);

            // Assert
            assertEquals(1, response.getItems().size()); // Originalmente eran 2, ahora 1
            verify(carritoRepository).save(carrito);
        }

        @Test
        @DisplayName("Debe lanzar excepción al quitar un item que no pertenece al carrito")
        void quitarItemQueNoPerteneceAlCarrito() {
            // Arrange
            Carrito carrito = TestDataFactory.unCarritoVacio("user");
            
            Carrito otroCarrito = TestDataFactory.unCarritoVacio("user2");
            otroCarrito.setId(99L);
            ItemCarrito itemDeOtroCarrito = TestDataFactory.unItemCarrito(otroCarrito, 1L);
            itemDeOtroCarrito.setId(5L);

            when(carritoRepository.findById(carrito.getId())).thenReturn(Optional.of(carrito));
            when(itemCarritoRepository.findById(5L)).thenReturn(Optional.of(itemDeOtroCarrito));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                carritoService.quitarItem(carrito.getId(), 5L);
            });

            assertTrue(exception.getMessage().contains("no pertenece al carrito"));
            verify(carritoRepository, never()).save(any(Carrito.class));
        }

        @Test
        @DisplayName("Debe vaciar todos los items de un carrito")
        void vaciarCarritoExitoso() {
            // Arrange
            Carrito carrito = TestDataFactory.unCarritoConItems("user");
            when(carritoRepository.findById(carrito.getId())).thenReturn(Optional.of(carrito));
            when(carritoRepository.save(any(Carrito.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            CarritoResponseDTO response = carritoService.vaciar(carrito.getId());

            // Assert
            assertTrue(response.getItems().isEmpty());
            assertEquals(BigDecimal.ZERO, response.getTotal());
        }

        @Test
        @DisplayName("Debe eliminar un carrito mediante su repositorio")
        void eliminarCarritoExitoso() {
            // Arrange
            Long carritoId = 1L;
            doNothing().when(carritoRepository).deleteById(carritoId);

            // Act
            carritoService.eliminar(carritoId);

            // Assert
            verify(carritoRepository, times(1)).deleteById(carritoId);
        }
    }
}