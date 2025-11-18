package com.example.primeravance.ui.mesas

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.primeravance.R
import com.example.primeravance.adapter.MesaGridAdapter
import com.example.primeravance.adapter.MesaSlot
import com.example.primeravance.data.SessionManager
import com.example.primeravance.databinding.DialogReviewBinding
import com.example.primeravance.databinding.FragmentMesasBinding
import com.example.primeravance.model.Mesa
import com.example.primeravance.model.ReservaRequest
import com.example.primeravance.model.Restaurante
import com.example.primeravance.model.ReviewRequest
import com.example.primeravance.network.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MesasFragment : Fragment() {

    private var _binding: FragmentMesasBinding? = null
    private val binding get() = _binding!!
    
    private val calendar = Calendar.getInstance()
    
    private var restaurante: Restaurante? = null
    private var mesasDisponibles: List<Mesa> = emptyList()
    private var mesaSlots: List<MesaSlot> = emptyList()
    private var mesaSeleccionada: Mesa? = null
    private lateinit var sessionManager: SessionManager
    
    // Horarios de reserva disponibles (10 AM - 10 PM)
    private val horariosDisponibles = listOf(
        "10:00 - 11:00",
        "11:00 - 12:00",
        "12:00 - 13:00",
        "13:00 - 14:00",
        "14:00 - 15:00",
        "15:00 - 16:00",
        "16:00 - 17:00",
        "17:00 - 18:00",
        "18:00 - 19:00",
        "19:00 - 20:00",
        "20:00 - 21:00",
        "21:00 - 22:00"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMesasBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Obtener el restaurante pasado como argumento
        restaurante = arguments?.let {
            Restaurante(
                id = it.getInt("restauranteId"),
                nombre = it.getString("restauranteNombre", ""),
                direccion = it.getString("restauranteDireccion", ""),
                telefono = it.getString("restauranteTelefono", ""),
                descripcion = it.getString("restauranteDescripcion") ,
                imageRes = R.drawable.restaurante_pf,
                imagenUrl = it.getString("restauranteImagenUrl"),
                miniaturaUrl = it.getString("restauranteMiniaturaUrl")
            )
        }
        
        setupUI()
        prefillClientData()
        setupClickListeners()
        loadMesas()
    }

    private fun setupUI() {
        restaurante?.let {
            val ratingInfo = if (it.ratingPromedio != null && it.totalResenas != null) {
                "${'$'}{it.nombre} (${String.format(Locale.getDefault(), "%.1f", it.ratingPromedio)} ⭐ · ${it.totalResenas} reseñas)"
            } else {
                it.nombre
            }
            binding.tvRestauranteNombre.text = ratingInfo
            binding.tvRestauranteDireccion.text = it.direccion
        }
    }

    private fun setupClickListeners() {
        binding.etFecha.setOnClickListener {
            showDatePicker()
        }

        binding.etHora.setOnClickListener {
            showTimePicker()
        }

        binding.btnSeleccionarMesa.setOnClickListener {
            if (mesaSlots.isEmpty()) {
                mesaSlots = buildMesaSlots(emptyList())
            }
            showMesaSelectionDialog()
        }

        // Validar cuando cambie el número de personas
        binding.etNumeroPersonas.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateForm()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        // Validar cuando cambien los datos del cliente
        val textWatcher = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateForm()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        }
        
        binding.etNombreCliente.addTextChangedListener(textWatcher)
        binding.etTelefonoCliente.addTextChangedListener(textWatcher)
        binding.etEmailCliente.addTextChangedListener(textWatcher)

        binding.btnConfirmarReserva.setOnClickListener {
            confirmarReserva()
        }
    }

    private fun prefillClientData() {
        val user = sessionManager.getUser()
        user?.let {
            binding.etNombreCliente.setText(it.nombre)
            binding.etNombreCliente.isEnabled = false
            binding.etNombreCliente.alpha = 0.6f

            binding.etEmailCliente.setText(it.email)
            binding.etEmailCliente.isEnabled = false
            binding.etEmailCliente.alpha = 0.6f

            if (!it.telefono.isNullOrBlank()) {
                binding.etTelefonoCliente.setText(it.telefono)
            } else {
                binding.etTelefonoCliente.setText("")
                binding.etTelefonoCliente.hint = getString(R.string.ingresa_telefono_contacto)
            }
        }
    }

    private fun loadMesas() {
        val restauranteId = restaurante?.id ?: return
        
        showLoading(true)
        
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.mesaApiService.getMesasByRestaurante(restauranteId)
                
                showLoading(false)
                
                if (response.isSuccessful && response.body() != null) {
                    val mesas = response.body()!!
                    mesasDisponibles = mesas.take(15)
                    mesaSlots = buildMesaSlots(mesasDisponibles)

                    if (mesasDisponibles.isEmpty()) {
                        showEmptyState(true)
                    } else {
                        showEmptyState(false)
                        binding.tvMesaSeleccionada.text = getString(R.string.mesa_no_seleccionada)
                        mesaSeleccionada = null
                        validateForm()
                    }
                } else {
                    showEmptyState(true)
                    Toast.makeText(
                        requireContext(),
                        "Error al cargar mesas: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                showLoading(false)
                showEmptyState(true)
                Toast.makeText(
                    requireContext(),
                    "Error de conexión: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun recargarMesasConDisponibilidad() {
        val fecha = binding.etFecha.text.toString()
        val horarioCompleto = binding.etHora.text.toString()
        val restauranteId = restaurante?.id ?: return
        
        if (fecha.isEmpty() || horarioCompleto.isEmpty()) {
            // Si no hay fecha u hora, cargar todas las mesas sin filtro
            loadMesas()
            return
        }
        
        // Extraer horas del rango
        val horasParts = horarioCompleto.split(" - ")
        if (horasParts.size != 2) return
        
        val horaInicio = horasParts[0].trim()
        val horaFin = horasParts[1].trim()
        
        // Convertir fecha a formato yyyy-MM-dd
        val fechaFormateada = try {
            val formatoEntrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formatoSalida = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = formatoEntrada.parse(fecha)
            formatoSalida.format(date!!)
        } catch (e: Exception) {
            loadMesas()
            return
        }
        
        showLoading(true)
        
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.mesaApiService.getMesasDisponiblesPorHorario(
                    restauranteId,
                    fechaFormateada,
                    "$horaInicio:00",
                    "$horaFin:00"
                )
                
                showLoading(false)
                
                if (response.isSuccessful && response.body() != null) {
                    val mesas = response.body()!!
                    mesasDisponibles = mesas.take(15)
                    mesaSlots = buildMesaSlots(mesasDisponibles)

                    if (mesasDisponibles.isEmpty()) {
                        showEmptyState(true)
                        Toast.makeText(
                            requireContext(),
                            "No hay mesas disponibles en este horario",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        showEmptyState(false)
                        binding.tvMesaSeleccionada.text = getString(R.string.mesa_no_seleccionada)
                        mesaSeleccionada = null
                        validateForm()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error al verificar disponibilidad: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadMesas()
                }
            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(
                    requireContext(),
                    "Error al verificar disponibilidad: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                loadMesas()
            }
        }
    }

    private fun onMesaSelected(mesa: Mesa) {
        mesaSeleccionada = mesa
        binding.tvMesaSeleccionada.text = getString(
            R.string.mesa_seleccionada_formato,
            mesa.numeroMesa ?: mesa.numero.toString(),
            mesa.capacidad
        )
        validateForm()
    }

    /**
     * Genera exactamente 15 espacios para el selector de mesas combinando mesas reales
     * con placeholders visuales cuando el backend devuelve menos registros.
     */
    private fun buildMesaSlots(mesas: List<Mesa>): List<MesaSlot> {
        val slots = mutableListOf<MesaSlot>()
        val usedLabels = mutableSetOf<String>()
        val limit = 15

        mesas.take(limit).forEachIndexed { index, mesa ->
            val label = when {
                !mesa.numeroMesa.isNullOrBlank() -> mesa.numeroMesa!!
                mesa.numero > 0 -> "M-${mesa.numero.toString().padStart(2, '0')}"
                else -> "M-${(index + 1).toString().padStart(2, '0')}"
            }
            usedLabels.add(label)
            slots.add(
                MesaSlot(
                    id = mesa.id,
                    label = label,
                    capacidad = mesa.capacidad,
                    disponible = mesa.disponible && !mesa.ocupada,
                    isPlaceholder = false,
                    mesa = mesa
                )
            )
        }

        var placeholderIndex = 1
        while (slots.size < limit) {
            var label: String
            do {
                label = "M-${placeholderIndex.toString().padStart(2, '0')}"
                placeholderIndex++
            } while (usedLabels.contains(label))

            usedLabels.add(label)
            slots.add(
                MesaSlot(
                    id = -1000 - slots.size,
                    label = label,
                    capacidad = 0,
                    disponible = false,
                    isPlaceholder = true,
                    mesa = null
                )
            )
        }

        return slots
    }

    private fun showMesaSelectionDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_select_mesa, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rvMesasGrid)
        val btnCerrar = dialogView.findViewById<Button>(R.id.btnCerrar)

        lateinit var dialog: AlertDialog

        val adapter = MesaGridAdapter { mesa ->
            onMesaSelected(mesa)
            dialog.dismiss()
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(false)

        val slots = if (mesaSlots.isEmpty()) buildMesaSlots(emptyList()) else mesaSlots
        adapter.setMesas(slots, mesaSeleccionada?.id)

        dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnCerrar.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.etFecha.setText(dateFormat.format(calendar.time))
                validateForm()
                recargarMesasConDisponibilidad()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        // No permitir fechas pasadas
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Selecciona el horario de reserva")
        
        val horariosArray = horariosDisponibles.toTypedArray()
        
        builder.setItems(horariosArray) { dialog, which ->
            val horarioSeleccionado = horariosArray[which]
            binding.etHora.setText(horarioSeleccionado)
            validateForm()
            recargarMesasConDisponibilidad()
        }
        
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun validateForm() {
        val nombre = binding.etNombreCliente.text.toString().trim()
        val telefono = binding.etTelefonoCliente.text.toString().trim()
        val email = binding.etEmailCliente.text.toString().trim()
        val fecha = binding.etFecha.text.toString()
        val hora = binding.etHora.text.toString()
        val numeroPersonas = binding.etNumeroPersonas.text.toString()
        val mesaSeleccionada = this.mesaSeleccionada
        
        val isValid = nombre.isNotEmpty() && 
                     telefono.isNotEmpty() &&
                     email.isNotEmpty() &&
                     fecha.isNotEmpty() && 
                     hora.isNotEmpty() && 
                     numeroPersonas.isNotEmpty() && 
                     mesaSeleccionada != null
        
        binding.btnConfirmarReserva.isEnabled = isValid
    }

    private fun confirmarReserva() {
        val nombreCliente = binding.etNombreCliente.text.toString().trim()
        val telefonoCliente = binding.etTelefonoCliente.text.toString().trim()
        val emailCliente = binding.etEmailCliente.text.toString().trim()
        val fecha = binding.etFecha.text.toString()
        val horarioCompleto = binding.etHora.text.toString()
    val numeroPersonas = binding.etNumeroPersonas.text.toString().toIntOrNull() ?: 0
    val mesa = mesaSeleccionada
        
        if (mesa == null) {
            Toast.makeText(requireContext(), "Selecciona una mesa", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Validar email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailCliente).matches()) {
            binding.etEmailCliente.error = "Email inválido"
            return
        }
        
        // Extraer la hora de inicio y fin del rango (ej: "10:00" y "11:00" de "10:00 - 11:00")
        val horasParts = horarioCompleto.split(" - ")
        if (horasParts.size != 2) {
            Toast.makeText(requireContext(), "Selecciona un horario válido", Toast.LENGTH_SHORT).show()
            return
        }
        
        val horaInicio = horasParts[0].trim()
        val horaFin = horasParts[1].trim()
        
        // Convertir fecha a formato yyyy-MM-dd
        val fechaFormateada = try {
            val formatoEntrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formatoSalida = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = formatoEntrada.parse(fecha)
            formatoSalida.format(date!!)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Formato de fecha inválido", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Deshabilitar botón y mostrar loading
        binding.btnConfirmarReserva.isEnabled = false
        showLoading(true)
        
        lifecycleScope.launch {
            try {
                val request = ReservaRequest(
                    mesaId = mesa.id,
                    nombreCliente = nombreCliente,
                    telefonoCliente = telefonoCliente,
                    emailCliente = emailCliente,
                    fechaReserva = fechaFormateada,
                    horaInicio = "$horaInicio:00",
                    horaFin = "$horaFin:00",
                    numeroPersonas = numeroPersonas,
                    precio = 0.0,
                    observaciones = "Reserva desde app móvil",
                    usuarioId = sessionManager.getUser()?.id
                )
                
                val response = RetrofitClient.reservaApiService.crearReserva(request)
                
                showLoading(false)
                binding.btnConfirmarReserva.isEnabled = true
                
                // El backend devuelve código 201 con el objeto Reserva directamente (no ReservaResponse)
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(
                        requireContext(),
                        "¡Reserva confirmada para $horarioCompleto!",
                        Toast.LENGTH_LONG
                    ).show()
                    
                    val restauranteId = restaurante?.id
                    if (restauranteId != null) {
                        showReviewDialog(restauranteId)
                    } else {
                        navigateToNotifications()
                    }
                } else if (response.code() == 400) {
                    // Error 400: ya existe una reserva
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    Toast.makeText(
                        requireContext(),
                        errorBody,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error al crear reserva: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                showLoading(false)
                binding.btnConfirmarReserva.isEnabled = true
                Toast.makeText(
                    requireContext(),
                    "Error de conexión: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showReviewDialog(restauranteId: Int) {
        val dialogBinding = DialogReviewBinding.inflate(LayoutInflater.from(requireContext()))
        dialogBinding.tvReviewTitle.text = getString(R.string.review_dialog_title)
        dialogBinding.tvReviewSubtitle.text = getString(
            R.string.review_dialog_subtitle,
            restaurante?.nombre ?: getString(R.string.app_name)
        )

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialogBinding.btnSkip.setOnClickListener {
            dialog.dismiss()
            navigateToNotifications()
        }

        dialogBinding.btnSubmit.setOnClickListener {
            val rating = dialogBinding.ratingBar.rating
            val comentario = dialogBinding.etComment.text.toString().trim()

            if (rating == 0f) {
                Toast.makeText(requireContext(), getString(R.string.review_dialog_rating_required), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            submitReview(restauranteId, rating, comentario, dialogBinding, dialog)
        }

        dialog.show()
    }

    private fun submitReview(
        restauranteId: Int,
        rating: Float,
        comentario: String,
        dialogBinding: DialogReviewBinding,
        dialog: AlertDialog
    ) {
        val usuario = sessionManager.getUser()
        if (usuario == null) {
            Toast.makeText(requireContext(), getString(R.string.review_dialog_login_required), Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            navigateToNotifications()
            return
        }

        dialogBinding.btnSubmit.isEnabled = false
        dialogBinding.btnSkip.isEnabled = false

        lifecycleScope.launch {
            try {
                val request = ReviewRequest(
                    restauranteId = restauranteId,
                    usuarioId = usuario.id,
                    comentario = comentario,
                    rating = rating
                )
                val response = RetrofitClient.reviewApiService.submitReview(restauranteId, request)

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), getString(R.string.review_dialog_thanks), Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    navigateToNotifications()
                } else {
                    dialogBinding.btnSubmit.isEnabled = true
                    dialogBinding.btnSkip.isEnabled = true
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.review_dialog_error_code, response.code()),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                dialogBinding.btnSubmit.isEnabled = true
                dialogBinding.btnSkip.isEnabled = true
                Toast.makeText(requireContext(), "Error: ${'$'}{e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToNotifications() {
        findNavController().navigate(R.id.navigation_notifications)
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showEmptyState(show: Boolean) {
        binding.layoutEmpty.visibility = if (show) View.VISIBLE else View.GONE
        if (show) {
            binding.tvMesaSeleccionada.text = getString(R.string.mesa_no_seleccionada)
            mesaSeleccionada = null
            mesasDisponibles = emptyList()
            mesaSlots = buildMesaSlots(emptyList())
            validateForm()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
