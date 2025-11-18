package com.webmovil.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponse {
    private boolean success;
    private String message;
    private UsuarioDTO usuario;
    private String token; // token o identificador de sesión para el cliente

    public LoginResponse(boolean success, String message, UsuarioDTO usuario) {
        this(success, message, usuario, null);
    }

    public LoginResponse(boolean success, String message, UsuarioDTO usuario, String token) {
        this.success = success;
        this.message = message;
        this.usuario = usuario;
        this.token = token;
    }
}
