package com.proyecto.appmpazpaniol.data

import kotlinx.coroutines.flow.Flow
import com.proyecto.appmpazpaniol.data.local.dao.EquipoDao
import com.proyecto.appmpazpaniol.data.local.entity.EquipoEntity

/**
 * Repositorio unificado (Single Source of Truth) para la capa de datos.
 */
class PaniolRepository(
    private val solicitudDao: SolicitudDao,
    private val equipoDao: EquipoDao
) {

    // Obtiene todos los equipos guardados.
    suspend fun obtenerEquipos(): List<EquipoEntity> {
        return equipoDao.getAllEquipos()
    }

    // Obtiene un equipo por su código.
    suspend fun obtenerEquipo(id: String): EquipoEntity? {
        return equipoDao.getEquipoById(id)
    }

    // Guarda un equipo nuevo o reemplaza uno existente.
    suspend fun guardarEquipo(equipo: EquipoEntity) {
        return equipoDao.insertEquipo(equipo)
    }

    // Marca un equipo como fuera de servicio.
    suspend fun darDeBajaEquipo(id: String) {
        return equipoDao.updateEstado(id, "Fuera de Servicio")
    }

    // Cambia el estado de un equipo.
    suspend fun cambiarEstadoEquipo(id: String, estado: String) {
        return equipoDao.updateEstado(id, estado)
    }

    // Actualiza el stock disponible de un equipo.
    suspend fun actualizarStockDisponible(id: String, stockDisponible: Int) {
        return equipoDao.updateStockDisponible(id, stockDisponible)
    }

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

//Se confirma que los metodos del Solicitante de paniolRepository funcionan
