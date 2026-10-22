package com.proyecto.appmpazpaniol.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class SolicitudHistorialMock(
    val id: String,
    val nombreEquipo: String,
    val fechaReserva: String,
    val horasSolicitadas: Int,
    val estado: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisSolicitudesScreen(
    onVolver: () -> Unit
) {
    val listaHistorial = listOf(
        SolicitudHistorialMock("1", "Proyector Epson EB-X06", "13/10/2026", 3, "Pendiente"),
        SolicitudHistorialMock("2", "Cámara Canon EOS Rebel T7", "10/10/2026", 4, "Aprobado"),
        SolicitudHistorialMock("3", "Notebook Lenovo ThinkPad", "05/10/2026", 2, "Rechazado")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Solicitudes - Historial") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
                .padding(16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(listaHistorial) { solicitud ->
                    ItemSolicitudCard(solicitud = solicitud)
                }
            }
        }
    }
}

@Composable
fun ItemSolicitudCard(solicitud: SolicitudHistorialMock) {
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
                    text = solicitud.nombreEquipo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                EtiquetaEstado(estado = solicitud.estado)
            }

            Text(
                text = "Fecha de reserva: ${solicitud.fechaReserva}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Duración: ${solicitud.horasSolicitadas} horas",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun EtiquetaEstado(estado: String) {
    val (colorFondo, colorTexto) = when (estado) {
        "Aprobado" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "Pendiente" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
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