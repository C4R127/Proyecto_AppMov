package com.example.primeravance.ui.auth

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
import com.example.primeravance.databinding.FragmentRegisterBinding
import com.example.primeravance.model.RegisterRequest
import com.example.primeravance.model.RegisterResponse
import com.example.primeravance.model.Usuario
import com.example.primeravance.network.RetrofitClient
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnRegistrar.setOnClickListener { attemptRegister() }
        binding.tvIrLogin.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun attemptRegister() {
        val nombre = binding.etNombre.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (nombre.isEmpty() || email.isEmpty() || username.isEmpty() || telefono.isEmpty() || password.length < 6) {
            Toast.makeText(requireContext(), "Completa todos los campos (contraseña mínimo 6 caracteres)", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnRegistrar.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.authApiService.register(
                    RegisterRequest(
                        nombre = nombre,
                        email = email,
                        username = username,
                        telefono = telefono,
                        password = password
                    )
                )

                binding.btnRegistrar.isEnabled = true

                val body = response.body()
                val apiSuccess = response.isSuccessful && body?.success != false

                if (apiSuccess) {
                    val sessionManager = SessionManager(requireContext())
                    sessionManager.setRegistered(true)
                    val usuarioRegistrado = body?.usuario
                        ?: body?.toUsuarioFallback(nombre, email, telefono)
                    usuarioRegistrado?.let { usuario ->
                        sessionManager.saveUser(usuario)
                        sessionManager.setLoggedIn(true)
                    }
                    Toast.makeText(requireContext(), "Cuenta creada con éxito", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.navigation_dashboard)
                } else {
                    val errorMessage = body?.message ?: "Error al registrar"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                binding.btnRegistrar.isEnabled = true
                Toast.makeText(requireContext(), "Error: ${'$'}{e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun RegisterResponse.toUsuarioFallback(
        defaultNombre: String,
        defaultEmail: String,
        defaultTelefono: String
    ): Usuario? {
        val resolvedId = id
        val resolvedRol = rol
        if (resolvedId == null || resolvedRol.isNullOrEmpty()) {
            return null
        }
        return Usuario(
            id = resolvedId,
            nombre = nombre ?: defaultNombre,
            email = email ?: defaultEmail,
            rol = resolvedRol,
            telefono = telefono ?: defaultTelefono
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
