package com.example.primeravance.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.primeravance.adapter.EstadoReservaAdapter
import com.example.primeravance.databinding.FragmentNotificationsBinding
import com.example.primeravance.model.EstadoReserva

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recuperar los datos enviados
        val nombreApellido = arguments?.getString("nombreApellido") ?: "Sin nombre"
        val turno = arguments?.getString("turno") ?: "Sin turno"
        val mesa = arguments?.getString("mesa") ?: "Sin mesa"
        val fecha = arguments?.getString("fecha") ?: "Sin fecha"
        val hora = arguments?.getString("hora") ?: "Sin hora"

        // Crear lista con UNA reserva
        val lista = listOf(
            EstadoReserva(nombreApellido, turno, mesa, fecha, hora)
        )

        // Configurar RecyclerView
        binding.recyclerEstados.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerEstados.adapter = EstadoReservaAdapter(lista)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
