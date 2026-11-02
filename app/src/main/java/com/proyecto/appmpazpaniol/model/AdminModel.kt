package com.proyecto.appmpazpaniol.model

data class AdminModel(
    val solicitudes: List<SolicitudAdmin> = listOf(),
    val motivoRechazo: String = "",
    val motivoRechazoError: String? = null
)
