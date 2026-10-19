package com.proyecto.appmpazpaniol.model

/**
 * Contiene los datos de interfaz del panel de administración.
 */
data class AdminModel(
    val solicitudes: List<SolicitudAdmin> = listOf(),
    val motivoRechazo: String = "",
    val motivoRechazoError: String? = null
)
