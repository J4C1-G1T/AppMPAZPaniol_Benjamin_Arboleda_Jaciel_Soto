package com.proyecto.appmpazpaniol.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.proyecto.appmpazpaniol.data.PaniolRepository
import com.proyecto.appmpazpaniol.model.Equipo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionInventarioScreen(
    onVolver: () -> Unit,
    repository: PaniolRepository? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var listaEquipos by remember { mutableStateOf<List<Equipo>>(emptyList()) }

    fun recargarEquipos() {
        val repo = repository ?: return
        scope.launch {
            listaEquipos = repo.obtenerEquipos().map { it.toEquipo() }
        }
    }

    LaunchedEffect(Unit) {
        recargarEquipos()
    }

    var equipoEditandoId by remember { mutableStateOf("") }
    var stockDisponibleEditando by remember { mutableIntStateOf(0) }
    var nombre by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }
    var stockTotal by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var editando by remember { mutableStateOf(false) }

    fun limpiarFormulario() {
        equipoEditandoId = ""
        stockDisponibleEditando = 0
        nombre = ""
        marca = ""
        modelo = ""
        categoria = ""
        estado = ""
        stockTotal = ""
        ubicacion = ""
        editando = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Inventario") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver al panel")
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
                                text = if (editando) "Editar equipo (ID: $equipoEditandoId)" else "Nuevo equipo",
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
                            OutlinedTextField(
                                value = marca,
                                onValueChange = { marca = it },
                                label = { Text("Marca") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = modelo,
                                onValueChange = { modelo = it },
                                label = { Text("Modelo") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = categoria,
                                onValueChange = { categoria = it },
                                label = { Text("Categoría") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = estado,
                                onValueChange = { estado = it },
                                label = { Text("Estado (ej. Disponible)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = stockTotal,
                                onValueChange = { stockTotal = it },
                                label = { Text("Stock total") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
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
                                        val totalInt = stockTotal.toIntOrNull() ?: 1
                                        val idFinal = if (editando) {
                                            equipoEditandoId
                                        } else {
                                            ((listaEquipos.mapNotNull { it.id.toIntOrNull() }.maxOrNull() ?: 5) + 1).toString()
                                        }
                                        val disponibleFinal = if (editando) {
                                            stockDisponibleEditando.coerceAtMost(totalInt)
                                        } else {
                                            totalInt
                                        }

                                        val equipoGuardar = Equipo(
                                            id = idFinal,
                                            nombre = nombre.ifBlank { "Equipo sin nombre" },
                                            marca = marca.ifBlank { "Genérica" },
                                            modelo = modelo.ifBlank { "Estándar" },
                                            categoria = categoria.ifBlank { "General" },
                                            stockTotal = totalInt,
                                            stockDisponible = disponibleFinal,
                                            estado = estado.ifBlank { "Disponible" },
                                            ubicacionFisica = ubicacion.ifBlank { "Bodega Pañol" }
                                        )

                                        scope.launch {
                                            try {
                                                repository?.guardarEquipoRemoto(equipoGuardar, esEdicion = editando)
                                                Toast.makeText(context, "Equipo guardado en AWS", Toast.LENGTH_SHORT).show()
                                                limpiarFormulario()
                                                recargarEquipos()
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Guardar equipo")
                                }
                                OutlinedButton(
                                    onClick = { limpiarFormulario() },
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
                            equipoEditandoId = equipo.id
                            stockDisponibleEditando = equipo.stockDisponible
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
                            scope.launch {
                                repository?.darDeBajaEquipo(equipo.id)
                                Toast.makeText(context, "Equipo dado de baja", Toast.LENGTH_SHORT).show()
                                recargarEquipos()
                            }
                        }
                    )
                }
            }
        }
    }
}

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

@Preview(showBackground = true)
@Composable
fun GestionInventarioScreenPreview() {
    GestionInventarioScreen(onVolver = {})
}