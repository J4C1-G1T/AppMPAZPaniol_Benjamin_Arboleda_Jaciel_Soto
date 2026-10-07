package com.proyecto.appmpazpaniol.ui.navigation

/**
 * Sealed class que define las rutas de navegación de forma segura (Type-Safe).
 * Evita errores tipográficos al cambiar entre pantallas.
 */
sealed class Screen(val route: String) {
    data object Catalogo : Screen("catalogo_screen")
    data object Carrito : Screen("carrito_screen")
    data object MisSolicitudes : Screen("mis_solicitudes_screen")
    data object AdminConsole : Screen("admin_console_screen")
}