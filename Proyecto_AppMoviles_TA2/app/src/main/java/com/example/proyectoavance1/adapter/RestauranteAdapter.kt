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
import com.squareup.picasso.RequestCreator

class RestauranteAdapter(
    private var restaurantes: List<Restaurante>,
    private val onItemClick: (Restaurante) -> Unit
) : RecyclerView.Adapter<RestauranteAdapter.RestauranteViewHolder>() {

    inner class RestauranteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tvRestauranteNombre)
        private val tvDireccion: TextView = itemView.findViewById(R.id.tvRestauranteDireccion)
        private val tvTelefono: TextView = itemView.findViewById(R.id.tvRestauranteTelefono)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvRestauranteDescripcion)
    private val tvRating: TextView = itemView.findViewById(R.id.tvRestauranteRating)
        private val imgRestaurante: ImageView = itemView.findViewById(R.id.imgRestaurante)

        private fun RequestCreator.applySizing(width: Int, height: Int): RequestCreator {
            return if (width > 0 && height > 0) {
                this.resize(width, height).centerCrop()
            } else {
                this.fit().centerCrop()
            }
        }

        private fun loadRestaurantImage(builder: () -> RequestCreator) {
            val width = imgRestaurante.width
            val height = imgRestaurante.height

            val executeLoad: (Int, Int) -> Unit = { w, h ->
                builder().applySizing(w, h).into(imgRestaurante)
            }

            if (width > 0 && height > 0) {
                executeLoad(width, height)
            } else {
                imgRestaurante.post {
                    val measuredWidth = imgRestaurante.width
                    val measuredHeight = imgRestaurante.height
                    executeLoad(measuredWidth, measuredHeight)
                }
            }
        }

        fun bind(restaurante: Restaurante) {
            tvNombre.text = restaurante.nombre
            tvDireccion.text = restaurante.direccion
            tvTelefono.text = restaurante.telefono

            val resolvedPlaceholder = when {
                restaurante.imageRes != 0 -> restaurante.imageRes
                restaurante.nombre.contains("pizz", ignoreCase = true) ||
                        restaurante.nombre.contains("ital", ignoreCase = true) -> R.drawable.img_restaurant_italian
                restaurante.nombre.contains("mex", ignoreCase = true) ||
                        restaurante.nombre.contains("taco", ignoreCase = true) ||
                        restaurante.nombre.contains("burrito", ignoreCase = true) -> R.drawable.img_restaurant_mexican
                restaurante.nombre.contains("sushi", ignoreCase = true) ||
                        restaurante.nombre.contains("asi", ignoreCase = true) ||
                        restaurante.nombre.contains("chin", ignoreCase = true) ||
                        restaurante.nombre.contains("jap", ignoreCase = true) -> R.drawable.img_restaurant_asian
                restaurante.nombre.contains("parrilla", ignoreCase = true) ||
                        restaurante.nombre.contains("carne", ignoreCase = true) ||
                        restaurante.nombre.contains("asado", ignoreCase = true) ||
                        restaurante.nombre.contains("steak", ignoreCase = true) -> R.drawable.img_restaurant_steakhouse
                restaurante.nombre.contains("burger", ignoreCase = true) ||
                        restaurante.nombre.contains("hambur", ignoreCase = true) -> R.drawable.img_restaurant_casual
                else -> R.drawable.ic_restaurant_placeholder
            }

            val urlToLoad = restaurante.imagenUrl ?: restaurante.miniaturaUrl

            if (!urlToLoad.isNullOrBlank()) {
                loadRestaurantImage {
                    Picasso.get()
                        .load(urlToLoad)
                        .placeholder(resolvedPlaceholder)
                        .error(R.drawable.restaurante_pf)
                }
            } else {
                loadRestaurantImage {
                    Picasso.get()
                        .load(resolvedPlaceholder)
                        .placeholder(R.drawable.ic_restaurant_placeholder)
                        .error(R.drawable.restaurante_pf)
                }
            }

            // Descripción opcional
            if (restaurante.descripcion.isNullOrEmpty()) {
                tvDescripcion.visibility = View.GONE
            } else {
                tvDescripcion.visibility = View.VISIBLE
                tvDescripcion.text = restaurante.descripcion
            }

            if (restaurante.ratingPromedio != null && restaurante.totalResenas != null) {
                tvRating.visibility = View.VISIBLE
                val promedio = String.format("%.1f", restaurante.ratingPromedio)
                tvRating.text = "$promedio (${restaurante.totalResenas} reseñas)"
            } else {
                tvRating.visibility = View.GONE
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
