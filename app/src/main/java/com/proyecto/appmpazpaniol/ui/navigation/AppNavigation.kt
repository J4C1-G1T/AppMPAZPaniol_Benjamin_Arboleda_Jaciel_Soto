package com.proyecto.appmpazpaniol.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proyecto.appmpazpaniol.model.Equipo
import com.proyecto.appmpazpaniol.ui.screens.LoginScreen
import com.proyecto.appmpazpaniol.ui.screens.RegisterScreen
import com.proyecto.appmpazpaniol.ui.screens.AdminScreen
import com.proyecto.appmpazpaniol.ui.screens.CatalogoScreen
import com.proyecto.appmpazpaniol.ui.screens.GestionInventarioScreen
import com.proyecto.appmpazpaniol.ui.screens.MisSolicitudesScreen
import com.proyecto.appmpazpaniol.ui.screens.SolicitudScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginAdmin = {
                    navController.navigate(Screen.PanelAdmin.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onLoginUsuario = {
                    navController.navigate(Screen.Catalogo.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Catalogo.route) {
            CatalogoScreen(
                onEquipoSeleccionado = { equipo ->
                    navController.navigate(Screen.CrearSolicitud.route)
                }
            )
        }

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
                    navController.popBackStack()
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.MisSolicitudes.route) {
            MisSolicitudesScreen(
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.PanelAdmin.route) {
            AdminScreen(
                onGestionInventario = {
                    navController.navigate(Screen.GestionInventario.route)
                }
            )
        }

        composable(route = Screen.GestionInventario.route) {
            GestionInventarioScreen(
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}