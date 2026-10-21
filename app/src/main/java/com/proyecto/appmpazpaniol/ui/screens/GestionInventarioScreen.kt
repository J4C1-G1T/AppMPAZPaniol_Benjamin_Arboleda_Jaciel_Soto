package com.proyecto.appmpazpaniol.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.proyecto.appmpazpaniol.model.Equipo

/**
 * Muestra un formulario de equipos y un listado simulado del inventario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionInventarioScreen(onVolver: () -> Unit) {
    val context = LocalContext.current
    // pendiente: reemplazar por datos reales de Room
    val listaEquipos = listOf(
        Equipo("1", "Computador HP ProDesk", "HP", "ProDesk 400", "Computador", 8, 5, "Disponible", "Estante A1"),
        Equipo("2", "Notebook Lenovo ThinkPad", "Lenovo", "ThinkPad E14", "Notebook", 10, 4, "Reservado", "Estante A2"),
        Equipo("3", "Proyector Epson EB-X06", "Epson", "EB-X06", "Proyector", 5, 3, "Prestado", "Estante B1"),
        Equipo("4", "Kit Arduino UNO", "Arduino", "UNO R3", "Robótica", 6, 6, "En Mantenimiento", "Estante C1"),
        Equipo("5", "Switch TP-Link", "TP-Link", "TL-SG1024", "Equipamiento de red", 4, 0, "Fuera de Servicio", "Estante D1")
    )

    var nombre by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }
    var stockTotal by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var editando by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Inventario") },
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
            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al panel")
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (editando) "Editar equipo" else "Nuevo equipo",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                label = { Text("Nombre") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = marca,
                                onValueChange = { marca = it },
                                label = { Text("Marca") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = modelo,
                                onValueChange = { modelo = it },
                                label = { Text("Modelo") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = categoria,
                                onValueChange = { categoria = it },
                                label = { Text("Categoría") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = estado,
                                onValueChange = { estado = it },
                                label = { Text("Estado") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = stockTotal,
                                onValueChange = { stockTotal = it },
                                label = { Text("Stock total") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = ubicacion,
                                onValueChange = { ubicacion = it },
                                label = { Text("Ubicación") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        Toast.makeText(context, "Equipo guardado", Toast.LENGTH_SHORT).show()
                                        nombre = ""
                                        marca = ""
                                        modelo = ""
                                        categoria = ""
                                        estado = ""
                                        stockTotal = ""
                                        ubicacion = ""
                                        editando = false
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Guardar equipo")
                                }
                                OutlinedButton(
                                    onClick = {
                                        nombre = ""
                                        marca = ""
                                        modelo = ""
                                        categoria = ""
                                        estado = ""
                                        stockTotal = ""
                                        ubicacion = ""
                                        editando = false
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Limpiar")
                                }
                            }
                        }
                    }
                }

                items(listaEquipos) { equipo ->
                    EquipoInventarioCard(
                        equipo = equipo,
                        onEditar = {
                            nombre = equipo.nombre
                            marca = equipo.marca
                            modelo = equipo.modelo
                            categoria = equipo.categoria
                            estado = equipo.estado
                            stockTotal = "${equipo.stockTotal}"
                            ubicacion = equipo.ubicacionFisica
                            editando = true
                        },
                        onDarDeBaja = {
                            Toast.makeText(context, "Equipo dado de baja", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

/**
 * Presenta los datos de un equipo y las acciones visuales de inventario.
 */
@Composable
fun EquipoInventarioCard(
    equipo: Equipo,
    onEditar: () -> Unit,
    onDarDeBaja: () -> Unit
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
            Text(
                text = equipo.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${equipo.marca} ${equipo.modelo}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Categoría: ${equipo.categoria} | Estado: ${equipo.estado}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Stock: ${equipo.stockDisponible} de ${equipo.stockTotal}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Ubicación: ${equipo.ubicacionFisica}",
                style = MaterialTheme.typography.bodySmall
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onEditar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Editar")
                }
                OutlinedButton(
                    onClick = onDarDeBaja,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Dar de baja")
                }
            }
        }
    }
}

/**
 * Vista previa de la pantalla de gestión de inventario.
 */
@Preview(showBackground = true)
@Composable
fun GestionInventarioScreenPreview() {
    GestionInventarioScreen(onVolver = {})
}
