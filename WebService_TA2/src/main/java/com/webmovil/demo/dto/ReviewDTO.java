package com.webmovil.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Integer id;
    private Integer restauranteId;
    private Integer usuarioId;
    private String nombreAutor;
    private String comentario;
    private Integer calificacion;
    private LocalDateTime fechaCreacion;
}
