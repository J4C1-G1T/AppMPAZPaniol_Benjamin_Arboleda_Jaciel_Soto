package com.proyecto.appmpazpaniol.ui.navigation

sealed class Screen(val route: String) {
    data object Catalogo : Screen("catalogo_screen")
    data object CrearSolicitud : Screen("crear_solicitud_screen")
    data object MisSolicitudes : Screen("mis_solicitudes_screen")

    data object PanelAdmin : Screen("panel_admin_screen")
    data object GestionInventario : Screen("gestion_inventario_screen")

    data object Login : Screen("login_screen")
    data object Register : Screen("register_screen")
}