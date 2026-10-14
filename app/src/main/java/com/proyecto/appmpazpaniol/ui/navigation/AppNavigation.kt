package com.proyecto.appmpazpaniol.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proyecto.appmpazpaniol.model.Equipo
import com.proyecto.appmpazpaniol.ui.screens.CatalogoScreen
import com.proyecto.appmpazpaniol.ui.screens.SolicitudScreen

/**
 * Contenedor principal de navegacion declarativa para la app MPAZ Pañol.
 * Gestiona el intercambio de pantallas mediante el NavController.
 */
@Composable
fun AppNavigation() {
    // NavController rastrea la pila de navegacion (pantallas anteriores y actuales)
    val navController = rememberNavController()

    // NavHost define el grafo de navegacion e inicia en el Catalogo
    NavHost(
        navController = navController,
        startDestination = Screen.Catalogo.route
    ) {
        // Ruta 1: Catálogo de Equipos
        composable(route = Screen.Catalogo.route) {
            CatalogoScreen(
                onEquipoSeleccionado = { equipo ->
                    // Navega a la pantalla de solicitud llevando el contexto del equipo
                    navController.navigate(Screen.CrearSolicitud.route)
                }
            )
        }

        // Ruta 2: Crear Solicitud de Préstamo (Regla de 3 horas)
        composable(route = Screen.CrearSolicitud.route) {
            // Equipo simulado para el flujo de prueba
            val equipoSeleccionado = Equipo(
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
                equipo = equipoSeleccionado,
                onConfirmarSolicitud = { horas ->
                    // Al confirmar, regresa al catálogo principal
                    navController.popBackStack()
                },
                onVolver = {
                    // Cancela y regresa a la pantalla anterior
                    navController.popBackStack()
                }
            )
        }
    }
}