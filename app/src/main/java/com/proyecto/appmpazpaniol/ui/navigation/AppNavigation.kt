package com.proyecto.appmpazpaniol.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proyecto.appmpazpaniol.model.Equipo
import com.proyecto.appmpazpaniol.ui.screens.CatalogoScreen
import com.proyecto.appmpazpaniol.ui.screens.MisSolicitudesScreen
import com.proyecto.appmpazpaniol.ui.screens.SolicitudScreen
import com.proyecto.appmpazpaniol.viewmodel.SolicitanteUiState
import com.proyecto.appmpazpaniol.viewmodel.SolicitanteViewModel

/**
 * Grafo de navegación centralizado que conecta la capa UI con la capa ViewModel (MVVM).
 */
@Composable
fun AppNavigation(
    // Inyección de la instancia del ViewModel usando la librería de Compose
    solicitanteViewModel: SolicitanteViewModel = viewModel()
) {
    val navController = rememberNavController()

    // Suscripción reactiva al StateFlow del ViewModel
    val uiState by solicitanteViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Catalogo.route
    ) {
        // Ruta 1: Catálogo de Equipos
        composable(route = Screen.Catalogo.route) {
            // Renderizado condicional según el estado expuesto por el ViewModel (Sealed Class)
            when (val state = uiState) {
                is SolicitanteUiState.Cargando -> {
                    // Muestra pantalla base de carga si los datos se están procesando
                }
                is SolicitanteUiState.Exito -> {
                    CatalogoScreen(
                        onEquipoSeleccionado = { equipo ->
                            navController.navigate(Screen.CrearSolicitud.route)
                        }
                    )
                }
                is SolicitanteUiState.Error -> {
                    // Manejo de error visual
                }
            }
        }

        // Ruta 2: Crear Solicitud de Préstamo (Regla de 3 horas)
        composable(route = Screen.CrearSolicitud.route) {
            val equipoSimulado = Equipo(
                id = "1",
                nombre = "Proyector Epson EB-X06",
                marca = "Epson",
                modelo = "EB-X06",
                categoria = "Proyector",
                stockTotal = 5,
                stockDisponible = 3,
                estado = "Disponible",
                ubicacionFisica = "Estante A1"
            )

            SolicitudScreen(
                equipo = equipoSimulado,
                onConfirmarSolicitud = { horas ->
                    // Llama a la lógica de negocio centralizada en el ViewModel
                    solicitanteViewModel.crearSolicitudPréstamo(
                        equipo = equipoSimulado,
                        horasAnticipacion = horas,
                        motivo = "Reserva desde App"
                    )
                    navController.popBackStack()
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // Ruta 3: Historial del Solicitante (Mis Solicitudes)
        composable(route = Screen.MisSolicitudes.route) {
            MisSolicitudesScreen(
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}