package com.proyecto.appmpazpaniol.data

import kotlinx.coroutines.flow.Flow
import com.proyecto.appmpazpaniol.data.local.dao.EquipoDao
import com.proyecto.appmpazpaniol.data.local.entity.EquipoEntity

class PaniolRepository(
    private val solicitudDao: SolicitudDao,
    private val equipoDao: EquipoDao
) {

    suspend fun obtenerEquipos(): List<EquipoEntity> {
        return equipoDao.getAllEquipos()
    }

    suspend fun obtenerEquipo(id: String): EquipoEntity? {
        return equipoDao.getEquipoById(id)
    }

    suspend fun guardarEquipo(equipo: EquipoEntity) {
        return equipoDao.insertEquipo(equipo)
    }

    suspend fun darDeBajaEquipo(id: String) {
        return equipoDao.updateEstado(id, "Fuera de Servicio")
    }

    suspend fun cambiarEstadoEquipo(id: String, estado: String) {
        return equipoDao.updateEstado(id, estado)
    }

    suspend fun actualizarStockDisponible(id: String, stockDisponible: Int) {
        return equipoDao.updateStockDisponible(id, stockDisponible)
    }

    val todasLasSolicitudes: Flow<List<SolicitudEntity>> = solicitudDao.obtenerTodasLasSolicitudes()

    suspend fun guardarSolicitud(solicitud: SolicitudEntity) {
        solicitudDao.insertarSolicitud(solicitud)
    }

    suspend fun cambiarEstadoSolicitud(id: Int, nuevoEstado: String) {
        solicitudDao.actualizarEstadoSolicitud(id, nuevoEstado)
    }
}
