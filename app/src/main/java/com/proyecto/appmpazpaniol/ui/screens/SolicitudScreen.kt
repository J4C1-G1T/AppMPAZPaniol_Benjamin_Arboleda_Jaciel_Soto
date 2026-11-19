package com.proyecto.appmpazpaniol.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.CancellationSignal
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proyecto.appmpazpaniol.data.PaniolRepository
import com.proyecto.appmpazpaniol.model.Equipo
import com.proyecto.appmpazpaniol.viewmodel.SolicitanteViewModel
import com.proyecto.appmpazpaniol.viewmodel.ValidacionState
import java.util.Locale

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
    var locationMessage by remember { mutableStateOf<String?>(null) }
    var coordinates by remember { mutableStateOf<String?>(null) }
    var locationPermissionRequested by rememberSaveable { mutableStateOf(false) }
    var openLocationSettings by remember { mutableStateOf(false) }
    val locationManager = remember(context) {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    fun obtainCurrentLocation() {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasFineLocation && !hasCoarseLocation) {
            locationMessage = "Se necesita permiso de ubicación para continuar."
            return
        }

        val provider = LocationManager.GPS_PROVIDER
        try {
            if (!locationManager.isProviderEnabled(provider)) {
                locationMessage = "Activa el GPS del dispositivo e inténtalo nuevamente."
                return
            }
        } catch (e: IllegalArgumentException) {
            locationMessage = "El proveedor GPS no está disponible en este dispositivo."
            return
        }

        coordinates = null
        locationMessage = "Obteniendo ubicación..."
        val cancellationSignal = CancellationSignal()
        val handler = Handler(Looper.getMainLooper())
        var completed = false
        val timeout = Runnable {
            if (!completed) {
                completed = true
                cancellationSignal.cancel()
                locationMessage = "No se obtuvo la ubicación en 15 segundos. Inténtalo nuevamente."
            }
        }
        handler.postDelayed(timeout, 15_000L)

        try {
            locationManager.getCurrentLocation(provider, cancellationSignal, context.mainExecutor) { location: Location? ->
                if (!completed) {
                    completed = true
                    handler.removeCallbacks(timeout)
                    if (location == null) {
                        locationMessage = "No se pudo obtener la ubicación. Inténtalo nuevamente."
                    } else {
                        val result = String.format(
                            Locale.US,
                            "%.6f, %.6f",
                            location.latitude,
                            location.longitude
                        )
                        coordinates = result
                        val priorReason = motivoUso.lineSequence()
                            .filterNot { it.startsWith("Ubicación: ") }
                            .joinToString("\n")
                            .trim()
                        motivoUso = listOf(priorReason, "Ubicación: $result")
                            .filter { it.isNotBlank() }
                            .joinToString("\n")
                        locationMessage = "Ubicación obtenida."
                    }
                }
            }
        } catch (e: SecurityException) {
            handler.removeCallbacks(timeout)
            completed = true
            locationMessage = "No se concedió el permiso de ubicación."
        } catch (e: IllegalArgumentException) {
            handler.removeCallbacks(timeout)
            completed = true
            locationMessage = "El proveedor GPS no está disponible en este dispositivo."
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (hasFineLocation || hasCoarseLocation) {
            openLocationSettings = false
            obtainCurrentLocation()
        } else {
            val activity = context as? Activity
            val shouldShowRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } ?: false
            if (locationPermissionRequested && !shouldShowRationale) {
                locationMessage = "Permiso denegado permanentemente. Puedes habilitarlo en Ajustes."
                openLocationSettings = true
            } else {
                locationMessage = "Permiso de ubicación denegado."
                openLocationSettings = false
            }
        }
    }

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

            OutlinedButton(
                onClick = {
                    val hasFineLocation = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    val hasCoarseLocation = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    if (hasFineLocation || hasCoarseLocation) {
                        obtainCurrentLocation()
                    } else {
                        locationPermissionRequested = true
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Usar mi ubicación")
            }
            locationMessage?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            coordinates?.let {
                Text("Coordenadas: $it", style = MaterialTheme.typography.bodySmall)
            }
            if (openLocationSettings) {
                TextButton(
                    onClick = {
                        val settingsIntent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                        context.startActivity(settingsIntent)
                    }
                ) {
                    Text("Abrir ajustes de la aplicación")
                }
            }

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