package com.proyecto.appmpazpaniol.data

import kotlinx.coroutines.flow.Flow

/**
 * Repositorio unificado (Single Source of Truth) para la capa de datos.
 */
class PaniolRepository(private val solicitudDao: SolicitudDao) {

    // Expone la lista reactiva de solicitudes guardadas
    val todasLasSolicitudes: Flow<List<SolicitudEntity>> = solicitudDao.obtenerTodasLasSolicitudes()

    // Llama al DAO de forma asíncrona mediante corutinas
    suspend fun guardarSolicitud(solicitud: SolicitudEntity) {
        solicitudDao.insertarSolicitud(solicitud)
    }

    suspend fun cambiarEstadoSolicitud(id: Int, nuevoEstado: String) {
        solicitudDao.actualizarEstadoSolicitud(id, nuevoEstado)
    }
}