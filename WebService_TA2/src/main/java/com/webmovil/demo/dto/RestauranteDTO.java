package com.webmovil.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestauranteDTO {
    private Integer id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private String imagenUrl;
    private String imagenThumbnailUrl;
    private String horaApertura;
    private String horaCierre;
}
