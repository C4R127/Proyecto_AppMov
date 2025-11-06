package com.webmovil.demo.controller;

import com.webmovil.demo.dto.RestauranteDTO;
import com.webmovil.demo.service.RestauranteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurantes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RestauranteController {
    
    private final RestauranteService restauranteService;
    
    @GetMapping
    public ResponseEntity<List<RestauranteDTO>> obtenerTodos() {
        return ResponseEntity.ok(restauranteService.obtenerTodosLosRestaurantes());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RestauranteDTO> obtenerPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(restauranteService.obtenerRestaurantePorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody RestauranteDTO restauranteDTO) {
        try {
            RestauranteDTO creado = restauranteService.crearRestaurante(restauranteDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody RestauranteDTO restauranteDTO) {
        try {
            RestauranteDTO actualizado = restauranteService.actualizarRestaurante(id, restauranteDTO);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            restauranteService.eliminarRestaurante(id);
            return ResponseEntity.ok("Restaurante eliminado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
