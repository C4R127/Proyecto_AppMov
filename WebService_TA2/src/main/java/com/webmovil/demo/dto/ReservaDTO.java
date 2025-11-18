package com.webmovil.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDTO {
    private Integer id;
    private Integer mesaId;
    private Integer usuarioId;
    private String numeroMesa;
    private String nombreUsuario;
    private String nombreCliente;
    private String telefonoCliente;
    private String emailCliente;
    private LocalDate fechaReserva;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer numeroPersonas;
    private BigDecimal precio;
    private String estado;
    private String observaciones;
    private LocalDateTime fechaCreacion;
}
