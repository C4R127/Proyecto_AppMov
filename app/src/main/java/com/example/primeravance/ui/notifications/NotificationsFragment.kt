package com.example.primeravance.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.primeravance.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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

        // Recuperamos los datos del bundle
        val nombreApellido = arguments?.getString("nombreApellido") ?: "Sin nombre"
        val turno = arguments?.getString("turno") ?: "Sin turno"
        val mesa = arguments?.getString("mesa") ?: "Sin mesa"
        val fecha = arguments?.getString("fecha") ?: "Sin fecha"
        val hora = arguments?.getString("hora") ?: "Sin hora"

        // Los mostramos en los TextView
        binding.txtEstadoNombreApellido.text = "Cliente: $nombreApellido"
        binding.txtEstadoTurnoReserva.text = "Turno: $turno"
        binding.txtEstadoNumMesa.text = "Mesa: $mesa"
        binding.txtEstadoFecha.text = "Fecha: $fecha"
        binding.txtEstadoHora.text = "Hora: $hora"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}