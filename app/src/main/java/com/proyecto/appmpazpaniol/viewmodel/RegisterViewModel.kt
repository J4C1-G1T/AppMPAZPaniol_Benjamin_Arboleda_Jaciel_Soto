package com.proyecto.appmpazpaniol.viewmodel

import android.content.Context
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.appmpazpaniol.data.AppDatabase
import com.proyecto.appmpazpaniol.data.local.entity.UsuarioEntity
import com.proyecto.appmpazpaniol.model.AdminCuenta
import com.proyecto.appmpazpaniol.model.RegisterModel
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    var uiState by mutableStateOf(RegisterModel())
        private set

    fun onNameChanged(name: String) {
        uiState = uiState.copy(
            name = name,
            nameError = null,
            errorMessage = null
        )
    }

    fun onEmailChanged(email: String) {
        uiState = uiState.copy(
            email = email,
            emailError = null,
            errorMessage = null
        )
    }

    fun onPasswordChanged(password: String) {
        uiState = uiState.copy(
            password = password,
            passwordError = null,
            errorMessage = null
        )
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        uiState = uiState.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = null,
            errorMessage = null
        )
    }

    private fun validateName(name: String): String? {
        if (name.isBlank()) {
            return "El nombre es obligatorio."
        } else {
            return null
        }
    }

    private fun validateEmail(email: String): String? {
        if (email.isBlank()) {
            return "El correo es obligatorio."
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            return "Ingresa un correo válido."
        } else if (email.trim() == AdminCuenta.CORREO) {
            return "Este correo está reservado."
        } else {
            return null
        }
    }

    private fun validatePassword(password: String): String? {
        if (password.isBlank()) {
            return "La contraseña es obligatoria."
        } else if (password.length < 6) {
            return "Debe tener al menos 6 caracteres."
        } else {
            return null
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        if (password != confirmPassword) {
            return "Las contraseñas no coinciden."
        } else {
            return null
        }
    }

    fun onRegisterClick(context: Context, onSuccess: () -> Unit) {
        val nameError = validateName(uiState.name)
        val emailError = validateEmail(uiState.email)
        val passwordError = validatePassword(uiState.password)
        val confirmPasswordError = validateConfirmPassword(
            uiState.password,
            uiState.confirmPassword
        )

        if (
            nameError != null ||
            emailError != null ||
            passwordError != null ||
            confirmPasswordError != null
        ) {
            uiState = uiState.copy(
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )
            return
        }

        val email = uiState.email.trim()
        viewModelScope.launch {
            val db = AppDatabase.obtenerBaseDatos(context)
            if (db.usuarioDao().getUserByEmail(email) != null) {
                uiState = uiState.copy(errorMessage = "El correo ya está registrado.")
            } else {
                db.usuarioDao().registerUser(
                    UsuarioEntity(
                        name = uiState.name,
                        email = email,
                        password = uiState.password
                    )
                )
                uiState = uiState.copy(errorMessage = null)
                onSuccess()
            }
        }
    }
}
