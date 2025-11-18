package com.example.primeravance.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.primeravance.R
import com.example.primeravance.adapter.RestauranteAdapter
import com.example.primeravance.databinding.FragmentDashboardBinding
import com.example.primeravance.model.Restaurante
import com.example.primeravance.network.RetrofitClient
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var restauranteAdapter: RestauranteAdapter

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
        setupRecyclerView()
        loadRestaurantes()
    }

    private fun setupRecyclerView() {
        restauranteAdapter = RestauranteAdapter(emptyList()) { restaurante ->
            onRestauranteClick(restaurante)
        }
        
        binding.rvRestaurantes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = restauranteAdapter
        }
    }

    private fun loadRestaurantes() {
        showLoading(true)
        
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.restauranteApiService.getRestaurantes()
                
                showLoading(false)
                
                if (response.isSuccessful && response.body() != null) {
                    val restaurantes = response.body()!!
                    
                    if (restaurantes.isEmpty()) {
                        showEmptyState(true)
                    } else {
                        showEmptyState(false)
                        restauranteAdapter.updateRestaurantes(restaurantes)
                    }
                } else {
                    showEmptyState(true)
                    Toast.makeText(
                        requireContext(),
                        "Error al cargar restaurantes: ${response.code()}",
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

    private fun onRestauranteClick(restaurante: Restaurante) {
        // Navegar al MesasFragment pasando los datos del restaurante
        val bundle = Bundle().apply {
            putInt("restauranteId", restaurante.id)
            putString("restauranteNombre", restaurante.nombre)
            putString("restauranteDireccion", restaurante.direccion)
            putString("restauranteTelefono", restaurante.telefono)
            putString("restauranteDescripcion", restaurante.descripcion)
            putString("restauranteImagenUrl", restaurante.imagenUrl)
            putString("restauranteMiniaturaUrl", restaurante.miniaturaUrl)
        }
        
        findNavController().navigate(R.id.action_dashboard_to_mesas, bundle)
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvRestaurantes.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showEmptyState(show: Boolean) {
        binding.layoutEmpty.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvRestaurantes.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
