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
import com.proyecto.appmpazpaniol.model.LoginModel
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    var uiState by mutableStateOf(LoginModel())
        private set

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

    private fun validateEmail(email: String): String? {
        if (email.isBlank()) {
            return "El correo es obligatorio."
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            return "Ingresa un correo válido."
        } else {
            return null
        }
    }

    private fun validatePassword(password: String): String? {
        if (password.isBlank()) {
            return "La contraseña es obligatoria."
        } else {
            return null
        }
    }

    fun onLoginClick(
        context: Context,
        onLoginAdmin: (String, String) -> Unit,
        onLoginUsuario: (String, String) -> Unit
    ) {
        val emailError = validateEmail(uiState.email)
        val passwordError = validatePassword(uiState.password)

        if (emailError != null || passwordError != null) {
            uiState = uiState.copy(
                emailError = emailError,
                passwordError = passwordError
            )
            return
        }

        val email = uiState.email.trim()
        viewModelScope.launch {
            val db = AppDatabase.obtenerBaseDatos(context)
            if (db.usuarioDao().getUserByEmail(AdminCuenta.CORREO) == null) {
                db.usuarioDao().registerUser(
                    UsuarioEntity(
                        name = AdminCuenta.NOMBRE,
                        email = AdminCuenta.CORREO,
                        password = AdminCuenta.CLAVE
                    )
                )
            }

            val user = db.usuarioDao().getUserByEmail(email)
            if (user != null && user.password == uiState.password) {
                uiState = uiState.copy(errorMessage = null)
                if (user.email == AdminCuenta.CORREO) {
                    onLoginAdmin(user.name, user.email)
                } else {
                    onLoginUsuario(user.name, user.email)
                }
            } else {
                uiState = uiState.copy(errorMessage = "Correo o contraseña incorrectos.")
            }
        }
    }
}
