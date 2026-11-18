package com.proyecto.appmpazpaniol.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.appmpazpaniol.data.PaniolRepository
import com.proyecto.appmpazpaniol.data.SolicitudEntity
import com.proyecto.appmpazpaniol.data.remote.SolicitudDto
import com.proyecto.appmpazpaniol.model.Equipo
import com.proyecto.appmpazpaniol.ui.screens.SolicitudHistorialMock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed class ValidacionState {
    object Idle : ValidacionState()
    object Exito : ValidacionState()
    data class Error(val mensaje: String) : ValidacionState()
}

class SolicitanteViewModel(private val repository: PaniolRepository? = null) : ViewModel() {

    private val _estadoValidacion = MutableStateFlow<ValidacionState>(ValidacionState.Idle)
    val estadoValidacion: StateFlow<ValidacionState> = _estadoValidacion.asStateFlow()

    var equiposCatalogo by mutableStateOf<List<Equipo>>(emptyList())
        private set

    var cargandoEquipos by mutableStateOf(false)
        private set

    var historialSolicitudes by mutableStateOf<List<SolicitudHistorialMock>>(emptyList())
        private set

    fun cargarCatalogoEquipos() {
        val repo = repository ?: return
        viewModelScope.launch {
            cargandoEquipos = true
            try {
                equiposCatalogo = repo.obtenerEquipos().map { it.toEquipo() }
            } catch (e: Exception) {
                Log.e("SolicitanteVM", "Error cargando catálogo: ${e.message}")
            } finally {
                cargandoEquipos = false
            }
        }
    }

    fun cargarHistorialSolicitudes(correoUsuario: String) {
        val repo = repository ?: return
        viewModelScope.launch {
            try {
                val mapaEquipos = repo.obtenerEquipos().associateBy { it.id }
                val remotas = repo.obtenerSolicitudesRemotas()

                // Si el usuario tiene correo activo filtramos sus solicitudes; si está vacío mostramos todas
                val filtradas = if (correoUsuario.isNotBlank()) {
                    remotas.filter { it.solicitanteEmail.equals(correoUsuario, ignoreCase = true) }
                } else {
                    remotas
                }

                historialSolicitudes = filtradas.map { dto ->
                    val nombreEq = mapaEquipos[dto.equipoId]?.nombre ?: "Equipo #${dto.equipoId}"
                    SolicitudHistorialMock(
                        id = dto.id?.toString() ?: "",
                        nombreEquipo = nombreEq,
                        fechaReserva = dto.fechaReserva,
                        horasSolicitadas = 3,
                        estado = dto.estado
                    )
                }
            } catch (e: Exception) {
                Log.e("SolicitanteVM", "Error cargando historial: ${e.message}")
            }
        }
    }

    fun crearSolicitudPrestamo(
        equipoId: String,
        nombreEquipo: String,
        solicitanteEmail: String,
        cargo: String,
        sala: String,
        horasAnticipacion: Int,
        motivo: String
    ) {
        if (motivo.isBlank()) {
            _estadoValidacion.value = ValidacionState.Error("Debe ingresar un motivo para el préstamo.")
            return
        }

        if (horasAnticipacion < 3) {
            _estadoValidacion.value = ValidacionState.Error("La solicitud debe realizarse con al menos 3 horas de anticipación.")
            return
        }

        viewModelScope.launch {
            try {
                // 1. Enviamos la solicitud real al microservicio en AWS (:8080/api/solicitudes)
                val dto = SolicitudDto(
                    equipoId = equipoId,
                    solicitanteEmail = solicitanteEmail.ifBlank { "solicitante@duocuc.cl" },
                    cargo = cargo.ifBlank { "Docente" },
                    sala = sala.ifBlank { "Sala por asignar" },
                    fechaReserva = "${LocalDate.now()} (+${horasAnticipacion}h) -$motivo"
                )
                repository?.crearSolicitudRemota(dto)

                // 2. Guardamos también respaldo en la base local Room
                val nuevaSolicitud = SolicitudEntity(
                    equipoId = equipoId,
                    nombreEquipo = nombreEquipo,
                    horasAnticipacion = horasAnticipacion,
                    motivo = motivo,
                    estado = "Pendiente"
                )
                repository?.guardarSolicitud(nuevaSolicitud)

                _estadoValidacion.value = ValidacionState.Exito
            } catch (e: Exception) {
                _estadoValidacion.value = ValidacionState.Error("Error al conectar con el servidor: ${e.message}")
            }
        }
    }

    fun limpiarEstadoValidacion() {
        _estadoValidacion.value = ValidacionState.Idle
    }
}