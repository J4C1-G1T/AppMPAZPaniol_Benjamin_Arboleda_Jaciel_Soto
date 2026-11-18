package com.proyecto.appmpazpaniol.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proyecto.appmpazpaniol.data.PaniolRepository
import com.proyecto.appmpazpaniol.model.Equipo
import com.proyecto.appmpazpaniol.viewmodel.SolicitanteViewModel
import com.proyecto.appmpazpaniol.viewmodel.ValidacionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudScreen(
    equipo: Equipo,
    onConfirmarSolicitud: (horasReserva: Int) -> Unit,
    onVolver: () -> Unit,
    correoUsuario: String = "",
    repository: PaniolRepository? = null
) {
    val context = LocalContext.current
    val viewModel: SolicitanteViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SolicitanteViewModel(repository) as T
            }
        }
    )

    val estadoValidacion by viewModel.estadoValidacion.collectAsState()

    var horasReserva by remember { mutableIntStateOf(3) }
    var cargo by remember { mutableStateOf("Docente") }
    var sala by remember { mutableStateOf("Sala 402") }
    var motivoUso by remember { mutableStateOf("") }

    LaunchedEffect(estadoValidacion) {
        when (val estado = estadoValidacion) {
            is ValidacionState.Exito -> {
                Toast.makeText(context, "Solicitud enviada al Pañol", Toast.LENGTH_SHORT).show()
                viewModel.limpiarEstadoValidacion()
                onConfirmarSolicitud(horasReserva)
            }
            is ValidacionState.Error -> {
                Toast.makeText(context, estado.mensaje, Toast.LENGTH_LONG).show()
                viewModel.limpiarEstadoValidacion()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitud de Préstamo") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver al catálogo")
                    }
                },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = equipo.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "Categoría: ${equipo.categoria}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Ubicación: ${equipo.ubicacionFisica}", style = MaterialTheme.typography.bodySmall)
                }
            }

            Text(text = "Anticipación de la Reserva (Mínimo 3 horas)", style = MaterialTheme.typography.labelLarge)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { if (horasReserva > 3) horasReserva-- },
                    enabled = horasReserva > 3
                ) {
                    Text("-")
                }
                Text(text = "$horasReserva Horas", style = MaterialTheme.typography.titleMedium)
                Button(onClick = { horasReserva++ }) {
                    Text("+")
                }
            }

            OutlinedTextField(
                value = cargo,
                onValueChange = { cargo = it },
                label = { Text("Cargo (Docente / Alumno)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = sala,
                onValueChange = { sala = it },
                label = { Text("Sala destino (ej. Sala 402)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = motivoUso,
                onValueChange = { motivoUso = it },
                label = { Text("Motivo del préstamo (ej. Asignatura Robótica)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onVolver,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        viewModel.crearSolicitudPrestamo(
                            equipoId = equipo.id,
                            nombreEquipo = equipo.nombre,
                            solicitanteEmail = correoUsuario,
                            cargo = cargo,
                            sala = sala,
                            horasAnticipacion = horasReserva,
                            motivo = motivoUso
                        )
                    },
                    enabled = motivoUso.isNotBlank() && sala.isNotBlank() && horasReserva >= 3,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Confirmar")
                }
            }
        }
    }
}