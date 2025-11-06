package com.webmovil.demo.controller;

import com.webmovil.demo.dto.UsuarioDTO;
import com.webmovil.demo.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    /**
     * API para consultar todos los usuarios
     * GET /api/usuarios
     */
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }
    
    /**
     * API para consultar un usuario por ID
     * GET /api/usuarios/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(usuarioService.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * API para actualizar un usuario
     * PUT /api/usuarios/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody UsuarioDTO usuarioDTO) {
        try {
            UsuarioDTO actualizado = usuarioService.actualizarUsuario(id, usuarioDTO);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * API para cambiar contraseña
     * PATCH /api/usuarios/{id}/password
     * Body: { "passwordActual": "123", "passwordNueva": "456" }
     */
    @PatchMapping("/{id}/password")
    public ResponseEntity<?> cambiarPassword(@PathVariable Integer id, @RequestBody Map<String, String> passwords) {
        try {
            String passwordActual = passwords.get("passwordActual");
            String passwordNueva = passwords.get("passwordNueva");
            usuarioService.cambiarPassword(id, passwordActual, passwordNueva);
            return ResponseEntity.ok("Contraseña actualizada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * API para desactivar un usuario
     * PATCH /api/usuarios/{id}/desactivar
     */
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivar(@PathVariable Integer id) {
        try {
            usuarioService.desactivarUsuario(id);
            return ResponseEntity.ok("Usuario desactivado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * API para eliminar un usuario
     * DELETE /api/usuarios/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok("Usuario eliminado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
