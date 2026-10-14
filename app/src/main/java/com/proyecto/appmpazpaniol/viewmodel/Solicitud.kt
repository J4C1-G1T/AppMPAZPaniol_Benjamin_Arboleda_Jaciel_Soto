package com.proyecto.appmpazpaniol.viewmodel

/**
 * Representa la entidad de una solicitud de préstamo en el sistema MPAZ Pañol.
 */
data class Solicitud(
    val id: String,
    val equipoId: String,
    val nombreEquipo: String,
    val horasAnticipacion: Int,
    val motivo: String,
    val estado: String = "Pendiente" // "Pendiente", "Aprobado", "Rechazado"
)