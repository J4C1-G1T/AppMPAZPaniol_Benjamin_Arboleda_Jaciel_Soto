package com.proyecto.appmpazpaniol.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proyecto.appmpazpaniol.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginAdmin: (String, String) -> Unit,
    onLoginUsuario: (String, String) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val state = viewModel.uiState
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesión") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            IlustracionPanol(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(width = 180.dp, height = 138.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "MPAZ Pañol",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Gestión y préstamo de equipos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Inicia sesión para continuar",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onEmailChanged(it) },
                label = { Text("Correo Electrónico") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Correo"
                    )
                },
                isError = state.emailError != null,
                supportingText = {
                    if (state.emailError != null) {
                        Text(text = state.emailError)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Contraseña"
                    )
                },
                isError = state.passwordError != null,
                supportingText = {
                    if (state.passwordError != null) {
                        Text(text = state.passwordError)
                    }
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(
                visible = state.errorMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = state.errorMessage ?: "",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = {
                    viewModel.onLoginClick(context, onLoginAdmin, onLoginUsuario)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onNavigateToRegister,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ir a Registro")
            }
        }
    }
}

@Composable
private fun IlustracionPanol(modifier: Modifier = Modifier) {
    val fondo = MaterialTheme.colorScheme.primaryContainer
    val estante = MaterialTheme.colorScheme.primary
    val equipo = MaterialTheme.colorScheme.secondary
    val detalle = MaterialTheme.colorScheme.onPrimaryContainer

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = fondo
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                val ancho = size.width
                val alto = size.height
                val radio = CornerRadius(10.dp.toPx())

                drawRoundRect(
                    color = estante,
                    topLeft = Offset(ancho * 0.18f, alto * 0.12f),
                    size = Size(ancho * 0.64f, alto * 0.76f),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )

                val margen = ancho * 0.07f
                for (nivel in 1..3) {
                    val y = alto * (0.12f + nivel * 0.19f)
                    drawRoundRect(
                        color = fondo,
                        topLeft = Offset(ancho * 0.18f + margen, y),
                        size = Size(ancho * 0.64f - margen * 2, alto * 0.035f),
                        cornerRadius = radio
                    )
                }

                drawRoundRect(
                    color = equipo,
                    topLeft = Offset(ancho * 0.29f, alto * 0.20f),
                    size = Size(ancho * 0.16f, alto * 0.13f),
                    cornerRadius = radio
                )
                drawRoundRect(
                    color = detalle,
                    topLeft = Offset(ancho * 0.51f, alto * 0.21f),
                    size = Size(ancho * 0.18f, alto * 0.12f),
                    cornerRadius = radio
                )
                drawRoundRect(
                    color = detalle,
                    topLeft = Offset(ancho * 0.28f, alto * 0.43f),
                    size = Size(ancho * 0.22f, alto * 0.14f),
                    cornerRadius = radio
                )
                drawRoundRect(
                    color = equipo,
                    topLeft = Offset(ancho * 0.55f, alto * 0.44f),
                    size = Size(ancho * 0.16f, alto * 0.13f),
                    cornerRadius = radio
                )
                drawRoundRect(
                    color = detalle,
                    topLeft = Offset(ancho * 0.34f, alto * 0.67f),
                    size = Size(ancho * 0.32f, alto * 0.13f),
                    cornerRadius = radio
                )
            }
        }
    }
}
