package com.webmovil.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarReservaRequest {
    private Integer mesaId;
    private Integer usuarioId;
    private String nombreCliente;
    private String telefonoCliente;
    private String emailCliente;
    private LocalDate fechaReserva;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer numeroPersonas;
    private BigDecimal precio;
    private String observaciones;
}
