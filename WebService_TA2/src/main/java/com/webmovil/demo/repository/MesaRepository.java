package com.webmovil.demo.repository;

import com.webmovil.demo.entity.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Integer> {
    
    List<Mesa> findByRestauranteId(Integer restauranteId);
    
    List<Mesa> findByRestauranteIdAndDisponible(Integer restauranteId, Boolean disponible);
    
    List<Mesa> findByRestauranteIdAndCapacidadGreaterThanEqual(Integer restauranteId, Integer capacidad);
    
    Optional<Mesa> findByRestauranteIdAndNumeroMesa(Integer restauranteId, String numeroMesa);
    
    @Query("SELECT m FROM Mesa m WHERE m.restaurante.id = :restauranteId AND m.disponible = true AND m.capacidad >= :capacidad")
    List<Mesa> findMesasDisponiblesPorCapacidad(@Param("restauranteId") Integer restauranteId, @Param("capacidad") Integer capacidad);
}
