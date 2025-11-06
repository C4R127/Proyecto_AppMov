package com.webmovil.demo.controller;

import com.webmovil.demo.dto.ActualizarReservaRequest;
import com.webmovil.demo.dto.CrearReservaRequest;
import com.webmovil.demo.dto.ReservaDTO;
import com.webmovil.demo.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReservaController {
    
    private final ReservaService reservaService;
    
    @GetMapping
    public ResponseEntity<List<ReservaDTO>> obtenerTodas() {
        return ResponseEntity.ok(reservaService.obtenerTodasLasReservas());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> obtenerPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(reservaService.obtenerReservaPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/mesa/{mesaId}")
    public ResponseEntity<List<ReservaDTO>> obtenerPorMesa(@PathVariable Integer mesaId) {
        return ResponseEntity.ok(reservaService.obtenerReservasPorMesa(mesaId));
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ReservaDTO>> obtenerPorEstado(@PathVariable String estado) {
        try {
            return ResponseEntity.ok(reservaService.obtenerReservasPorEstado(estado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<List<ReservaDTO>> obtenerPorRestaurante(
            @PathVariable Integer restauranteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde) {
        LocalDate fecha = (desde != null) ? desde : LocalDate.now();
        return ResponseEntity.ok(reservaService.obtenerReservasPorRestaurante(restauranteId, fecha));
    }
    
    @GetMapping("/rango-fechas")
    public ResponseEntity<List<ReservaDTO>> obtenerPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(reservaService.obtenerReservasPorRangoFechas(fechaInicio, fechaFin));
    }
    
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearReservaRequest request) {
        try {
            ReservaDTO creada = reservaService.crearReserva(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody ActualizarReservaRequest request) {
        try {
            ReservaDTO actualizada = reservaService.actualizarReserva(id, request);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Integer id, 
            @RequestParam String estado) {
        try {
            ReservaDTO actualizada = reservaService.cambiarEstadoReserva(id, estado);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Integer id) {
        try {
            reservaService.cancelarReserva(id);
            return ResponseEntity.ok("Reserva cancelada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            reservaService.eliminarReserva(id);
            return ResponseEntity.ok("Reserva eliminada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
