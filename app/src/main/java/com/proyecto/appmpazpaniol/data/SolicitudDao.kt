package com.proyecto.appmpazpaniol.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudDao {

    @Query("SELECT * FROM tabla_solicitudes ORDER BY id DESC")
    fun obtenerTodasLasSolicitudes(): Flow<List<SolicitudEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarSolicitud(solicitud: SolicitudEntity)

    @Query("UPDATE tabla_solicitudes SET estado = :nuevoEstado WHERE id = :solicitudId")
    suspend fun actualizarEstadoSolicitud(solicitudId: Int, nuevoEstado: String)
}