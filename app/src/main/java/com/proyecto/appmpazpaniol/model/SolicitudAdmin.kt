package com.proyecto.appmpazpaniol.model

/**
 * Representa una solicitud de préstamo gestionada por administración.
 */
data class SolicitudAdmin(
    val id: String,
    val solicitante: String,
    val cargo: String,
    val equipo: String,
    val sala: String,
    val fecha: String,
    val horaInicio: String,
    val horaDevolucion: String,
    val estado: String,
    val motivoRechazo: String = ""
)
