package com.webmovil.demo.repository;

import com.webmovil.demo.entity.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Integer> {
    
    Optional<Restaurante> findByNombre(String nombre);
    
    boolean existsByNombre(String nombre);
}
