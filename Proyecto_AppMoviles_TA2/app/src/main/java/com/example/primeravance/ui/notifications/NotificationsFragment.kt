package com.example.primeravance.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.primeravance.R
import com.example.primeravance.adapter.ReservaAdapter
import com.example.primeravance.data.SessionManager
import com.example.primeravance.databinding.FragmentNotificationsBinding
import com.example.primeravance.model.Reserva
import com.example.primeravance.network.RetrofitClient
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var reservaAdapter: ReservaAdapter
    private lateinit var sessionManager: SessionManager
    private val allReservas = mutableListOf<Reserva>()
    private var startDateMillis: Long? = null
    private var endDateMillis: Long? = null
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSwipeRefresh()
        setupFilters()
        cargarReservas()
    }

    private fun setupRecyclerView() {
        reservaAdapter = ReservaAdapter(
            mutableListOf(),
            { reserva -> mostrarDialogoEditar(reserva) },
            { reserva -> mostrarDialogoCancelar(reserva) }
        )
        
        binding.rvReservas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reservaAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            cargarReservas()
        }
        
        // Configurar colores del SwipeRefresh
        binding.swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_green_dark,
            android.R.color.holo_blue_dark,
            android.R.color.holo_orange_dark
        )
    }

    private fun setupFilters() {
        binding.chipGroupEstado.setOnCheckedChangeListener { _, _ ->
            aplicarFiltros()
        }

        binding.btnRangoFechas.setOnClickListener {
            mostrarDateRangePicker()
        }
    }

    private fun mostrarDateRangePicker() {
        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Selecciona rango de fechas")
            .build()

        picker.addOnPositiveButtonClickListener { selection ->
            startDateMillis = selection.first
            endDateMillis = selection.second
            binding.btnRangoFechas.text = picker.headerText
            aplicarFiltros()
        }

        picker.addOnNegativeButtonClickListener {
            startDateMillis = null
            endDateMillis = null
            binding.btnRangoFechas.text = "Seleccionar rango de fechas"
            aplicarFiltros()
        }

        picker.show(parentFragmentManager, "DateRangePicker")
    }

    private fun cargarReservas() {
        // No mostrar ProgressBar si es refresh manual
        if (!binding.swipeRefreshLayout.isRefreshing) {
            binding.progressBar.visibility = View.VISIBLE
        }
        
        binding.rvReservas.visibility = View.GONE
        binding.layoutEmpty.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val usuario = sessionManager.getUser()
                if (usuario == null) {
                    Toast.makeText(requireContext(), "Inicia sesión para ver tus reservas", Toast.LENGTH_SHORT).show()
                    binding.layoutEmpty.visibility = View.VISIBLE
                    return@launch
                }

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.reservaApiService.obtenerReservasPorUsuario(usuario.id)
                }

                if (response.isSuccessful) {
                    allReservas.clear()
                    allReservas.addAll(response.body() ?: emptyList())
                    aplicarFiltros()

                    if (allReservas.isEmpty()) {
                        binding.rvReservas.visibility = View.GONE
                        binding.layoutEmpty.visibility = View.VISIBLE
                    } else {
                        binding.rvReservas.visibility = View.VISIBLE
                        binding.layoutEmpty.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error al cargar reservas: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.layoutEmpty.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error de conexión: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                binding.layoutEmpty.visibility = View.VISIBLE
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun aplicarFiltros() {
        var filtradas = allReservas.toList()

        when (binding.chipGroupEstado.checkedChipId) {
            binding.chipProximos.id -> {
                filtradas = filtradas.filter { reserva ->
                    isFutureReservation(reserva)
                }
            }
            binding.chipPasados.id -> {
                filtradas = filtradas.filter { !isFutureReservation(reserva = it) }
            }
            binding.chipCanceladas.id -> {
                filtradas = filtradas.filter { it.estado.equals("CANCELADA", true) }
            }
        }

        if (startDateMillis != null && endDateMillis != null) {
            filtradas = filtradas.filter { reserva ->
                val fecha = runCatching { dateFormatter.parse(reserva.fechaReserva)?.time }.getOrNull()
                fecha != null && fecha in startDateMillis!!..endDateMillis!!
            }
        }

        reservaAdapter.updateReservas(filtradas)

        binding.layoutEmpty.visibility = if (filtradas.isEmpty()) View.VISIBLE else View.GONE
        binding.rvReservas.visibility = if (filtradas.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun isFutureReservation(reserva: Reserva): Boolean {
        return try {
            val fecha = dateFormatter.parse(reserva.fechaReserva)
            val hora = reserva.horaInicio.substring(0, 5)
            val dateTimeStr = "${'$'}{reserva.fechaReserva} ${'$'}hora"
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val dateTime = formatter.parse(dateTimeStr)
            dateTime?.time ?: 0 > System.currentTimeMillis()
        } catch (e: Exception) {
            false
        }
    }

    private fun mostrarDialogoCancelar(reserva: Reserva) {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancelar Reserva")
            .setMessage("¿Estás seguro de que deseas cancelar esta reserva?")
            .setPositiveButton("Sí") { _, _ ->
                cancelarReserva(reserva)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun mostrarDialogoEditar(reserva: Reserva) {
        // Inflar el layout personalizado
        val dialogView = layoutInflater.inflate(R.layout.dialog_editar_reserva, null)
        
        // Crear el diálogo
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Editar Reserva #${reserva.id}")
            .setView(dialogView)
            .setNegativeButton("CANCELAR", null)
            .create()
        
        // Configurar botones
        val btnVerDetalles = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnVerDetalles)
        val btnCambiarEstado = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCambiarEstado)
        
        btnVerDetalles.setOnClickListener {
            dialog.dismiss()
            mostrarDetallesReserva(reserva)
        }
        
        btnCambiarEstado.setOnClickListener {
            dialog.dismiss()
            cambiarEstadoReserva(reserva.id, "CONFIRMADA")
        }
        
        dialog.show()
    }
    
    private fun mostrarDetallesReserva(reserva: Reserva) {
        val mensaje = """
            Cliente: ${reserva.nombreCliente}
            Teléfono: ${reserva.telefonoCliente}
            Email: ${reserva.emailCliente}
            Fecha: ${reserva.fechaReserva}
            Horario: ${reserva.horaInicio.substring(0, 5)} - ${reserva.horaFin.substring(0, 5)}
            Personas: ${reserva.numeroPersonas}
            Mesa: ${reserva.mesaNumero ?: reserva.mesaId}
            Estado: ${reserva.estado}
            Observaciones: ${reserva.observaciones ?: "Ninguna"}
        """.trimIndent()
        
        AlertDialog.Builder(requireContext())
            .setTitle("Detalles de Reserva #${reserva.id}")
            .setMessage(mensaje)
            .setPositiveButton("Cerrar", null)
            .show()
    }
    
    private fun cambiarEstadoReserva(reservaId: Int, nuevoEstado: String) {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.reservaApiService.cambiarEstadoReserva(reservaId, nuevoEstado)
                }

                binding.progressBar.visibility = View.GONE
                
                if (response.isSuccessful) {
                    // ✅ Actualización inmediata sin recargar toda la lista
                    actualizarReservaLocal(reservaId, nuevoEstado)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error al cambiar estado: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Error de conexión: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun cancelarReserva(reserva: Reserva) {
        // Mostrar indicador de carga
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.reservaApiService.cancelarReserva(reserva.id)
                }

                binding.progressBar.visibility = View.GONE
                
                // El backend devuelve código 200 con texto plano si es exitoso
                if (response.isSuccessful) {
                    // ✅ Actualización inmediata sin recargar toda la lista
                    actualizarReservaLocal(reserva.id, "CANCELADA")
                    val mensaje = response.body()?.ifBlank { null }
                        ?: getString(R.string.reservation_cancel_success)
                    Toast.makeText(
                        requireContext(),
                        mensaje,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error al cancelar reserva: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Error de conexión: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun actualizarReservaLocal(reservaId: Int, nuevoEstado: String) {
        val index = allReservas.indexOfFirst { it.id == reservaId }
        if (index != -1) {
            val copia = allReservas[index].copy(estado = nuevoEstado)
            allReservas[index] = copia
            aplicarFiltros()
        } else {
            reservaAdapter.updateReserva(reservaId, nuevoEstado)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
