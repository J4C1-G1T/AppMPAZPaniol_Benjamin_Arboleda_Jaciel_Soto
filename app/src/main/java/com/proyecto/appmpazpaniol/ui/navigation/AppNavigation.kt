package com.proyecto.appmpazpaniol.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.proyecto.appmpazpaniol.data.AppDatabase
import com.proyecto.appmpazpaniol.data.PaniolRepository
import com.proyecto.appmpazpaniol.model.Equipo
import com.proyecto.appmpazpaniol.ui.screens.*

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
    val context = LocalContext.current
    val repository = remember {
        val db = AppDatabase.obtenerBaseDatos(context)
        PaniolRepository(db.solicitudDao(), db.equipoDao())
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    var nombreUsuario by remember { mutableStateOf("") }
    var correoUsuario by remember { mutableStateOf("") }
    var esAdministrador by remember { mutableStateOf(false) }
    var equipoSeleccionado by remember { mutableStateOf<Equipo?>(null) }

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
                    onEquipoSeleccionado = { equipo ->
                        equipoSeleccionado = equipo
                        navController.navigate(Screen.CrearSolicitud.route)
                    },
                    repository = repository
                )
            }

            composable(route = Screen.CrearSolicitud.route) {
                val equipoActual = equipoSeleccionado ?: Equipo(
                    id = "1",
                    nombre = "Computador HP ProDesk",
                    marca = "HP",
                    modelo = "ProDesk 400",
                    categoria = "Computador",
                    stockTotal = 8,
                    stockDisponible = 5,
                    estado = "Disponible",
                    ubicacionFisica = "Estante A1"
                )

                SolicitudScreen(
                    equipo = equipoActual,
                    onConfirmarSolicitud = {
                        navController.popBackStack()
                    },
                    onVolver = {
                        navController.popBackStack()
                    },
                    correoUsuario = correoUsuario,
                    repository = repository
                )
            }

            composable(route = Screen.MisSolicitudes.route) {
                MisSolicitudesScreen(
                    onVolver = {
                        navController.popBackStack()
                    },
                    correoUsuario = correoUsuario,
                    repository = repository
                )
            }

            composable(route = Screen.PanelAdmin.route) {
                AdminScreen(
                    onGestionInventario = {
                        navController.navigate(Screen.GestionInventario.route)
                    },
                    onVolver = {
                        navController.popBackStack()
                    },
                    repository = repository
                )
            }

            composable(route = Screen.GestionInventario.route) {
                GestionInventarioScreen(
                    onVolver = {
                        navController.popBackStack()
                    },
                    repository = repository
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