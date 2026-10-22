package com.proyecto.appmpazpaniol.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proyecto.appmpazpaniol.model.SolicitudAdmin
import com.proyecto.appmpazpaniol.viewmodel.AdminViewModel

/**
 * Muestra solicitudes simuladas y el acceso a la gestión de inventario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onGestionInventario: () -> Unit,
    onVolver:()-> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val context = LocalContext.current
    val state = viewModel.uiState
    var idAceptar by remember { mutableStateOf("") }
    var idRechazar by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración - MPAZ Pañol") },
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
        ) {
            Text(
                text = "Solicitudes de Préstamo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Button(
                onClick = onGestionInventario,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gestión de Inventario")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(state.solicitudes) { solicitud ->
                    SolicitudAdminCard(
                        solicitud = solicitud,
                        onAceptar = { idAceptar = solicitud.id },
                        onRechazar = { idRechazar = solicitud.id },
                        onEntregar = {
                            Toast.makeText(context, "Equipo entregado", Toast.LENGTH_SHORT).show()
                        },
                        onDevolver = {
                            Toast.makeText(context, "Equipo devuelto", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            if (idAceptar != "") {
                AlertDialog(
                    onDismissRequest = { idAceptar = "" },
                    title = { Text("Aceptar solicitud") },
                    text = { Text("¿Confirmas que quieres aceptar la solicitud $idAceptar?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.onAceptarClick(idAceptar)
                                Toast.makeText(context, "Solicitud aceptada", Toast.LENGTH_SHORT).show()
                                idAceptar = ""
                            }
                        ) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { idAceptar = "" }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            if (idRechazar != "") {
                AlertDialog(
                    onDismissRequest = {
                        viewModel.onCancelarRechazo()
                        idRechazar = ""
                    },
                    title = { Text("Rechazar solicitud") },
                    text = {
                        Column {
                            Text("Escribe la justificación del rechazo (obligatoria).")
                            OutlinedTextField(
                                value = state.motivoRechazo,
                                onValueChange = { viewModel.onMotivoRechazoChanged(it) },
                                label = { Text("Justificación del rechazo") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (state.motivoRechazoError != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = state.motivoRechazoError,
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.onRechazarClick(idRechazar) {
                                    Toast.makeText(context, "Solicitud rechazada", Toast.LENGTH_SHORT).show()
                                    idRechazar = ""
                                }
                            }
                        ) {
                            Text("Confirmar rechazo")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = {
                                viewModel.onCancelarRechazo()
                                idRechazar = ""
                            }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

/**
 * Presenta los datos de una solicitud y las acciones disponibles según su estado.
 */
@Composable
fun SolicitudAdminCard(
    solicitud: SolicitudAdmin,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit,
    onEntregar: () -> Unit,
    onDevolver: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = solicitud.equipo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                EtiquetaEstadoAdmin(estado = solicitud.estado)
            }

            Text(
                text = "Solicitante: ${solicitud.solicitante} (${solicitud.cargo})",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Sala: ${solicitud.sala}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Fecha: ${solicitud.fecha} | ${solicitud.horaInicio} a ${solicitud.horaDevolucion}",
                style = MaterialTheme.typography.bodySmall
            )
            if (solicitud.estado == "Rechazada") {
                Text(
                    text = "Motivo: ${solicitud.motivoRechazo}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            when (solicitud.estado) {
                "Pendiente" -> Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAceptar,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Aceptar")
                    }
                    OutlinedButton(
                        onClick = onRechazar,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Rechazar")
                    }
                }
                "Aceptada" -> Button(
                    onClick = onEntregar,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Marcar entregada")
                }
                "Entregada" -> Button(
                    onClick = onDevolver,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Marcar devuelta")
                }
                else -> {}
            }
        }
    }
}

/**
 * Muestra una etiqueta con colores asociados al estado de la solicitud.
 */
@Composable
fun EtiquetaEstadoAdmin(estado: String) {
    val (colorFondo, colorTexto) = when (estado) {
        "Pendiente" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        "Aceptada" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "Entregada" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        "Devuelta" -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        "Rechazada" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }

    Surface(
        color = colorFondo,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = estado,
            color = colorTexto,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Vista previa del panel de administración.
 */
@Preview(showBackground = true)
@Composable
fun AdminScreenPreview() {
    AdminScreen(
        onGestionInventario = {},
        onVolver = {},
        viewModel = AdminViewModel()
    )
}
