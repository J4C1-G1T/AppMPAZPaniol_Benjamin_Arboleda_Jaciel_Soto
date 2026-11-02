package com.proyecto.appmpazpaniol.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.proyecto.appmpazpaniol.model.Equipo
import com.proyecto.appmpazpaniol.ui.screens.AdminScreen
import com.proyecto.appmpazpaniol.ui.screens.AjustesScreen
import com.proyecto.appmpazpaniol.ui.screens.CatalogoScreen
import com.proyecto.appmpazpaniol.ui.screens.GestionInventarioScreen
import com.proyecto.appmpazpaniol.ui.screens.LoginScreen
import com.proyecto.appmpazpaniol.ui.screens.MisSolicitudesScreen
import com.proyecto.appmpazpaniol.ui.screens.PerfilScreen
import com.proyecto.appmpazpaniol.ui.screens.RegisterScreen
import com.proyecto.appmpazpaniol.ui.screens.SolicitudScreen

private data class NavigationItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun AppNavigation(
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    var nombreUsuario by remember { mutableStateOf("") }
    var correoUsuario by remember { mutableStateOf("") }
    var esAdministrador by remember { mutableStateOf(false) }

    val homeRoute = if (esAdministrador) Screen.PanelAdmin.route else Screen.Catalogo.route
    val navigationItems = if (esAdministrador) {
        listOf(
            NavigationItem(Screen.PanelAdmin.route, "Inicio", Icons.Default.Home),
            NavigationItem(Screen.GestionInventario.route, "Inventario", Icons.AutoMirrored.Filled.List),
            NavigationItem(Screen.Perfil.route, "Perfil", Icons.Default.Person),
            NavigationItem(Screen.Ajustes.route, "Ajustes", Icons.Default.Settings)
        )
    } else {
        listOf(
            NavigationItem(Screen.Catalogo.route, "Inicio", Icons.Default.Home),
            NavigationItem(Screen.MisSolicitudes.route, "Solicitudes", Icons.AutoMirrored.Filled.List),
            NavigationItem(Screen.Perfil.route, "Perfil", Icons.Default.Person),
            NavigationItem(Screen.Ajustes.route, "Ajustes", Icons.Default.Settings)
        )
    }
    val showNavigationBar = navigationItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showNavigationBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    navigationItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(homeRoute) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onLoginAdmin = { nombre, correo ->
                        nombreUsuario = nombre
                        correoUsuario = correo
                        esAdministrador = true
                        navController.navigate(Screen.PanelAdmin.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onLoginUsuario = { nombre, correo ->
                        nombreUsuario = nombre
                        correoUsuario = correo
                        esAdministrador = false
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
                    onEquipoSeleccionado = {
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
                    onConfirmarSolicitud = {
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

            composable(route = Screen.Perfil.route) {
                PerfilScreen(
                    nombre = nombreUsuario,
                    correo = correoUsuario,
                    rol = if (esAdministrador) "Administrador" else "Usuario",
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }

            composable(route = Screen.Ajustes.route) {
                AjustesScreen(
                    darkTheme = darkTheme,
                    onDarkThemeChange = onDarkThemeChange,
                    onVolver = {
                        navController.popBackStack()
                    },
                    onCerrarSesion = {
                        nombreUsuario = ""
                        correoUsuario = ""
                        esAdministrador = false
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
