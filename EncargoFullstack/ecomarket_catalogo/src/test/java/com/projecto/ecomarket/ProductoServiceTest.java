package com.projecto.ecomarket;

import com.projecto.ecomarket.dto.ProductoRequestDTO;
import com.projecto.ecomarket.dto.ProductoResponseDTO;
import com.projecto.ecomarket.model.Categoria;
import com.projecto.ecomarket.model.Producto;
import com.projecto.ecomarket.repository.CategoriaRepository;
import com.projecto.ecomarket.repository.ProductoRepository;
import com.projecto.ecomarket.service.ProductoService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService; // Asegúrate de que el nombre coincida con tu clase real

    @Test
    @DisplayName("Retornar una lista con todos los productos mapeados a DTO")
    void obtenerTodos() {
        // Arrange
        Categoria categoria = TestDataFactory.unaCategoria();
        Producto producto = TestDataFactory.unProducto(categoria);
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        // Act
        List<ProductoResponseDTO> resultado = productoService.obtenerTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(producto.getNombre(), resultado.get(0).getNombre());
        assertEquals(categoria.getNombre(), resultado.get(0).getCategoriaNombre()); 
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Retornar un Optional con el producto DTO si existe el ID")
    void obtenerPorId_Existe() {
        // Arrange
        Long id = 1L;
        Categoria categoria = TestDataFactory.unaCategoria();
        Producto producto = TestDataFactory.unProducto(categoria);
        producto.setId(id); // Seteamos el ID esperado
        when(productoRepository.findById(id)).thenReturn(Optional.of(producto));

        // Act
        Optional<ProductoResponseDTO> resultado = productoService.obtenerPorId(id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(producto.getNombre(), resultado.get().getNombre());
        verify(productoRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Retornar un Optional vacío si el ID del producto no existe")
    void obtenerPorId_NoExiste() {
        // Arrange
        Long id = 99L;
        when(productoRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<ProductoResponseDTO> resultado = productoService.obtenerPorId(id);

        // Assert
        assertFalse(resultado.isPresent());
        verify(productoRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Guardar un producto exitosamente cuando la categoría existe")
    void guardar_Exitoso() {
        // Arrange
        Long categoriaId = 10L;
        Categoria categoria = TestDataFactory.unaCategoria();
        categoria.setId(categoriaId);

        ProductoRequestDTO requestDTO = TestDataFactory.unProductoRequest(categoriaId);
        
        // El producto simulado que devolverá el repositorio simulando que generó el ID 1L
        Producto productoGuardado = new Producto(1L, requestDTO.getNombre(), requestDTO.getGtin(), requestDTO.getPrecio(), categoria);

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

        // Act
        ProductoResponseDTO resultado = productoService.guardar(requestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(requestDTO.getNombre(), resultado.getNombre());
        assertEquals(categoria.getNombre(), resultado.getCategoriaNombre());
        verify(categoriaRepository, times(1)).findById(categoriaId);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Lanzar una RuntimeException al guardar si la categoría no existe")
    void guardar_CategoriaNoEncontrada_LanzaExcepcion() {
        // Arrange
        Long categoriaId = 99L;
        ProductoRequestDTO requestDTO = TestDataFactory.unProductoRequest(categoriaId);

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            productoService.guardar(requestDTO);
        });

        assertEquals("Categoria no encontrada con id: " + categoriaId, excepcion.getMessage());
        // Verificamos que jamás se intentó guardar el producto si falló la categoría
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Llamar al repositorio para eliminar un producto")
    void eliminar() {
        // Arrange
        Long id = 1L;
        doNothing().when(productoRepository).deleteById(id);

        // Act
        productoService.eliminar(id);

        // Assert
        verify(productoRepository, times(1)).deleteById(id);
    }
}
