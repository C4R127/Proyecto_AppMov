package com.webmovil.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearReviewRequest {
    private Integer usuarioId; // opcional
    private String nombreCliente; // requerido si no hay usuario
    private String comentario;
    private Integer calificacion; // 1-5
}
