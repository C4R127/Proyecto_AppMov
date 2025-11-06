package com.example.primeravance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.primeravance.R
import com.example.primeravance.model.Reserva
import java.text.SimpleDateFormat
import java.util.*

class ReservaAdapter(
    private val reservas: MutableList<Reserva>,
    private val onEditarClick: (Reserva) -> Unit,
    private val onCancelarClick: (Reserva) -> Unit
) : RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder>() {

    inner class ReservaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvRestaurante: TextView = itemView.findViewById(R.id.tvReservaRestaurante)
        private val tvEstado: TextView = itemView.findViewById(R.id.tvReservaEstado)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvReservaNombre)
        private val tvFechaHora: TextView = itemView.findViewById(R.id.tvReservaFechaHora)
        private val tvMesaPersonas: TextView = itemView.findViewById(R.id.tvReservaMesaPersonas)
        private val btnEditar: Button = itemView.findViewById(R.id.btnEditarReserva)
        private val btnCancelar: Button = itemView.findViewById(R.id.btnCancelarReserva)

        fun bind(reserva: Reserva) {
            // Mostrar nombre del restaurante o mesa
            tvRestaurante.text = reserva.restauranteNombre ?: "Mesa ${reserva.mesaNumero ?: reserva.mesaId}"
            
            // Estado con colores
            tvEstado.text = reserva.estado
            when (reserva.estado.uppercase()) {
                "CONFIRMADA" -> tvEstado.setBackgroundColor(0xFF4CAF50.toInt())
                "PENDIENTE" -> tvEstado.setBackgroundColor(0xFFFFA726.toInt())
                "CANCELADA" -> tvEstado.setBackgroundColor(0xFFEF5350.toInt())
                else -> tvEstado.setBackgroundColor(0xFF9E9E9E.toInt())
            }
            
            // Nombre del cliente
            tvNombre.text = reserva.nombreCliente
            
            // Formatear fecha
            val fechaFormateada = try {
                val formatoEntrada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formatoSalida = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = formatoEntrada.parse(reserva.fechaReserva)
                formatoSalida.format(date!!)
            } catch (e: Exception) {
                reserva.fechaReserva
            }
            
            // Extraer solo HH:mm de la hora (por si viene con segundos)
            val horaInicio = if (reserva.horaInicio.length >= 5) {
                reserva.horaInicio.substring(0, 5)
            } else {
                reserva.horaInicio
            }
            
            val horaFin = if (reserva.horaFin.length >= 5) {
                reserva.horaFin.substring(0, 5)
            } else {
                reserva.horaFin
            }
            
            tvFechaHora.text = "$fechaFormateada - $horaInicio a $horaFin"
            tvMesaPersonas.text = "Mesa ${reserva.mesaNumero ?: reserva.mesaId} - ${reserva.numeroPersonas} personas"
            
            // Mostrar botones según el estado
            if (reserva.estado.equals("CANCELADA", ignoreCase = true)) {
                btnEditar.visibility = View.GONE
                btnCancelar.visibility = View.GONE
            } else {
                btnEditar.visibility = View.VISIBLE
                btnCancelar.visibility = View.VISIBLE
                
                btnEditar.setOnClickListener {
                    onEditarClick(reserva)
                }
                
                btnCancelar.setOnClickListener {
                    onCancelarClick(reserva)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserva, parent, false)
        return ReservaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        holder.bind(reservas[position])
    }

    override fun getItemCount(): Int = reservas.size

    fun updateReservas(newReservas: List<Reserva>) {
        reservas.clear()
        reservas.addAll(newReservas)
        notifyDataSetChanged()
    }
    
    fun updateReserva(reservaId: Int, newEstado: String) {
        val index = reservas.indexOfFirst { it.id == reservaId }
        if (index != -1) {
            // Actualizar el estado de la reserva
            val reservaActualizada = reservas[index].copy(estado = newEstado)
            reservas[index] = reservaActualizada
            notifyItemChanged(index)
        }
    }
    
    fun removeReserva(reservaId: Int) {
        val index = reservas.indexOfFirst { it.id == reservaId }
        if (index != -1) {
            reservas.removeAt(index)
            notifyItemRemoved(index)
        }
    }
    
    fun clear() {
        reservas.clear()
        notifyDataSetChanged()
    }
}
