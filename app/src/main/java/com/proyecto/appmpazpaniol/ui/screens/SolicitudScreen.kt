package com.proyecto.appmpazpaniol.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.proyecto.appmpazpaniol.model.Equipo

/**
 * Pantalla para la creación y confirmación de solicitudes de préstamo.
 * Permite al docente/solicitante seleccionar las horas de uso y validar la regla de 3 horas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudScreen(
    equipo: Equipo,
    onConfirmarSolicitud: (horasReserva: Int) -> Unit,
    onVolver: () -> Unit
) {
    var horasReserva by remember { mutableIntStateOf(3) } // Regla de negocio: mínimo 3 horas
    var motivoUso by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitud de Préstamo") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Detalle del Equipo Seleccionado
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = equipo.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "Categoría: ${equipo.categoria}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Ubicación: ${equipo.ubicacionFisica}", style = MaterialTheme.typography.bodySmall)
                }
            }

            // Selector de Horas de Anticipación / Uso
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

            // Campo de texto para el motivo de uso
            OutlinedTextField(
                value = motivoUso,
                onValueChange = { motivoUso = it },
                label = { Text("Motivo del préstamo (ej. Asignatura Robótica)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botones de acción
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
                    onClick = { onConfirmarSolicitud(horasReserva) },
                    enabled = motivoUso.isNotBlank() && horasReserva >= 3,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Confirmar")
                }
            }
        }
    }
}