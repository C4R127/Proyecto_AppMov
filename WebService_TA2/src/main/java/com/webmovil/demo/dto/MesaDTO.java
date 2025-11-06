package com.webmovil.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MesaDTO {
    private Integer id;
    private Integer restauranteId;
    private String numeroMesa;
    private Integer capacidad;
    private Boolean disponible;
}
