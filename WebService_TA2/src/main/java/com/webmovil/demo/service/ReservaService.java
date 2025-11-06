package com.webmovil.demo.service;

import com.webmovil.demo.dto.ActualizarReservaRequest;
import com.webmovil.demo.dto.CrearReservaRequest;
import com.webmovil.demo.dto.ReservaDTO;
import com.webmovil.demo.entity.Mesa;
import com.webmovil.demo.entity.Reserva;
import com.webmovil.demo.entity.Reserva.EstadoReserva;
import com.webmovil.demo.repository.MesaRepository;
import com.webmovil.demo.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService {
    
    private final ReservaRepository reservaRepository;
    private final MesaRepository mesaRepository;
    
    @Transactional(readOnly = true)
    public List<ReservaDTO> obtenerTodasLasReservas() {
        return reservaRepository.findAll().stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ReservaDTO obtenerReservaPorId(Integer id) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
        return convertirADTO(reserva);
    }
    
    @Transactional(readOnly = true)
    public List<ReservaDTO> obtenerReservasPorMesa(Integer mesaId) {
        return reservaRepository.findByMesaId(mesaId).stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ReservaDTO> obtenerReservasPorEstado(String estado) {
        EstadoReserva estadoReserva = EstadoReserva.valueOf(estado.toUpperCase());
        return reservaRepository.findByEstado(estadoReserva).stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ReservaDTO> obtenerReservasPorRestaurante(Integer restauranteId, LocalDate desde) {
        return reservaRepository.findReservasPorRestauranteDesde(restauranteId, desde).stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ReservaDTO> obtenerReservasPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return reservaRepository.findReservasPorRangoFechas(fechaInicio, fechaFin).stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ReservaDTO> obtenerReservasPorFecha(Integer mesaId, LocalDate fecha) {
        return reservaRepository.findAll().stream()
            .filter(r -> r.getMesa().getId().equals(mesaId) && r.getFechaReserva().equals(fecha))
            .filter(r -> r.getEstado() != EstadoReserva.CANCELADA)
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public ReservaDTO crearReserva(CrearReservaRequest request) {
        // Validar que la mesa existe
        Mesa mesa = mesaRepository.findById(request.getMesaId())
            .orElseThrow(() -> new RuntimeException("Mesa no encontrada con ID: " + request.getMesaId()));
        
        // Validar que la mesa está disponible
        if (!mesa.getDisponible()) {
            throw new RuntimeException("La mesa no está disponible");
        }
        
        // Validar que la capacidad de la mesa es suficiente
        if (mesa.getCapacidad() < request.getNumeroPersonas()) {
            throw new RuntimeException("La mesa no tiene capacidad suficiente. Capacidad: " 
                + mesa.getCapacidad() + ", Personas: " + request.getNumeroPersonas());
        }
        
        // Validar que la fecha de reserva sea futura
        LocalDateTime fechaHoraReserva = LocalDateTime.of(request.getFechaReserva(), request.getHoraInicio());
        if (fechaHoraReserva.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("La fecha de reserva debe ser futura");
        }
        
        // Validar que horaFin sea después de horaInicio
        if (!request.getHoraFin().isAfter(request.getHoraInicio())) {
            throw new RuntimeException("La hora de fin debe ser posterior a la hora de inicio");
        }
        
        // Verificar que no haya conflictos de horario con otras reservas
        validarConflictosHorario(request.getMesaId(), request.getFechaReserva(), 
            request.getHoraInicio(), request.getHoraFin(), null);
        
        // Crear la reserva
        Reserva reserva = new Reserva();
        reserva.setMesa(mesa);
        reserva.setNombreCliente(request.getNombreCliente());
        reserva.setTelefonoCliente(request.getTelefonoCliente());
        reserva.setEmailCliente(request.getEmailCliente());
        reserva.setFechaReserva(request.getFechaReserva());
        reserva.setHoraInicio(request.getHoraInicio());
        reserva.setHoraFin(request.getHoraFin());
        reserva.setNumeroPersonas(request.getNumeroPersonas());
        reserva.setPrecio(request.getPrecio() != null ? request.getPrecio() : java.math.BigDecimal.ZERO);
        reserva.setObservaciones(request.getObservaciones());
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setFechaCreacion(LocalDateTime.now());
        
        Reserva guardada = reservaRepository.save(reserva);
        return convertirADTO(guardada);
    }
    
    @Transactional
    public ReservaDTO actualizarReserva(Integer id, ActualizarReservaRequest request) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
        
        // No permitir actualizar reservas completadas o canceladas
        if (reserva.getEstado() == EstadoReserva.COMPLETADA) {
            throw new RuntimeException("No se puede modificar una reserva completada");
        }
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new RuntimeException("No se puede modificar una reserva cancelada");
        }
        
        // Si se cambia la mesa, validar que existe
        if (request.getMesaId() != null && !request.getMesaId().equals(reserva.getMesa().getId())) {
            Mesa nuevaMesa = mesaRepository.findById(request.getMesaId())
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada con ID: " + request.getMesaId()));
            
            if (!nuevaMesa.getDisponible()) {
                throw new RuntimeException("La mesa no está disponible");
            }
            
            if (request.getNumeroPersonas() != null && nuevaMesa.getCapacidad() < request.getNumeroPersonas()) {
                throw new RuntimeException("La mesa no tiene capacidad suficiente");
            }
            
            reserva.setMesa(nuevaMesa);
        }
        
        // Validar horarios si se modifican
        LocalDate fechaReserva = request.getFechaReserva() != null ? request.getFechaReserva() : reserva.getFechaReserva();
        LocalTime horaInicio = request.getHoraInicio() != null ? request.getHoraInicio() : reserva.getHoraInicio();
        LocalTime horaFin = request.getHoraFin() != null ? request.getHoraFin() : reserva.getHoraFin();
        
        if (!horaFin.isAfter(horaInicio)) {
            throw new RuntimeException("La hora de fin debe ser posterior a la hora de inicio");
        }
        
        // Verificar conflictos (excluyendo la reserva actual)
        Integer mesaId = request.getMesaId() != null ? request.getMesaId() : reserva.getMesa().getId();
        validarConflictosHorario(mesaId, fechaReserva, horaInicio, horaFin, id);
        
        // Actualizar campos
        if (request.getNombreCliente() != null) reserva.setNombreCliente(request.getNombreCliente());
        if (request.getTelefonoCliente() != null) reserva.setTelefonoCliente(request.getTelefonoCliente());
        if (request.getEmailCliente() != null) reserva.setEmailCliente(request.getEmailCliente());
        if (request.getFechaReserva() != null) reserva.setFechaReserva(request.getFechaReserva());
        if (request.getHoraInicio() != null) reserva.setHoraInicio(request.getHoraInicio());
        if (request.getHoraFin() != null) reserva.setHoraFin(request.getHoraFin());
        if (request.getNumeroPersonas() != null) reserva.setNumeroPersonas(request.getNumeroPersonas());
        if (request.getPrecio() != null) reserva.setPrecio(request.getPrecio());
        if (request.getObservaciones() != null) reserva.setObservaciones(request.getObservaciones());
        
        Reserva actualizada = reservaRepository.save(reserva);
        return convertirADTO(actualizada);
    }
    
    @Transactional
    public ReservaDTO cambiarEstadoReserva(Integer id, String nuevoEstado) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
        
        EstadoReserva estado = EstadoReserva.valueOf(nuevoEstado.toUpperCase());
        reserva.setEstado(estado);
        
        Reserva actualizada = reservaRepository.save(reserva);
        return convertirADTO(actualizada);
    }
    
    @Transactional
    public void cancelarReserva(Integer id) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
        
        if (reserva.getEstado() == EstadoReserva.COMPLETADA) {
            throw new RuntimeException("No se puede cancelar una reserva completada");
        }
        
        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
    }
    
    @Transactional
    public void eliminarReserva(Integer id) {
        if (!reservaRepository.existsById(id)) {
            throw new RuntimeException("Reserva no encontrada con ID: " + id);
        }
        reservaRepository.deleteById(id);
    }
    
    /**
     * Valida que no haya conflictos de horario con otras reservas activas
     */
    private void validarConflictosHorario(Integer mesaId, LocalDate fecha, 
                                         LocalTime horaInicio, LocalTime horaFin, 
                                         Integer reservaIdExcluir) {
        List<Reserva> reservasExistentes = reservaRepository.findAll().stream()
            .filter(r -> r.getMesa().getId().equals(mesaId))
            .filter(r -> r.getFechaReserva().equals(fecha))
            .filter(r -> r.getEstado() != EstadoReserva.CANCELADA)
            .filter(r -> reservaIdExcluir == null || !r.getId().equals(reservaIdExcluir))
            .collect(Collectors.toList());
        
        for (Reserva reserva : reservasExistentes) {
            // Verificar si hay traslape de horarios
            // Un rango se traslapa si: (inicio1 < fin2) AND (fin1 > inicio2)
            if (horaInicio.isBefore(reserva.getHoraFin()) && horaFin.isAfter(reserva.getHoraInicio())) {
                throw new RuntimeException(String.format(
                    "Ya existe una reserva en ese horario. Reserva existente de %s a %s",
                    reserva.getHoraInicio(), reserva.getHoraFin()
                ));
            }
        }
    }
    
    private ReservaDTO convertirADTO(Reserva reserva) {
        ReservaDTO dto = new ReservaDTO();
        dto.setId(reserva.getId());
        dto.setMesaId(reserva.getMesa().getId());
        dto.setNumeroMesa(reserva.getMesa().getNumeroMesa());
        dto.setNombreCliente(reserva.getNombreCliente());
        dto.setTelefonoCliente(reserva.getTelefonoCliente());
        dto.setEmailCliente(reserva.getEmailCliente());
        dto.setFechaReserva(reserva.getFechaReserva());
        dto.setHoraInicio(reserva.getHoraInicio());
        dto.setHoraFin(reserva.getHoraFin());
        dto.setNumeroPersonas(reserva.getNumeroPersonas());
        dto.setPrecio(reserva.getPrecio());
        dto.setEstado(reserva.getEstado().name());
        dto.setObservaciones(reserva.getObservaciones());
        dto.setFechaCreacion(reserva.getFechaCreacion());
        return dto;
    }
}
