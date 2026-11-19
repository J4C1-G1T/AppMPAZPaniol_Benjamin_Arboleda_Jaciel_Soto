package com.proyecto.appmpazpaniol.data

import android.util.Log
import com.proyecto.appmpazpaniol.data.local.dao.EquipoDao
import com.proyecto.appmpazpaniol.data.local.entity.EquipoEntity
import com.proyecto.appmpazpaniol.data.remote.EquipoDto
import com.proyecto.appmpazpaniol.data.remote.InventarioDto
import com.proyecto.appmpazpaniol.data.remote.RetrofitClient
import com.proyecto.appmpazpaniol.data.remote.SolicitudDto
import com.proyecto.appmpazpaniol.model.Equipo
import com.proyecto.appmpazpaniol.model.EstadoSolicitud
import java.io.EOFException
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
            coroutineScope {
                val equiposDeferred = async { RetrofitClient.equiposApi.obtenerTodosLosEquipos() }
                val inventarioDeferred = async { RetrofitClient.inventarioApi.obtenerTodoElInventario() }

                val listaEquipos = equiposDeferred.await()
                val mapaInventario = inventarioDeferred.await().associateBy { it.equipoId }

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
            ejecutarSinFalloCuerpoVacio {
                RetrofitClient.inventarioApi.darDeBajaEquipo(id)
            }
        } catch (e: Exception) {
            // Si el equipo estaba huérfano en :8082 (sin fila en :8081), creamos su baja en :8081
            Log.w("PaniolRepository", "Equipo $id sin fila previa en inventario, registrando baja: ${e.message}")
            try {
                ejecutarSinFalloCuerpoVacio {
                    RetrofitClient.inventarioApi.crearInventario(
                        InventarioDto(
                            equipoId = id,
                            stockTotal = 0,
                            stockDisponible = 0,
                            estado = "Fuera de Servicio",
                            ubicacionFisica = "Bodega Pañol"
                        )
                    )
                }
            } catch (e2: Exception) {
                Log.e("PaniolRepository", "Error al registrar baja en AWS: ${e2.message}")
            }
        }
        equipoDao.updateEstado(id, "Fuera de Servicio")
    }

    suspend fun cambiarEstadoEquipo(id: String, estado: String) {
        val estadoValido = normalizarEstadoInventario(estado)
        try {
            ejecutarSinFalloCuerpoVacio {
                RetrofitClient.inventarioApi.cambiarEstadoInventario(id, estadoValido)
            }
        } catch (e: Exception) {
            Log.e("PaniolRepository", "Error al cambiar estado de inventario en AWS: ${e.message}")
        }
        equipoDao.updateEstado(id, estadoValido)
    }

    suspend fun actualizarStockDisponible(id: String, stockDisponible: Int) {
        try {
            ejecutarSinFalloCuerpoVacio {
                RetrofitClient.inventarioApi.actualizarStockInventario(id, stockDisponible)
            }
        } catch (e: Exception) {
            Log.e("PaniolRepository", "Error al actualizar stock en AWS: ${e.message}")
        }
        equipoDao.updateStockDisponible(id, stockDisponible)
    }

    suspend fun guardarEquipoRemoto(equipo: Equipo, esEdicion: Boolean) {
        val idLimpio = equipo.id.trim()
        val nombreLimpio = equipo.nombre.trim()
        val marcaLimpia = equipo.marca.trim()
        val modeloLimpio = equipo.modelo.trim()
        val categoriaLimpia = equipo.categoria.trim()
        val estadoLimpio = normalizarEstadoInventario(equipo.estado)
        val ubicacionLimpia = equipo.ubicacionFisica.trim().ifBlank { "Bodega Pañol" }

        val equipoDto = EquipoDto(
            id = idLimpio,
            nombre = nombreLimpio,
            marca = marcaLimpia,
            modelo = modeloLimpio,
            categoria = categoriaLimpia
        )
        val inventarioDto = InventarioDto(
            equipoId = idLimpio,
            stockTotal = equipo.stockTotal,
            stockDisponible = equipo.stockDisponible,
            estado = estadoLimpio,
            ubicacionFisica = ubicacionLimpia
        )

        if (esEdicion) {
            ejecutarSinFalloCuerpoVacio {
                RetrofitClient.equiposApi.actualizarEquipo(idLimpio, equipoDto)
            }
            try {
                ejecutarSinFalloCuerpoVacio {
                    RetrofitClient.inventarioApi.actualizarInventario(idLimpio, inventarioDto)
                }
            } catch (e: Exception) {
                ejecutarSinFalloCuerpoVacio {
                    RetrofitClient.inventarioApi.crearInventario(inventarioDto)
                }
            }
        } else {
            ejecutarSinFalloCuerpoVacio {
                RetrofitClient.equiposApi.crearEquipo(equipoDto)
            }
            try {
                ejecutarSinFalloCuerpoVacio {
                    RetrofitClient.inventarioApi.crearInventario(inventarioDto)
                }
            } catch (e: Exception) {
                ejecutarSinFalloCuerpoVacio {
                    RetrofitClient.inventarioApi.actualizarInventario(idLimpio, inventarioDto)
                }
            }
        }

        equipoDao.insertEquipo(
            EquipoEntity(
                id = idLimpio,
                nombre = nombreLimpio,
                marca = marcaLimpia,
                modelo = modeloLimpio,
                categoria = categoriaLimpia,
                stockTotal = equipo.stockTotal,
                stockDisponible = equipo.stockDisponible,
                estado = estadoLimpio,
                ubicacionFisica = ubicacionLimpia
            )
        )
    }

    // Garantiza que el estado coincida exactamente con ESTADOS_PERMITIDOS en Spring Boot
    private fun normalizarEstadoInventario(estadoRaw: String): String {
        return when (estadoRaw.trim().lowercase()) {
            "disponible" -> "Disponible"
            "reservado" -> "Reservado"
            "prestado" -> "Prestado"
            "en mantenimiento", "mantenimiento" -> "En Mantenimiento"
            "fuera de servicio" -> "Fuera de Servicio"
            else -> "Disponible"
        }
    }

    private suspend inline fun ejecutarSinFalloCuerpoVacio(accion: () -> Unit) {
        try {
            accion()
        } catch (_: EOFException) {
            // Respuesta vacía válida del servidor
        }
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