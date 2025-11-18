package com.example.primeravance.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.primeravance.R
import com.example.primeravance.data.SessionManager
import com.example.primeravance.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var sessionManager: SessionManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager?.getUser()?.let {
            binding.tvBienvenida.text = getString(R.string.home_saludo, it.nombre)
        }
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnReserva.setOnClickListener {
            // Navigate to Dashboard (Store) tab
            Toast.makeText(requireContext(), "Welcome! Navigate to Store tab to shop", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.navigation_dashboard)
        }

        binding.btnCerrarSesion.setOnClickListener {
            sessionManager?.clearSession()
            findNavController().navigate(R.id.loginFragment)
        }

    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        sessionManager = null
    }
}
