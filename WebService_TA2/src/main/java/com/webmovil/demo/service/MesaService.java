package com.webmovil.demo.service;

import com.webmovil.demo.dto.MesaDTO;
import com.webmovil.demo.entity.Mesa;
import com.webmovil.demo.entity.Restaurante;
import com.webmovil.demo.repository.MesaRepository;
import com.webmovil.demo.repository.RestauranteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MesaService {
    
    private final MesaRepository mesaRepository;
    private final RestauranteRepository restauranteRepository;
    
    @Transactional(readOnly = true)
    public List<MesaDTO> obtenerMesasPorRestaurante(Integer restauranteId) {
        return mesaRepository.findByRestauranteId(restauranteId).stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MesaDTO> obtenerMesasDisponibles(Integer restauranteId) {
        return mesaRepository.findByRestauranteIdAndDisponible(restauranteId, true).stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MesaDTO> obtenerMesasPorCapacidad(Integer restauranteId, Integer capacidad) {
        return mesaRepository.findMesasDisponiblesPorCapacidad(restauranteId, capacidad).stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public MesaDTO obtenerMesaPorId(Integer id) {
        Mesa mesa = mesaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mesa no encontrada con ID: " + id));
        return convertirADTO(mesa);
    }
    
    @Transactional
    public MesaDTO crearMesa(MesaDTO mesaDTO) {
        Restaurante restaurante = restauranteRepository.findById(mesaDTO.getRestauranteId())
            .orElseThrow(() -> new RuntimeException("Restaurante no encontrado con ID: " + mesaDTO.getRestauranteId()));
        
        // Verificar que no exista una mesa con el mismo número en el restaurante
        mesaRepository.findByRestauranteIdAndNumeroMesa(mesaDTO.getRestauranteId(), mesaDTO.getNumeroMesa())
            .ifPresent(m -> {
                throw new RuntimeException("Ya existe una mesa con el número " + mesaDTO.getNumeroMesa() + " en este restaurante");
            });
        
        Mesa mesa = new Mesa();
        mesa.setRestaurante(restaurante);
        mesa.setNumeroMesa(mesaDTO.getNumeroMesa());
        mesa.setCapacidad(mesaDTO.getCapacidad());
        mesa.setDisponible(mesaDTO.getDisponible() != null ? mesaDTO.getDisponible() : true);
        
        Mesa guardada = mesaRepository.save(mesa);
        return convertirADTO(guardada);
    }
    
    @Transactional
    public MesaDTO actualizarMesa(Integer id, MesaDTO mesaDTO) {
        Mesa mesa = mesaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mesa no encontrada con ID: " + id));
        
        mesa.setNumeroMesa(mesaDTO.getNumeroMesa());
        mesa.setCapacidad(mesaDTO.getCapacidad());
        mesa.setDisponible(mesaDTO.getDisponible());
        
        Mesa actualizada = mesaRepository.save(mesa);
        return convertirADTO(actualizada);
    }
    
    @Transactional
    public void eliminarMesa(Integer id) {
        if (!mesaRepository.existsById(id)) {
            throw new RuntimeException("Mesa no encontrada con ID: " + id);
        }
        mesaRepository.deleteById(id);
    }
    
    @Transactional
    public MesaDTO cambiarDisponibilidad(Integer id, Boolean disponible) {
        Mesa mesa = mesaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mesa no encontrada con ID: " + id));
        
        mesa.setDisponible(disponible);
        Mesa actualizada = mesaRepository.save(mesa);
        return convertirADTO(actualizada);
    }
    
    private MesaDTO convertirADTO(Mesa mesa) {
        MesaDTO dto = new MesaDTO();
        dto.setId(mesa.getId());
        dto.setRestauranteId(mesa.getRestaurante().getId());
        dto.setNumeroMesa(mesa.getNumeroMesa());
        dto.setCapacidad(mesa.getCapacidad());
        dto.setDisponible(mesa.getDisponible());
        return dto;
    }
}
