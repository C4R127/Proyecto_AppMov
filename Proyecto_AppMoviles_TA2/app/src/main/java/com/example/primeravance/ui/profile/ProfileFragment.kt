package com.example.primeravance.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.primeravance.R
import com.example.primeravance.data.SessionManager
import com.example.primeravance.databinding.DialogEditProfileBinding
import com.example.primeravance.databinding.FragmentProfileBinding
import com.example.primeravance.model.Usuario
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private var currentUser: Usuario? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        refreshUserInfo()

        binding.btnVerReservas.setOnClickListener {
            findNavController().navigate(R.id.navigation_notifications)
        }

        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.clearSession()
            Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun refreshUserInfo() {
        currentUser = sessionManager.getUser()
        val user = currentUser
        if (user != null) {
            binding.tvNombre.text = user.nombre
            binding.tvEmail.text = user.email
            val telefono = if (!user.telefono.isNullOrBlank()) user.telefono else "-"
            binding.tvTelefono.text = getString(R.string.perfil_telefono_label) + ": ${telefono}"
            binding.btnEditProfile.isEnabled = true
        } else {
            binding.tvNombre.text = ""
            binding.tvEmail.text = ""
            binding.tvTelefono.text = getString(R.string.perfil_telefono_label) + ": -"
            binding.btnEditProfile.isEnabled = false
        }
    }

    private fun showEditProfileDialog() {
        val user = currentUser ?: return
        val dialogBinding = DialogEditProfileBinding.inflate(LayoutInflater.from(requireContext()))
        dialogBinding.etNombre.setText(user.nombre)
        dialogBinding.etEmail.setText(user.email)
        dialogBinding.etTelefono.setText(user.telefono ?: "")

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.perfil_edit_info))
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnGuardarCambios.setOnClickListener {
            val nombre = dialogBinding.etNombre.text?.toString()?.trim().orEmpty()
            val email = dialogBinding.etEmail.text?.toString()?.trim().orEmpty()
            val telefono = dialogBinding.etTelefono.text?.toString()?.trim().orEmpty()

            var hasError = false

            if (nombre.isEmpty()) {
                dialogBinding.tilNombre.error = getString(R.string.perfil_error_nombre)
                hasError = true
            } else {
                dialogBinding.tilNombre.error = null
            }

            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                dialogBinding.tilEmail.error = getString(R.string.perfil_error_email)
                hasError = true
            } else {
                dialogBinding.tilEmail.error = null
            }

            if (telefono.isNotEmpty() && telefono.length < 6) {
                dialogBinding.tilTelefono.error = getString(R.string.perfil_error_telefono)
                hasError = true
            } else {
                dialogBinding.tilTelefono.error = null
            }

            if (hasError) return@setOnClickListener

            sessionManager.updateUserProfile(
                nombre = nombre,
                email = email,
                telefono = telefono.ifEmpty { null }
            )?.also {
                currentUser = it
            }
            refreshUserInfo()
            Toast.makeText(requireContext(), getString(R.string.perfil_actualizado), Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
