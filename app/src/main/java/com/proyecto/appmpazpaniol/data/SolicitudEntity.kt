package com.proyecto.appmpazpaniol.data



import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que mapea la tabla 'tabla_solicitudes' en la Base de Datos Room.
 * Almacena de forma persistente cada préstamo solicitado en el teléfono.
 */
@Entity(tableName = "tabla_solicitudes")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,               // Identificador único autogenerado por Room
    val equipoId: String,          // ID del equipo asociado
    val nombreEquipo: String,      // Nombre del equipo guardado
    val horasAnticipacion: Int,    // Regla de negocio: mínimo 3 horas
    val motivo: String,            // Motivo del préstamo ingresado por el usuario
    val estado: String = "Pendiente" // Estado: "Pendiente", "Aprobado", "Rechazado"
)