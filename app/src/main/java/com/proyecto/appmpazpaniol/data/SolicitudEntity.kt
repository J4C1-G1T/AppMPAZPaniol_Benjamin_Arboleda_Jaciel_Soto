package com.proyecto.appmpazpaniol.data



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabla_solicitudes")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val equipoId: String,
    val nombreEquipo: String,
    val horasAnticipacion: Int,
    val motivo: String,
    val estado: String = "Pendiente"
)
