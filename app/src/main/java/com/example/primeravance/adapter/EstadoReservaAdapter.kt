package com.example.primeravance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.primeravance.R
import com.example.primeravance.model.EstadoReserva

class EstadoReservaAdapter(private val lista: List<EstadoReserva>) :
    RecyclerView.Adapter<EstadoReservaAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreApellido: TextView = itemView.findViewById(R.id.txtNombreApellido)
        val turno: TextView = itemView.findViewById(R.id.txtTurno)
        val mesa: TextView = itemView.findViewById(R.id.txtMesa)
        val fecha: TextView = itemView.findViewById(R.id.txtFecha)
        val hora: TextView = itemView.findViewById(R.id.txtHora)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_estado_reserva, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reserva = lista[position]
        holder.nombreApellido.text = "Cliente: ${reserva.nombreApellido}"
        holder.turno.text = "Turno: ${reserva.turno}"
        holder.mesa.text = "Mesa: ${reserva.mesa}"
        holder.fecha.text = "Fecha: ${reserva.fecha}"
        holder.hora.text = "Hora: ${reserva.hora}"
    }

    override fun getItemCount(): Int = lista.size
}
