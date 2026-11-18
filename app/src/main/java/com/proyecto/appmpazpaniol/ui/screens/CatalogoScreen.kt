package com.proyecto.appmpazpaniol.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proyecto.appmpazpaniol.data.PaniolRepository
import com.proyecto.appmpazpaniol.model.Equipo
import com.proyecto.appmpazpaniol.viewmodel.SolicitanteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    onEquipoSeleccionado: (Equipo) -> Unit,
    repository: PaniolRepository? = null
) {
    val viewModel: SolicitanteViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SolicitanteViewModel(repository) as T
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.cargarCatalogoEquipos()
    }

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

            if (viewModel.cargandoEquipos) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(viewModel.equiposCatalogo) { equipo ->
                        EquipoItemCard(
                            equipo = equipo,
                            onSolicitarClick = { onEquipoSeleccionado(equipo) }
                        )
                    }
                }
            }
        }
    }
}

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
                    text = "Categoría: ${equipo.categoria} | Estado: ${equipo.estado}",
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
                enabled = equipo.stockDisponible > 0 && equipo.estado != "Fuera de Servicio"
            ) {
                Text("Solicitar")
            }
        }
    }
}