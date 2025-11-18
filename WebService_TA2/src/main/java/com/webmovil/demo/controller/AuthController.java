package com.webmovil.demo.controller;

import com.webmovil.demo.dto.LoginRequest;
import com.webmovil.demo.dto.LoginResponse;
import com.webmovil.demo.dto.RegistroRequest;
import com.webmovil.demo.dto.UsuarioDTO;
import com.webmovil.demo.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final UsuarioService usuarioService;
    
    /**
     * API para hacer login
     * POST /api/auth/login
     * Body: { "username": "admin", "password": "123456" }
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = usuarioService.login(request);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            LoginResponse errorResponse = new LoginResponse(false, "Error en el servidor: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * API para registrar un nuevo usuario
     * POST /api/auth/registro
     * Body: { "username": "nuevo", "password": "123", "nombre": "Nombre", "email": "email@test.com", "telefono": "555-0000" }
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody RegistroRequest request) {
        try {
            UsuarioDTO nuevoUsuario = usuarioService.registrar(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Alias in English for client expectation: /register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistroRequest request) {
        return registro(request);
    }

    /**
     * API para recuperación/olvido de contraseña
     * POST /api/auth/forgot-password
     * Body: { "username": "user" }  OR { "email": "email@example.com" }
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String email = body.get("email");
            usuarioService.forgotPassword(username, email);
            return ResponseEntity.ok("Instrucciones de recuperación enviadas (simulado)");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * API para verificar si un usuario existe
     * GET /api/auth/verificar/{username}
     */
    @GetMapping("/verificar/{username}")
    public ResponseEntity<?> verificarUsuario(@PathVariable String username) {
        try {
            UsuarioDTO usuario = usuarioService.obtenerPorUsername(username);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
