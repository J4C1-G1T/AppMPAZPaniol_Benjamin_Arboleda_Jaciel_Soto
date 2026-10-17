package com.proyecto.appmpazpaniol.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Modelo temporal para representar solicitudes en el panel de administración.
 */
data class SolicitudAdminMock(
    val id: String,
    val solicitante: String,
    val cargo: String,
    val equipo: String,
    val sala: String,
    val fecha: String,
    val horaInicio: String,
    val horaDevolucion: String,
    val estado: String // Pendiente, Aceptada, Rechazada, Entregada, Devuelta
)

/**
 * Muestra solicitudes simuladas y el acceso a la gestión de inventario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(onGestionInventario: () -> Unit) {
    val context = LocalContext.current
    // pendiente: reemplazar por datos reales de Room
    val listaSolicitudes = listOf(
        SolicitudAdminMock("1", "Profesor Prueba 1", "Docente", "Proyector Epson", "Sala 101", "20/10/2026", "09:00", "12:00", "Pendiente"),
        SolicitudAdminMock("2", "Profesor Prueba 2", "Docente", "Notebook Lenovo", "Sala 204", "20/10/2026", "10:00", "13:00", "Aceptada"),
        SolicitudAdminMock("3", "Profesor Prueba 3", "Docente", "Cámara Canon", "Sala 305", "21/10/2026", "08:30", "11:30", "Rechazada"),
        SolicitudAdminMock("4", "Profesor Prueba 4", "Docente", "Micrófono inalámbrico", "Sala 112", "21/10/2026", "14:00", "16:00", "Entregada"),
        SolicitudAdminMock("5", "Profesor Prueba 5", "Docente", "Parlante portátil", "Sala 208", "22/10/2026", "09:30", "12:30", "Devuelta")
    )

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
                items(listaSolicitudes) { solicitud ->
                    SolicitudAdminCard(
                        solicitud = solicitud,
                        onAceptar = {
                            Toast.makeText(context, "Solicitud aceptada", Toast.LENGTH_SHORT).show()
                        },
                        onRechazar = {
                            Toast.makeText(context, "Solicitud rechazada", Toast.LENGTH_SHORT).show()
                        },
                        onEntregar = {
                            Toast.makeText(context, "Equipo entregado", Toast.LENGTH_SHORT).show()
                        },
                        onDevolver = {
                            Toast.makeText(context, "Equipo devuelto", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

/**
 * Presenta los datos de una solicitud y las acciones disponibles según su estado.
 */
@Composable
fun SolicitudAdminCard(
    solicitud: SolicitudAdminMock,
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
    AdminScreen(onGestionInventario = {})
}
