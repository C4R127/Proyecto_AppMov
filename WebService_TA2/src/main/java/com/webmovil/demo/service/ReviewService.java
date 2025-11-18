package com.webmovil.demo.service;

import com.webmovil.demo.dto.CrearReviewRequest;
import com.webmovil.demo.dto.ReviewDTO;
import com.webmovil.demo.entity.Restaurante;
import com.webmovil.demo.entity.Review;
import com.webmovil.demo.entity.Usuario;
import com.webmovil.demo.repository.RestauranteRepository;
import com.webmovil.demo.repository.ReviewRepository;
import com.webmovil.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<ReviewDTO> obtenerReviewsPorRestaurante(Integer restauranteId) {
        return reviewRepository.findByRestauranteIdOrderByFechaCreacionDesc(restauranteId).stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public ReviewDTO crearReview(Integer restauranteId, CrearReviewRequest request) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
            .orElseThrow(() -> new RuntimeException("Restaurante no encontrado con ID: " + restauranteId));

        if (request.getCalificacion() == null || request.getCalificacion() < 1 || request.getCalificacion() > 5) {
            throw new RuntimeException("La calificación debe estar entre 1 y 5");
        }

        Review review = new Review();
        review.setRestaurante(restaurante);
        review.setComentario(request.getComentario());
        review.setCalificacion(request.getCalificacion());
        review.setFechaCreacion(LocalDateTime.now());

        if (request.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + request.getUsuarioId()));
            review.setUsuario(usuario);
            // Si no se envía nombreCliente, usar nombre del usuario
            if (request.getNombreCliente() == null || request.getNombreCliente().isBlank()) {
                review.setNombreCliente(usuario.getNombre());
            } else {
                review.setNombreCliente(request.getNombreCliente());
            }
        } else {
            if (request.getNombreCliente() == null || request.getNombreCliente().isBlank()) {
                throw new RuntimeException("Debe proporcionar el nombre del cliente o un usuario válido");
            }
            review.setNombreCliente(request.getNombreCliente());
        }

        Review guardado = reviewRepository.save(review);
        return convertirADTO(guardado);
    }

    private ReviewDTO convertirADTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setRestauranteId(review.getRestaurante().getId());
        dto.setComentario(review.getComentario());
        dto.setCalificacion(review.getCalificacion());
        dto.setFechaCreacion(review.getFechaCreacion());
        if (review.getUsuario() != null) {
            dto.setUsuarioId(review.getUsuario().getId());
            dto.setNombreAutor(review.getUsuario().getNombre());
        } else {
            dto.setNombreAutor(review.getNombreCliente());
        }
        return dto;
    }
}
