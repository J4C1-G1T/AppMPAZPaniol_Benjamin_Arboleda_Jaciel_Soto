package com.proyecto.appmpazpaniol.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.proyecto.appmpazpaniol.model.Equipo

/**
 * Pantalla que muestra el catálogo de equipos disponibles en el Pañol (MPAZ Pañol).
 * Utiliza una LazyColumn para renderizar la lista de forma eficiente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    onEquipoSeleccionado: (Equipo) -> Unit
) {
    // Lista de equipos simulados (Mock Data) para probar el flujo de la UI
    val listaEquipos = listOf(
        Equipo(
            id = "1",
            nombre = "Proyector Epson EB-X06",
            marca = "Epson",
            modelo = "EB-X06",
            categoria = "Proyector",
            stockTotal = 5,
            stockDisponible = 3,
            estado = "Disponible",
            ubicacionFisica = "Estante A1"
        ),
        Equipo(
            id = "2",
            nombre = "Notebook Lenovo ThinkPad",
            marca = "Lenovo",
            modelo = "ThinkPad E14",
            categoria = "Computación",
            stockTotal = 10,
            stockDisponible = 0,
            estado = "En Mantenimiento",
            ubicacionFisica = "Estante B2"
        ),
        Equipo(
            id = "3",
            nombre = "Cámara Canon EOS Rebel T7",
            marca = "Canon",
            modelo = "EOS Rebel T7",
            categoria = "Audiovisual",
            stockTotal = 4,
            stockDisponible = 2,
            estado = "Disponible",
            ubicacionFisica = "Armario C"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de Equipos - MPAZ Pañol") },
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
                text = "Equipos Disponibles",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // LazyColumn reemplaza el RecyclerView tradicional de Android para listas de alto rendimiento
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(listaEquipos) { equipo ->
                    EquipoItemCard(
                        equipo = equipo,
                        onSolicitarClick = { onEquipoSeleccionado(equipo) }
                    )
                }
            }
        }
    }
}

/**
 * Componente Tarjeta para renderizar cada equipo de forma individual.
 */
@Composable
fun EquipoItemCard(
    equipo: Equipo,
    onSolicitarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = equipo.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Categoría: ${equipo.categoria}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Disponible: ${equipo.stockDisponible} de ${equipo.stockTotal}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (equipo.stockDisponible > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = onSolicitarClick,
                enabled = equipo.stockDisponible > 0
            ) {
                Text("Solicitar")
            }
        }
    }
}