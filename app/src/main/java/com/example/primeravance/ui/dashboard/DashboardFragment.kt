package com.example.primeravance.ui.dashboard

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.primeravance.databinding.FragmentDashboardBinding
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinner()
        setupClickListeners()
    }

    private fun setupSpinner() {
        // Setup mesa spinner with tables 1-10
        val mesas = (1..10).map { "Mesa $it" }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mesas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMesa.adapter = adapter
    }

    private fun setupClickListeners() {
        // Date picker
        binding.etFecha.setOnClickListener {
            showDatePicker()
        }

        // Time picker
        binding.etHora.setOnClickListener {
            showTimePicker()
        }

        // Save button
        binding.btnGuardar.setOnClickListener {
            guardarReserva()
        }
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
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        // Don't allow past dates
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                binding.etHora.setText(timeFormat.format(calendar.time))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun guardarReserva() {
        val nombre = binding.etNombre.text.toString().trim()
        val apellido = binding.etApellido.text.toString().trim()
        val fecha = binding.etFecha.text.toString().trim()
        val hora = binding.etHora.text.toString().trim()
        
        // Get selected turno
        val turnoSeleccionado = when (binding.rgTurno.checkedRadioButtonId) {
            binding.rbManana.id -> "Mañana"
            binding.rbTarde.id -> "Tarde"
            binding.rbNoche.id -> "Noche"
            else -> ""
        }
        
        // Get selected mesa
        val mesaSeleccionada = binding.spinnerMesa.selectedItem?.toString() ?: ""

        // Validate form
        if (validateForm(nombre, apellido, turnoSeleccionado, mesaSeleccionada, fecha, hora)) {
            // Show confirmation
            val mensaje = """
                Reserva guardada exitosamente:
                
                Nombre: $nombre $apellido
                Turno: $turnoSeleccionado
                Mesa: $mesaSeleccionada
                Fecha: $fecha
                Hora: $hora
            """.trimIndent()
            
            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
            
            // Clear form
            clearForm()
        }
    }

    private fun validateForm(nombre: String, apellido: String, turno: String, mesa: String, fecha: String, hora: String): Boolean {
        var isValid = true

        if (nombre.isEmpty()) {
            binding.etNombre.error = "El nombre es requerido"
            isValid = false
        }

        if (apellido.isEmpty()) {
            binding.etApellido.error = "El apellido es requerido"
            isValid = false
        }

        if (turno.isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona un turno", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (fecha.isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona una fecha", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (hora.isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona una hora", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun clearForm() {
        binding.etNombre.setText("")
        binding.etApellido.setText("")
        binding.etFecha.setText("")
        binding.etHora.setText("")
        binding.rgTurno.clearCheck()
        binding.spinnerMesa.setSelection(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
