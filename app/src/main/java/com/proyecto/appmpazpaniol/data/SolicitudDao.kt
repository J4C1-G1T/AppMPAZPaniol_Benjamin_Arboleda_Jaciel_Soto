package com.proyecto.appmpazpaniol.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz DAO que define las operaciones de lectura/escritura en SQLite.
 */
@Dao
interface SolicitudDao {

    // Retorna un Flow reactivo: La UI se actualizará automáticamente ante cambios
    @Query("SELECT * FROM tabla_solicitudes ORDER BY id DESC")
    fun obtenerTodasLasSolicitudes(): Flow<List<SolicitudEntity>>

    // Inserta una nueva solicitud en la base de datos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarSolicitud(solicitud: SolicitudEntity)

    // Permite al Administrador cambiar el estado de la solicitud
    @Query("UPDATE tabla_solicitudes SET estado = :nuevoEstado WHERE id = :solicitudId")
    suspend fun actualizarEstadoSolicitud(solicitudId: Int, nuevoEstado: String)
}