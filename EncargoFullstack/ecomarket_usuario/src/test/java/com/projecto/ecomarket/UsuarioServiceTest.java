package com.projecto.ecomarket;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projecto.ecomarket.dto.UsuarioRequestDTO;
import com.projecto.ecomarket.dto.UsuarioResponseDTO;
import com.projecto.ecomarket.model.Usuario;
import com.projecto.ecomarket.repository.UsuarioRepository;
import com.projecto.ecomarket.service.UsuarioService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService - Pruebas Unitarias")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("obtenerTodos: retorna lista de DTOs cuando existen usuarios")
    void obtenerTodos_conUsuarios_retornaListaDTO() {
        Usuario usuario = TestDataFactory.unUsuario();
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<UsuarioResponseDTO> resultado = usuarioService.obtenerTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo(usuario.getNombre());
        assertThat(resultado.get(0).getEmail()).isEqualTo(usuario.getEmail());
    }

    @Test
    @DisplayName("obtenerTodos: retorna lista vacía cuando no hay usuarios")
    void obtenerTodos_sinUsuarios_retornaListaVacia() {
        when(usuarioRepository.findAll()).thenReturn(Collections.emptyList());

        List<UsuarioResponseDTO> resultado = usuarioService.obtenerTodos();

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("obtenerPorId: retorna el usuario mapeado cuando el ID existe")
    void obtenerPorId_idExistente_retornaUsuario() {
        Usuario usuario = TestDataFactory.unUsuario();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<UsuarioResponseDTO> resultado = usuarioService.obtenerPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(usuario.getId());
        assertThat(resultado.get().getNombre()).isEqualTo(usuario.getNombre());
    }

    @Test
    @DisplayName("obtenerPorId: retorna vacío cuando el ID no existe")
    void obtenerPorId_idInexistente_retornaVacio() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<UsuarioResponseDTO> resultado = usuarioService.obtenerPorId(1L);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("buscarPorNombre: retorna el usuario mapeado cuando el nombre coincide")
    void buscarPorNombre_nombreExistente_retornaUsuario() {
        Usuario usuario = TestDataFactory.unUsuario();
        when(usuarioRepository.findByNombre(usuario.getNombre())).thenReturn(Optional.of(usuario));

        Optional<UsuarioResponseDTO> resultado = usuarioService.buscarPorNombre(usuario.getNombre());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo(usuario.getNombre());
    }

    @Test
    @DisplayName("crear: guarda exitosamente un usuario nuevo si el nombre no está duplicado")
    void crear_usuarioNuevo_guardaYRetornaDTO() {
        UsuarioRequestDTO request = TestDataFactory.unUsuarioRequest();
        Usuario usuarioGuardado = new Usuario(10L, request.getNombre(), request.getEmail(), request.getTelefono(), true);

        when(usuarioRepository.existsByNombre(request.getNombre())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        UsuarioResponseDTO resultado = usuarioService.crear(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getNombre()).isEqualTo(request.getNombre());
        assertThat(resultado.getActivo()).isTrue();
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("crear: lanza excepción si el nombre de usuario ya existe en el sistema")
    void crear_nombreDuplicado_lanzaExcepcion() {
        UsuarioRequestDTO request = TestDataFactory.unUsuarioRequest();
        when(usuarioRepository.existsByNombre(request.getNombre())).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.crear(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un usuario con el nombre");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("eliminar: borra el usuario si el ID existe")
    void eliminar_idExistente_eliminaUsuario() {
        Usuario usuario = TestDataFactory.unUsuario();
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        usuarioService.eliminar(usuario.getId());

        verify(usuarioRepository).delete(usuario);
    }

    @Test
    @DisplayName("eliminar: lanza excepción si el usuario con ese ID no se encuentra")
    void eliminar_idInexistente_lanzaExcepcion() {
        Long idInexistente = 99L;
        when(usuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.eliminar(idInexistente))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario con id " + idInexistente + " no encontrado");

        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }
}
