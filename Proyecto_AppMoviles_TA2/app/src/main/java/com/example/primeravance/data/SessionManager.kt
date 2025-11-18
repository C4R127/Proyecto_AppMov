package com.example.primeravance.data

import android.content.Context
import com.example.primeravance.model.Usuario

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUser(user: Usuario) {
        prefs.edit()
            .putInt(KEY_USER_ID, user.id)
            .putString(KEY_USER_NAME, user.nombre)
            .putString(KEY_USER_EMAIL, user.email)
            .putString(KEY_USER_PHONE, user.telefono)
            .putString(KEY_USER_ROLE, user.rol)
            .apply()
    }

    fun getUser(): Usuario? {
        val id = prefs.getInt(KEY_USER_ID, DEFAULT_INT)
        val name = prefs.getString(KEY_USER_NAME, null)
        val email = prefs.getString(KEY_USER_EMAIL, null)
        val phone = prefs.getString(KEY_USER_PHONE, null)
        val role = prefs.getString(KEY_USER_ROLE, null)

        return if (id != DEFAULT_INT && !name.isNullOrEmpty() && !email.isNullOrEmpty() && !role.isNullOrEmpty()) {
            Usuario(id = id, nombre = name, email = email, rol = role, telefono = phone)
        } else {
            null
        }
    }

    fun updateUserProfile(nombre: String, email: String, telefono: String?): Usuario? {
        val currentRole = prefs.getString(KEY_USER_ROLE, null)
        val currentId = prefs.getInt(KEY_USER_ID, DEFAULT_INT)

        prefs.edit()
            .putString(KEY_USER_NAME, nombre)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_PHONE, telefono)
            .apply()

        return if (currentId != DEFAULT_INT && !currentRole.isNullOrEmpty()) {
            Usuario(id = currentId, nombre = nombre, email = email, rol = currentRole, telefono = telefono)
        } else {
            null
        }
    }

    fun setRegistered(value: Boolean) {
        prefs.edit().putBoolean(KEY_IS_REGISTERED, value).apply()
    }

    fun isRegistered(): Boolean = prefs.getBoolean(KEY_IS_REGISTERED, false)

    fun setLoggedIn(value: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun clearSession(preserveRegistration: Boolean = true) {
        val wasRegistered = isRegistered()
        prefs.edit().clear().apply()
        if (preserveRegistration && wasRegistered) {
            setRegistered(true)
        }
    }

    companion object {
        private const val PREFS_NAME = "session_prefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_IS_REGISTERED = "is_registered"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val DEFAULT_INT = -1
    }
}
