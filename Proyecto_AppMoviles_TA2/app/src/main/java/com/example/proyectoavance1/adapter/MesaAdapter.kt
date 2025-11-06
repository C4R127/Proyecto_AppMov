package com.example.primeravance.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.primeravance.R
import com.example.primeravance.model.Mesa

class MesaAdapter(
    private var mesas: List<Mesa>,
    private val onItemClick: (Mesa) -> Unit
) : RecyclerView.Adapter<MesaAdapter.MesaViewHolder>() {

    private var selectedPosition = -1

    inner class MesaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardMesa: CardView = itemView.findViewById(R.id.cardMesa)
        private val tvNumero: TextView = itemView.findViewById(R.id.tvMesaNumero)
        private val tvCapacidad: TextView = itemView.findViewById(R.id.tvMesaCapacidad)
        private val tvEstado: TextView = itemView.findViewById(R.id.tvMesaEstado)
        private val viewEstado: View = itemView.findViewById(R.id.viewEstado)

        fun bind(mesa: Mesa, position: Int) {
            val numeroMesaDisplay = mesa.numeroMesa ?: "Mesa ${mesa.numero}"
            tvNumero.text = numeroMesaDisplay
            tvCapacidad.text = "Capacidad: ${mesa.capacidad} personas"
            
            // Determinar si la mesa está disponible
            val disponible = mesa.disponible && !mesa.ocupada
            
            if (disponible) {
                tvEstado.text = "Disponible"
                tvEstado.setTextColor(ContextCompat.getColor(itemView.context, R.color.green_primary))
                viewEstado.setBackgroundResource(R.drawable.circle_shape)
                itemView.alpha = 1.0f
            } else {
                tvEstado.text = if (mesa.ocupada) "Ocupada en este horario" else "No disponible"
                tvEstado.setTextColor(Color.parseColor("#FF6B6B"))
                viewEstado.setBackgroundColor(Color.parseColor("#FF6B6B"))
                itemView.alpha = 0.5f
            }

            // Highlight si está seleccionada
            if (position == selectedPosition && disponible) {
                cardMesa.setCardBackgroundColor(
                    ContextCompat.getColor(itemView.context, R.color.green_light)
                )
                cardMesa.cardElevation = 8f
            } else {
                cardMesa.setCardBackgroundColor(Color.WHITE)
                cardMesa.cardElevation = 3f
            }

            // Click solo si está disponible
            if (disponible) {
                itemView.setOnClickListener {
                    if (selectedPosition != position) {
                        val previousPosition = selectedPosition
                        selectedPosition = position
                        
                        // Notificar cambios de manera más eficiente
                        if (previousPosition >= 0) {
                            notifyItemChanged(previousPosition)
                        }
                        notifyItemChanged(selectedPosition)
                        
                        onItemClick(mesa)
                    }
                }
            } else {
                itemView.setOnClickListener(null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mesa, parent, false)
        return MesaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MesaViewHolder, position: Int) {
        holder.bind(mesas[position], position)
    }

    override fun getItemCount(): Int = mesas.size

    fun updateMesas(newMesas: List<Mesa>) {
        val oldSelectedPosition = selectedPosition
        mesas = newMesas
        selectedPosition = -1
        notifyDataSetChanged()
    }

    fun getSelectedMesa(): Mesa? {
        return if (selectedPosition >= 0 && selectedPosition < mesas.size) {
            mesas[selectedPosition]
        } else null
    }
}
