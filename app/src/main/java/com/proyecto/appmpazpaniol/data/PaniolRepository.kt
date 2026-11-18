package com.proyecto.appmpazpaniol.data

import android.util.Log
import com.proyecto.appmpazpaniol.data.local.dao.EquipoDao
import com.proyecto.appmpazpaniol.data.local.entity.EquipoEntity
import com.proyecto.appmpazpaniol.data.remote.RetrofitClient
import com.proyecto.appmpazpaniol.data.remote.SolicitudDto
import com.proyecto.appmpazpaniol.model.EstadoSolicitud
import com.proyecto.appmpazpaniol.model.Equipo
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

class PaniolRepository(
    private val solicitudDao: SolicitudDao,
    private val equipoDao: EquipoDao
) {

    // =========================================================================
    // 1. EQUIPOS + INVENTARIO (Microservicios :8082 y :8081 + Caché Room)
    // =========================================================================

    suspend fun obtenerEquipos(): List<EquipoEntity> {
        return try {
            // Consultamos ambos microservicios en paralelo para reducir tiempos de espera
            coroutineScope {
                val equiposDeferred = async { RetrofitClient.equiposApi.obtenerTodosLosEquipos() }
                val inventarioDeferred = async { RetrofitClient.inventarioApi.obtenerTodoElInventario() }

                val listaEquipos = equiposDeferred.await()
                val mapaInventario = inventarioDeferred.await().associateBy { it.equipoId }

                // Combinamos los datos descriptivos (:8082) con stock y ubicación (:8081)
                val entidadesCombinadas = listaEquipos.map { eq ->
                    val inv = mapaInventario[eq.id]
                    EquipoEntity(
                        id = eq.id,
                        nombre = eq.nombre,
                        marca = eq.marca,
                        modelo = eq.modelo,
                        categoria = eq.categoria,
                        stockTotal = inv?.stockTotal ?: 0,
                        stockDisponible = inv?.stockDisponible ?: 0,
                        estado = inv?.estado ?: "Sin Inventario",
                        ubicacionFisica = inv?.ubicacionFisica ?: "Sin ubicación"
                    )
                }

                // Guardamos en Room como respaldo local
                entidadesCombinadas.forEach { equipoDao.insertEquipo(it) }
                entidadesCombinadas
            }
        } catch (e: Exception) {
            Log.e("PaniolRepository", "Error al obtener equipos desde AWS, usando caché Room: ${e.message}")
            equipoDao.getAllEquipos()
        }
    }

    suspend fun obtenerEquipo(id: String): EquipoEntity? {
        return equipoDao.getEquipoById(id)
    }

    suspend fun guardarEquipo(equipo: EquipoEntity) {
        equipoDao.insertEquipo(equipo)
    }

    suspend fun darDeBajaEquipo(id: String) {
        try {
            RetrofitClient.inventarioApi.darDeBajaEquipo(id)
        } catch (e: Exception) {
            Log.e("PaniolRepository", "Error al dar de baja en AWS: ${e.message}")
        }
        equipoDao.updateEstado(id, "Fuera de Servicio")
    }

    suspend fun cambiarEstadoEquipo(id: String, estado: String) {
        try {
            RetrofitClient.inventarioApi.cambiarEstadoInventario(id, estado)
        } catch (e: Exception) {
            Log.e("PaniolRepository", "Error al cambiar estado de inventario en AWS: ${e.message}")
        }
        equipoDao.updateEstado(id, estado)
    }

    suspend fun actualizarStockDisponible(id: String, stockDisponible: Int) {
        try {
            RetrofitClient.inventarioApi.actualizarStockInventario(id, stockDisponible)
        } catch (e: Exception) {
            Log.e("PaniolRepository", "Error al actualizar stock en AWS: ${e.message}")
        }
        equipoDao.updateStockDisponible(id, stockDisponible)
    }

    // =========================================================================
    // 2. SOLICITUDES (Microservicio :8080 + Normalización de Estados)
    // =========================================================================

    val todasLasSolicitudes: Flow<List<SolicitudEntity>> = solicitudDao.obtenerTodasLasSolicitudes()

    suspend fun obtenerSolicitudesRemotas(): List<SolicitudDto> {
        val remotas = RetrofitClient.solicitudesApi.obtenerTodasLasSolicitudes()
        return remotas.map { dto ->
            dto.copy(estado = normalizarEstadoParaApp(dto.estado))
        }
    }

    suspend fun crearSolicitudRemota(solicitud: SolicitudDto): SolicitudDto {
        val creada = RetrofitClient.solicitudesApi.crearSolicitud(solicitud)
        return creada.copy(estado = normalizarEstadoParaApp(creada.estado))
    }

    suspend fun cambiarEstadoSolicitudRemota(id: Long, nuevoEstadoApp: String): SolicitudDto {
        // Enviamos en MAYÚSCULAS a Spring Boot ("ACEPTADA", "ENTREGADA", "DEVUELTA")
        val estadoBackend = nuevoEstadoApp.uppercase()
        val actualizada = RetrofitClient.solicitudesApi.cambiarEstado(id, estadoBackend)
        return actualizada.copy(estado = normalizarEstadoParaApp(actualizada.estado))
    }

    suspend fun rechazarSolicitudRemota(id: Long, motivo: String): SolicitudDto {
        val rechazada = RetrofitClient.solicitudesApi.rechazarSolicitud(id, motivo)
        return rechazada.copy(estado = normalizarEstadoParaApp(rechazada.estado))
    }

    suspend fun guardarSolicitud(solicitud: SolicitudEntity) {
        solicitudDao.insertarSolicitud(solicitud)
    }

    suspend fun cambiarEstadoSolicitud(id: Int, nuevoEstado: String) {
        solicitudDao.actualizarEstadoSolicitud(id, nuevoEstado)
    }

    suspend fun guardarEquipoRemoto(equipo: Equipo, esEdicion: Boolean) {
        val equipoDto = com.proyecto.appmpazpaniol.data.remote.EquipoDto(
            id = equipo.id,
            nombre = equipo.nombre,
            marca = equipo.marca,
            modelo = equipo.modelo,
            categoria = equipo.categoria
        )
        val inventarioDto = com.proyecto.appmpazpaniol.data.remote.InventarioDto(
            equipoId = equipo.id,
            stockTotal = equipo.stockTotal,
            stockDisponible = equipo.stockDisponible,
            estado = equipo.estado,
            ubicacionFisica = equipo.ubicacionFisica
        )

        if (esEdicion) {
            RetrofitClient.equiposApi.actualizarEquipo(equipo.id, equipoDto)
            RetrofitClient.inventarioApi.actualizarInventario(equipo.id, inventarioDto)
        } else {
            RetrofitClient.equiposApi.crearEquipo(equipoDto)
            RetrofitClient.inventarioApi.crearInventario(inventarioDto)
        }

        // Actualizamos también la caché local en Room
        equipoDao.insertEquipo(
            EquipoEntity(
                id = equipo.id,
                nombre = equipo.nombre,
                marca = equipo.marca,
                modelo = equipo.modelo,
                categoria = equipo.categoria,
                stockTotal = equipo.stockTotal,
                stockDisponible = equipo.stockDisponible,
                estado = equipo.estado,
                ubicacionFisica = equipo.ubicacionFisica
            )
        )
    }



    // Traduce "PENDIENTE" (Backend) -> "Pendiente" (EstadoSolicitud en Android)
    private fun normalizarEstadoParaApp(estadoBackend: String): String {
        return when (estadoBackend.uppercase()) {
            "PENDIENTE" -> EstadoSolicitud.PENDIENTE
            "ACEPTADA"  -> EstadoSolicitud.ACEPTADA
            "RECHAZADA" -> EstadoSolicitud.RECHAZADA
            "ENTREGADA" -> EstadoSolicitud.ENTREGADA
            "DEVUELTA"  -> EstadoSolicitud.DEVUELTA
            else        -> estadoBackend
        }
    }
}