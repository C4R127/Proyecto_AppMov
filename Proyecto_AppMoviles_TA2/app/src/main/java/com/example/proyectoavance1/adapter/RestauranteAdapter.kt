package com.example.primeravance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.primeravance.R
import com.example.primeravance.model.Restaurante
import com.squareup.picasso.Picasso

class RestauranteAdapter(
    private var restaurantes: List<Restaurante>,
    private val onItemClick: (Restaurante) -> Unit
) : RecyclerView.Adapter<RestauranteAdapter.RestauranteViewHolder>() {

    inner class RestauranteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tvRestauranteNombre)
        private val tvDireccion: TextView = itemView.findViewById(R.id.tvRestauranteDireccion)
        private val tvTelefono: TextView = itemView.findViewById(R.id.tvRestauranteTelefono)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvRestauranteDescripcion)
        private val imgRestaurante: ImageView = itemView.findViewById(R.id.imgRestaurante)

        fun bind(restaurante: Restaurante) {
            tvNombre.text = restaurante.nombre
            tvDireccion.text = restaurante.direccion
            tvTelefono.text = restaurante.telefono

            if (restaurante.imageRes != 0) {
                Picasso.get().load(restaurante.imageRes).into(imgRestaurante)
            } else {
                imgRestaurante.setImageResource(R.drawable.restaurante_pf) // placeholder local
            }


            // Descripción opcional
            if (restaurante.descripcion.isNullOrEmpty()) {
                tvDescripcion.visibility = View.GONE
            } else {
                tvDescripcion.visibility = View.VISIBLE
                tvDescripcion.text = restaurante.descripcion
            }

            // Acción al hacer clic en un restaurante
            itemView.setOnClickListener {
                onItemClick(restaurante)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestauranteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurante, parent, false)
        return RestauranteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestauranteViewHolder, position: Int) {
        holder.bind(restaurantes[position])
    }

    override fun getItemCount(): Int = restaurantes.size

    fun updateRestaurantes(newRestaurantes: List<Restaurante>) {
        restaurantes = newRestaurantes
        notifyDataSetChanged()
    }
}
