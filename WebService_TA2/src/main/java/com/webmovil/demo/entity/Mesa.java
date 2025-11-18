package com.webmovil.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "mesas", uniqueConstraints = {
    @UniqueConstraint(name = "unique_mesa_restaurante", columnNames = {"restaurante_id", "numero_mesa"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mesa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;
    
    @Column(name = "numero_mesa", nullable = false, length = 10)
    private String numeroMesa;
    
    @Column(nullable = false)
    private Integer capacidad;
    
    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean disponible = true;
    
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;
    
    @OneToMany(mappedBy = "mesa", cascade = CascadeType.ALL)
    private List<Reserva> reservas;
}
