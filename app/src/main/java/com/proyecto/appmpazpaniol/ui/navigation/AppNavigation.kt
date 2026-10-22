package com.proyecto.appmpazpaniol.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proyecto.appmpazpaniol.model.Equipo
import com.proyecto.appmpazpaniol.ui.screens.AdminScreen
import com.proyecto.appmpazpaniol.ui.screens.CatalogoScreen
import com.proyecto.appmpazpaniol.ui.screens.GestionInventarioScreen
import com.proyecto.appmpazpaniol.ui.screens.MisSolicitudesScreen
import com.proyecto.appmpazpaniol.ui.screens.SolicitudScreen

/**
 * Contenedor principal de navegación declarativa para la app MPAZ Pañol.
 * Gestiona el intercambio de pantallas mediante el NavController.
 */
@Composable
fun AppNavigation() {
    // NavController rastrea la pila de navegación (BackStack)
    val navController = rememberNavController()

    // NavHost define el grafo de navegación e inicia en el Catálogo
    NavHost(
        navController = navController,
        startDestination = Screen.Catalogo.route
    ) {
        // Ruta 1: Catálogo de Equipos (Inicio)
        composable(route = Screen.Catalogo.route) {
            CatalogoScreen(
                onSolicitarEquipo = { equipo ->
                    // Navega a la pantalla de solicitud llevando la ruta configurada
                    navController.navigate(Screen.CrearSolicitud.route)
                },
                onVerMisSolicitudes = {
                    navController.navigate(Screen.MisSolicitudes.route)
                },
                onIrAdmin = {
                    navController.navigate(Screen.PanelAdmin.route)
                }
            )
        }

        // Ruta 2: Crear Solicitud de Préstamo
        composable(route = Screen.CrearSolicitud.route) {
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
                    // Al confirmar, regresa al catálogo principal desapilando
                    navController.popBackStack()
                },
                onVolver = {
                    // Cancela y regresa a la pantalla anterior
                    navController.popBackStack()
                }
            )
        }

        // Ruta 3: Historial del Solicitante
        composable(route = Screen.MisSolicitudes.route) {
            MisSolicitudesScreen(
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // Ruta 4: Panel de Administración
        composable(route = Screen.PanelAdmin.route) {
            AdminScreen(
                onGestionInventario = {
                    navController.navigate(Screen.GestionInventario.route)
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // Ruta 5: Gestión de Inventario
        composable(route = Screen.GestionInventario.route) {
            GestionInventarioScreen(
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}