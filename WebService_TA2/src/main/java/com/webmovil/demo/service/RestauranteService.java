package com.webmovil.demo.service;

import com.webmovil.demo.dto.RestauranteDTO;
import com.webmovil.demo.entity.Restaurante;
import com.webmovil.demo.repository.RestauranteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestauranteService {
    
    private final RestauranteRepository restauranteRepository;
    
    @Transactional(readOnly = true)
    public List<RestauranteDTO> obtenerTodosLosRestaurantes() {
        return restauranteRepository.findAll().stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public RestauranteDTO obtenerRestaurantePorId(Integer id) {
        Restaurante restaurante = restauranteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Restaurante no encontrado con ID: " + id));
        return convertirADTO(restaurante);
    }
    
    @Transactional
    public RestauranteDTO crearRestaurante(RestauranteDTO restauranteDTO) {
        if (restauranteRepository.existsByNombre(restauranteDTO.getNombre())) {
            throw new RuntimeException("Ya existe un restaurante con el nombre: " + restauranteDTO.getNombre());
        }
        
        Restaurante restaurante = new Restaurante();
        restaurante.setNombre(restauranteDTO.getNombre());
        restaurante.setDireccion(restauranteDTO.getDireccion());
        restaurante.setTelefono(restauranteDTO.getTelefono());
        restaurante.setEmail(restauranteDTO.getEmail());
        restaurante.setImagenUrl(restauranteDTO.getImagenUrl());
        restaurante.setImagenThumbnailUrl(restauranteDTO.getImagenThumbnailUrl());
        restaurante.setHoraApertura(restauranteDTO.getHoraApertura());
        restaurante.setHoraCierre(restauranteDTO.getHoraCierre());
        
        Restaurante guardado = restauranteRepository.save(restaurante);
        return convertirADTO(guardado);
    }
    
    @Transactional
    public RestauranteDTO actualizarRestaurante(Integer id, RestauranteDTO restauranteDTO) {
        Restaurante restaurante = restauranteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Restaurante no encontrado con ID: " + id));
        
        restaurante.setNombre(restauranteDTO.getNombre());
        restaurante.setDireccion(restauranteDTO.getDireccion());
        restaurante.setTelefono(restauranteDTO.getTelefono());
        restaurante.setEmail(restauranteDTO.getEmail());
        restaurante.setImagenUrl(restauranteDTO.getImagenUrl());
        restaurante.setImagenThumbnailUrl(restauranteDTO.getImagenThumbnailUrl());
        restaurante.setHoraApertura(restauranteDTO.getHoraApertura());
        restaurante.setHoraCierre(restauranteDTO.getHoraCierre());
        
        Restaurante actualizado = restauranteRepository.save(restaurante);
        return convertirADTO(actualizado);
    }
    
    @Transactional
    public void eliminarRestaurante(Integer id) {
        if (!restauranteRepository.existsById(id)) {
            throw new RuntimeException("Restaurante no encontrado con ID: " + id);
        }
        restauranteRepository.deleteById(id);
    }
    
    private RestauranteDTO convertirADTO(Restaurante restaurante) {
        RestauranteDTO dto = new RestauranteDTO();
        dto.setId(restaurante.getId());
        dto.setNombre(restaurante.getNombre());
        dto.setDireccion(restaurante.getDireccion());
        dto.setTelefono(restaurante.getTelefono());
        dto.setEmail(restaurante.getEmail());
        dto.setImagenUrl(restaurante.getImagenUrl());
        dto.setImagenThumbnailUrl(restaurante.getImagenThumbnailUrl());
        dto.setHoraApertura(restaurante.getHoraApertura());
        dto.setHoraCierre(restaurante.getHoraCierre());
        return dto;
    }
}
