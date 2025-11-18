package com.webmovil.demo.repository;

import com.webmovil.demo.entity.Reserva;
import com.webmovil.demo.entity.Reserva.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    
    List<Reserva> findByMesaId(Integer mesaId);
    
    List<Reserva> findByEstado(EstadoReserva estado);
    
    List<Reserva> findByTelefonoCliente(String telefonoCliente);
    
    List<Reserva> findByEmailCliente(String emailCliente);
    
    List<Reserva> findByUsuarioId(Integer usuarioId);
    
    // Buscar reservas por mesa y fecha específica
    @Query("SELECT r FROM Reserva r WHERE r.mesa.id = :mesaId AND r.fechaReserva = :fecha AND r.estado != 'CANCELADA'")
    List<Reserva> findReservasActivasPorMesaYFecha(
        @Param("mesaId") Integer mesaId, 
        @Param("fecha") LocalDate fecha
    );
    
    // Buscar reservas de un restaurante desde una fecha
    @Query("SELECT r FROM Reserva r WHERE r.mesa.restaurante.id = :restauranteId AND r.fechaReserva >= :fecha ORDER BY r.fechaReserva ASC, r.horaInicio ASC")
    List<Reserva> findReservasPorRestauranteDesde(
        @Param("restauranteId") Integer restauranteId, 
        @Param("fecha") LocalDate fecha
    );
    
    // Buscar reservas por rango de fechas
    @Query("SELECT r FROM Reserva r WHERE r.fechaReserva BETWEEN :fechaInicio AND :fechaFin ORDER BY r.fechaReserva ASC, r.horaInicio ASC")
    List<Reserva> findReservasPorRangoFechas(
        @Param("fechaInicio") LocalDate fechaInicio, 
        @Param("fechaFin") LocalDate fechaFin
    );
}
