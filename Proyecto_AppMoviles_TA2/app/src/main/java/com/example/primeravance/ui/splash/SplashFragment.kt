package com.example.primeravance.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.primeravance.R
import com.example.primeravance.data.SessionManager
import com.example.primeravance.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        decideNextDestination()
    }

    private fun decideNextDestination() {
        val sessionManager = SessionManager(requireContext())

        val destination = when {
            sessionManager.isLoggedIn() && sessionManager.getUser() != null -> {
                R.id.navigation_home
            }
            sessionManager.isRegistered() -> {
                R.id.loginFragment
            }
            else -> {
                R.id.registerFragment
            }
        }

        // Pequeño delay para mostrar el splash (opcional)
        binding.root.postDelayed({
            findNavController().navigate(destination)
        }, 600)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
