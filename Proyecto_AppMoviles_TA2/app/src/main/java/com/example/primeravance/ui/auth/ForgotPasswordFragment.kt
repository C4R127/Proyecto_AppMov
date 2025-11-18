package com.example.primeravance.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.primeravance.databinding.FragmentForgotPasswordBinding
import com.example.primeravance.model.ForgotPasswordRequest
import com.example.primeravance.network.RetrofitClient
import kotlinx.coroutines.launch

class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnEnviar.setOnClickListener { sendRecovery() }
    }

    private fun sendRecovery() {
        val email = binding.etEmailRecovery.text.toString().trim()
        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa tu correo", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnEnviar.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.authApiService.forgotPassword(
                    ForgotPasswordRequest(email)
                )
                binding.btnEnviar.isEnabled = true

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), response.body()?.message ?: "Revisa tu bandeja", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Error ${'$'}{response.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                binding.btnEnviar.isEnabled = true
                Toast.makeText(requireContext(), "Error: ${'$'}{e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
