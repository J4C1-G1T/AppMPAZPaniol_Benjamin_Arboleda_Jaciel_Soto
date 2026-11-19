package com.proyecto.appmpazpaniol.ui.screens

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
    var guardando by remember { mutableStateOf(false) }

    fun recargarEquipos() {
        val repo = repository ?: return
        scope.launch {
            listaEquipos = repo.obtenerEquipos()
                .map { it.toEquipo() }
                .filter { !it.estado.equals("Fuera de Servicio", ignoreCase = true) }
        }
    }

    LaunchedEffect(Unit) {
        recargarEquipos()
    }

    val estadosDisponibles = listOf("Disponible", "En Mantenimiento", "Reservado")

    var equipoEditandoId by remember { mutableStateOf("") }
    var stockDisponibleEditando by remember { mutableIntStateOf(0) }
    var nombre by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("Disponible") }
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
        estado = "Disponible"
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
                    LinternaControl()
                }

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

                            Text(
                                text = "Estado",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                estadosDisponibles.forEach { opcion ->
                                    FilterChip(
                                        selected = estado.equals(opcion, ignoreCase = true),
                                        onClick = { estado = opcion },
                                        label = { Text(opcion) }
                                    )
                                }
                            }

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
                                    enabled = !guardando,
                                    onClick = {
                                        val repo = repository ?: return@Button
                                        val totalInt = (stockTotal.toIntOrNull() ?: 1).coerceAtLeast(1)

                                        scope.launch {
                                            guardando = true
                                            try {
                                                val idFinal = if (editando) {
                                                    equipoEditandoId
                                                } else {
                                                    val todosEnAws = repo.obtenerEquipos()
                                                    val idsExistentes = todosEnAws.map { it.id.trim() }.toSet()
                                                    var siguienteNum = (todosEnAws
                                                        .mapNotNull { it.id.filter(Char::isDigit).toIntOrNull() }
                                                        .maxOrNull() ?: 5) + 1
                                                    while (idsExistentes.contains(siguienteNum.toString())) {
                                                        siguienteNum++
                                                    }
                                                    siguienteNum.toString()
                                                }

                                                val disponibleFinal = if (editando) {
                                                    stockDisponibleEditando.coerceAtMost(totalInt)
                                                } else {
                                                    totalInt
                                                }

                                                val equipoGuardar = Equipo(
                                                    id = idFinal,
                                                    nombre = nombre.trim().ifBlank { "Equipo sin nombre" },
                                                    marca = marca.trim().ifBlank { "Genérica" },
                                                    modelo = modelo.trim().ifBlank { "Estándar" },
                                                    categoria = categoria.trim().ifBlank { "General" },
                                                    stockTotal = totalInt,
                                                    stockDisponible = disponibleFinal,
                                                    estado = estado,
                                                    ubicacionFisica = ubicacion.trim().ifBlank { "Bodega Pañol" }
                                                )

                                                repo.guardarEquipoRemoto(equipoGuardar, esEdicion = editando)
                                                Toast.makeText(context, "Equipo guardado en AWS", Toast.LENGTH_SHORT).show()
                                                limpiarFormulario()
                                            } catch (e: Exception) {
                                                Log.e("GestionInventario", "Error al guardar equipo", e)
                                                Toast.makeText(context, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                                            } finally {
                                                recargarEquipos()
                                                guardando = false
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(if (guardando) "Guardando..." else "Guardar equipo")
                                }
                                OutlinedButton(
                                    enabled = !guardando,
                                    onClick = { limpiarFormulario() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Limpiar")
                                }
                            }
                        }
                    }
                }

                items(listaEquipos, key = { it.id }) { equipo ->
                    EquipoInventarioCard(
                        equipo = equipo,
                        onEditar = {
                            equipoEditandoId = equipo.id
                            stockDisponibleEditando = equipo.stockDisponible
                            nombre = equipo.nombre
                            marca = equipo.marca
                            modelo = equipo.modelo
                            categoria = equipo.categoria
                            estado = if (estadosDisponibles.any { it.equals(equipo.estado, ignoreCase = true) }) {
                                equipo.estado
                            } else {
                                "Disponible"
                            }
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

private data class FlashCameraSelection(
    val cameraId: String?,
    val message: String?
)

private fun findFlashCamera(cameraManager: CameraManager): FlashCameraSelection {
    return try {
        val selected = cameraManager.cameraIdList
            .mapNotNull { cameraId ->
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                if (characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true) {
                    cameraId to (characteristics.get(CameraCharacteristics.LENS_FACING) ==
                            CameraCharacteristics.LENS_FACING_BACK)
                } else {
                    null
                }
            }
            .maxByOrNull { it.second }
        FlashCameraSelection(
            cameraId = selected?.first,
            message = if (selected == null) "Este dispositivo no tiene cámara con flash." else null
        )
    } catch (e: CameraAccessException) {
        Log.e("GestionInventario", "No se pudo consultar la cámara con flash", e)
        FlashCameraSelection(null, "No se pudo acceder a la cámara.")
    } catch (e: IllegalArgumentException) {
        Log.e("GestionInventario", "No se pudo consultar la cámara con flash", e)
        FlashCameraSelection(null, "No se pudo acceder a la cámara.")
    }
}

@Composable
private fun LinternaControl() {
    val context = LocalContext.current
    val cameraManager = remember(context) {
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }
    val selection = remember(cameraManager) { findFlashCamera(cameraManager) }
    val cameraId = selection.cameraId
    var torchEnabled by remember(cameraId) { mutableStateOf(false) }
    var message by remember(selection) { mutableStateOf(selection.message) }

    DisposableEffect(cameraManager, cameraId) {
        if (cameraId == null) {
            onDispose {}
        } else {
            val callback = object : CameraManager.TorchCallback() {
                override fun onTorchModeChanged(changedCameraId: String, enabled: Boolean) {
                    if (changedCameraId == cameraId) {
                        torchEnabled = enabled
                        message = if (enabled) "Linterna encendida." else "Linterna apagada."
                    }
                }

                override fun onTorchModeUnavailable(unavailableCameraId: String) {
                    if (unavailableCameraId == cameraId) {
                        torchEnabled = false
                        message = "La linterna no está disponible en este momento."
                    }
                }
            }
            cameraManager.registerTorchCallback(callback, Handler(Looper.getMainLooper()))

            onDispose {
                cameraManager.unregisterTorchCallback(callback)
                if (torchEnabled) {
                    try {
                        cameraManager.setTorchMode(cameraId, false)
                    } catch (e: CameraAccessException) {
                        Log.e("GestionInventario", "No se pudo apagar la linterna al salir", e)
                    } catch (e: IllegalArgumentException) {
                        Log.e("GestionInventario", "No se pudo apagar la linterna al salir", e)
                    }
                }
            }
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (cameraId != null) {
                        try {
                            cameraManager.setTorchMode(cameraId, !torchEnabled)
                        } catch (e: CameraAccessException) {
                            message = "No se pudo cambiar el estado de la linterna."
                            Log.e("GestionInventario", "No se pudo cambiar la linterna", e)
                        } catch (e: IllegalArgumentException) {
                            message = "No se pudo cambiar el estado de la linterna."
                            Log.e("GestionInventario", "No se pudo cambiar la linterna", e)
                        }
                    }
                },
                enabled = cameraId != null
            ) {
                Text(if (torchEnabled) "Linterna: encendida" else "Linterna: apagada")
            }
            message?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
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