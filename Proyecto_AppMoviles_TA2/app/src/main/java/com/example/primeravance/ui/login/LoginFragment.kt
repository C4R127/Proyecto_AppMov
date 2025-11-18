package com.example.primeravance.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.primeravance.R
import com.example.primeravance.data.SessionManager
import com.example.primeravance.databinding.FragmentLoginBinding
import com.example.primeravance.model.LoginRequest
import com.example.primeravance.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var sessionManager: SessionManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (sessionManager?.isLoggedIn() == true && sessionManager?.getUser() != null) {
            findNavController().navigate(R.id.navigation_home)
            return
        }
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnPeruvianFlavor.setOnClickListener {
            findNavController().navigate(R.id.navigation_welcome)
        }

        binding.btnIngresar.setOnClickListener {
            performLogin()
        }

        binding.tvOlvidasteContrasenia.setOnClickListener {
            findNavController().navigate(R.id.forgotPasswordFragment)
        }

        binding.tvRegistrar.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }
    }

    private fun performLogin() {
        val username = binding.etUsuario.text.toString().trim()
        val password = binding.etContrasenia.text.toString().trim()

        // Validar entrada
        if (!validateInput(username, password)) {
            return
        }

        // Mostrar loading (deshabilitar botón)
        binding.btnIngresar.isEnabled = false

        // Llamar a la API
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.authApiService.login(
                    LoginRequest(username = username, password = password)
                )

                binding.btnIngresar.isEnabled = true

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    
                    if (loginResponse.success) {
                        sessionManager?.setRegistered(true)
                        sessionManager?.setLoggedIn(true)
                        loginResponse.usuario?.let { sessionManager?.saveUser(it) }
                        Toast.makeText(
                            requireContext(),
                            "Login exitoso! Bienvenido ${loginResponse.usuario?.nombre ?: ""}",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.action_login_to_home)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            loginResponse.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${response.code()} - ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                binding.btnIngresar.isEnabled = true
                Toast.makeText(
                    requireContext(),
                    "Error de conexión: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun validateInput(username: String, password: String): Boolean {
        if (username.isEmpty()) {
            binding.etUsuario.error = "Usuario es requerido"
            return false
        }

        if (password.isEmpty()) {
            binding.etContrasenia.error = "Contraseña es requerida"
            return false
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        sessionManager = null
    }
}
