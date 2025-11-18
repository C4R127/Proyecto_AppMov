package com.webmovil.demo.service;

import com.webmovil.demo.dto.LoginRequest;
import com.webmovil.demo.dto.LoginResponse;
import com.webmovil.demo.dto.RegistroRequest;
import com.webmovil.demo.dto.UsuarioDTO;
import com.webmovil.demo.entity.Usuario;
import com.webmovil.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // Buscar usuario por username
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
            .orElse(null);
        
        if (usuario == null) {
            return new LoginResponse(false, "Usuario no encontrado", null);
        }
        
        // Verificar si está activo
        if (!usuario.getActivo()) {
            return new LoginResponse(false, "Usuario inactivo", null);
        }
        
        // Verificar contraseña (en producción usar BCrypt)
        if (!usuario.getPassword().equals(request.getPassword())) {
            return new LoginResponse(false, "Contraseña incorrecta", null);
        }
        
        // Actualizar último acceso
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);
        
        // Login exitoso
        UsuarioDTO usuarioDTO = convertirADTO(usuario);
        // Generar token simple (UUID). In production, use JWT.
        String token = java.util.UUID.randomUUID().toString();
        return new LoginResponse(true, "Login exitoso", usuarioDTO, token);
    }
    
    @Transactional
    public UsuarioDTO registrar(RegistroRequest request) {
        // Validar que el username no existe
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El username ya está en uso");
        }
        
        // Validar que el email no existe
        if (request.getEmail() != null && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(request.getPassword()); // En producción usar BCrypt
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setTelefono(request.getTelefono());
        usuario.setRol(Usuario.Rol.CLIENTE);
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        
        Usuario guardado = usuarioRepository.save(usuario);
        return convertirADTO(guardado);
    }
    
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerTodos() {
        return usuarioRepository.findAll().stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorId(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return convertirADTO(usuario);
    }
    
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        return convertirADTO(usuario);
    }
    
    @Transactional
    public UsuarioDTO actualizarUsuario(Integer id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setTelefono(usuarioDTO.getTelefono());
        
        Usuario actualizado = usuarioRepository.save(usuario);
        return convertirADTO(actualizado);
    }
    
    @Transactional
    public void cambiarPassword(Integer id, String passwordActual, String passwordNueva) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        if (!usuario.getPassword().equals(passwordActual)) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }
        
        usuario.setPassword(passwordNueva);
        usuarioRepository.save(usuario);
    }
    
    @Transactional
    public void desactivarUsuario(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void forgotPassword(String username, String email) {
        Usuario usuario = null;
        if (username != null && !username.isEmpty()) {
            usuario = usuarioRepository.findByUsername(username).orElse(null);
        }
        if (usuario == null && email != null && !email.isEmpty()) {
            usuario = usuarioRepository.findByEmail(email).orElse(null);
        }

        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado para recuperar contraseña");
        }

        // Generar contraseña temporal simple. En producción, enviar un token por email.
        String tempPassword = "temp-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        usuario.setPassword(tempPassword);
        usuarioRepository.save(usuario);

        // Nota: Aquí se debería enviar un correo con instrucciones. Por ahora devolvemos el hecho de actualización.
    }
    
    @Transactional
    public void eliminarUsuario(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
    
    private UsuarioDTO convertirADTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setTelefono(usuario.getTelefono());
        dto.setRol(usuario.getRol().name());
        dto.setActivo(usuario.getActivo());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        dto.setUltimoAcceso(usuario.getUltimoAcceso());
        return dto;
    }
}
