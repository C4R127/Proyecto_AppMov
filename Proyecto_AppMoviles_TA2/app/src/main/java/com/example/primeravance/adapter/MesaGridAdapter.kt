package com.example.primeravance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.primeravance.R
import com.example.primeravance.model.Mesa
import com.google.android.material.card.MaterialCardView

data class MesaSlot(
    val id: Int,
    val label: String,
    val capacidad: Int,
    val disponible: Boolean,
    val isPlaceholder: Boolean,
    val mesa: Mesa?
)

class MesaGridAdapter(
    private var slots: List<MesaSlot> = emptyList(),
    private val onMesaSelected: (Mesa) -> Unit
) : RecyclerView.Adapter<MesaGridAdapter.MesaGridViewHolder>() {

    private var selectedMesaId: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaGridViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mesa_grid, parent, false)
        return MesaGridViewHolder(view)
    }

    override fun getItemCount(): Int = slots.size

    override fun onBindViewHolder(holder: MesaGridViewHolder, position: Int) {
        holder.bind(slots[position])
    }

    fun setMesas(newSlots: List<MesaSlot>, selectedId: Int?) {
        slots = newSlots
        selectedMesaId = selectedId
        notifyDataSetChanged()
    }

    inner class MesaGridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconMesa: ImageView = itemView.findViewById(R.id.imgMesa)
        private val tvNumero: TextView = itemView.findViewById(R.id.tvMesaNumero)
        private val tvCapacidad: TextView = itemView.findViewById(R.id.tvMesaCapacidad)
        private val tvEstado: TextView = itemView.findViewById(R.id.tvMesaEstado)
        private val card: MaterialCardView = itemView as MaterialCardView

        fun bind(slot: MesaSlot) {
            val context = itemView.context
            val mesa = slot.mesa
            val disponible = slot.disponible && !slot.isPlaceholder
            val selected = mesa != null && selectedMesaId == mesa.id

            val backgroundDefault = ContextCompat.getColor(context, R.color.white)
            val placeholderBackground = ContextCompat.getColor(context, R.color.gray_light)
            val occupiedBackground = ContextCompat.getColor(context, R.color.red_light)
            val selectedBackground = ContextCompat.getColor(context, R.color.green_primary)
            val strokeDefault = ContextCompat.getColor(context, R.color.gray_medium)
            val strokeSelected = ContextCompat.getColor(context, R.color.green_dark)
            val textPrimary = ContextCompat.getColor(context, R.color.text_primary)
            val textSecondary = ContextCompat.getColor(context, R.color.text_secondary)
            val textOnColor = ContextCompat.getColor(context, R.color.white)
            val statusAvailable = ContextCompat.getColor(context, R.color.green_primary)
            val statusUnavailable = ContextCompat.getColor(context, R.color.red_light)
            val strokeWidth = context.resources.getDimensionPixelSize(R.dimen.mesa_card_stroke)

            val backgroundColor = when {
                selected -> selectedBackground
                slot.isPlaceholder -> placeholderBackground
                disponible -> backgroundDefault
                else -> occupiedBackground
            }

            val strokeColor = when {
                selected -> strokeSelected
                slot.isPlaceholder -> placeholderBackground
                disponible -> strokeDefault
                else -> occupiedBackground
            }

            val primaryColor = if (selected) textOnColor else textPrimary
            val secondaryColor = if (selected) textOnColor else textSecondary
            val statusColor = when {
                selected -> textOnColor
                slot.isPlaceholder -> textSecondary
                disponible -> statusAvailable
                else -> textOnColor
            }

            card.setCardBackgroundColor(backgroundColor)
            card.strokeWidth = strokeWidth
            card.strokeColor = strokeColor
            card.alpha = if (slot.isPlaceholder) 0.6f else 1f

            tvNumero.text = slot.label
            tvCapacidad.text = if (slot.capacidad > 0) {
                context.getString(R.string.mesa_capacidad_formato, slot.capacidad)
            } else {
                context.getString(R.string.mesa_capacidad_placeholder)
            }
            tvEstado.text = if (disponible) {
                context.getString(R.string.mesa_estado_disponible)
            } else {
                context.getString(R.string.mesa_estado_ocupada)
            }

            tvNumero.setTextColor(primaryColor)
            tvCapacidad.setTextColor(secondaryColor)
            tvEstado.setTextColor(statusColor)
            iconMesa.setColorFilter(primaryColor)

            itemView.isEnabled = disponible
            itemView.setOnClickListener {
                if (disponible && mesa != null) {
                    selectedMesaId = mesa.id
                    notifyDataSetChanged()
                    onMesaSelected(mesa)
                }
            }
        }
    }
}
