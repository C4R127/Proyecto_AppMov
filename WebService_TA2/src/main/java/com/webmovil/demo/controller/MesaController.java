package com.webmovil.demo.controller;

import com.webmovil.demo.dto.MesaDTO;
import com.webmovil.demo.service.MesaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MesaController {
    
    private final MesaService mesaService;
    
    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<List<MesaDTO>> obtenerPorRestaurante(@PathVariable Integer restauranteId) {
        return ResponseEntity.ok(mesaService.obtenerMesasPorRestaurante(restauranteId));
    }
    
    @GetMapping("/restaurante/{restauranteId}/disponibles")
    public ResponseEntity<List<MesaDTO>> obtenerDisponibles(@PathVariable Integer restauranteId) {
        return ResponseEntity.ok(mesaService.obtenerMesasDisponibles(restauranteId));
    }
    
    @GetMapping("/restaurante/{restauranteId}/capacidad/{capacidad}")
    public ResponseEntity<List<MesaDTO>> obtenerPorCapacidad(
            @PathVariable Integer restauranteId, 
            @PathVariable Integer capacidad) {
        return ResponseEntity.ok(mesaService.obtenerMesasPorCapacidad(restauranteId, capacidad));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MesaDTO> obtenerPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(mesaService.obtenerMesaPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody MesaDTO mesaDTO) {
        try {
            MesaDTO creada = mesaService.crearMesa(mesaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody MesaDTO mesaDTO) {
        try {
            MesaDTO actualizada = mesaService.actualizarMesa(id, mesaDTO);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<?> cambiarDisponibilidad(
            @PathVariable Integer id, 
            @RequestParam Boolean disponible) {
        try {
            MesaDTO actualizada = mesaService.cambiarDisponibilidad(id, disponible);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            mesaService.eliminarMesa(id);
            return ResponseEntity.ok("Mesa eliminada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
