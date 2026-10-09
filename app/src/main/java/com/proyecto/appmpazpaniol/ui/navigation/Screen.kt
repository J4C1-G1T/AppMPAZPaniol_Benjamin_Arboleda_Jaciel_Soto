package com.proyecto.appmpazpaniol.ui.navigation

/**
 * Clase Sellada (Sealed Class) que define las rutas navegables de la app MPAZ Pañol.
 * Utilizar una sealed class evita errores tipográficos al cambiar de pantalla.
 */
sealed class Screen(val route: String) {
    // Rutas del Rol Solicitante / Usuario Normal (Jaciel)
    data object Catalogo : Screen("catalogo_screen")
    data object CrearSolicitud : Screen("crear_solicitud_screen") // <-- ¡Esta es la ruta que te faltaba!
    data object MisSolicitudes : Screen("mis_solicitudes_screen")

    // Rutas del Rol Administrador / Encargado de Pañol (Benjamín)
    data object PanelAdmin : Screen("panel_admin_screen")
    data object GestionInventario : Screen("gestion_inventario_screen")
}