package com.proyecto.appmpazpaniol.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.appmpazpaniol.data.PaniolRepository
import com.proyecto.appmpazpaniol.model.AdminModel
import com.proyecto.appmpazpaniol.model.EstadoSolicitud
import com.proyecto.appmpazpaniol.model.SolicitudAdmin
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: PaniolRepository? = null
) : ViewModel() {

    var uiState by mutableStateOf(AdminModel())
        private set

    init {
        cargarSolicitudes()
    }

    fun cargarSolicitudes() {
        val repo = repository ?: return
        viewModelScope.launch {
            try {
                // Obtenemos equipos para traducir equipoId -> nombre real del equipo
                val mapaEquipos = repo.obtenerEquipos().associateBy { it.id }
                val remotas = repo.obtenerSolicitudesRemotas()

                val listaMapeada = remotas.map { dto ->
                    val nombreEquipo = mapaEquipos[dto.equipoId]?.nombre ?: "Equipo ID: ${dto.equipoId}"
                    SolicitudAdmin(
                        id = dto.id?.toString() ?: "",
                        solicitante = dto.solicitanteEmail,
                        cargo = dto.cargo,
                        equipo = nombreEquipo,
                        sala = dto.sala,
                        fecha = dto.fechaReserva,
                        horaInicio = "Reserva",
                        horaDevolucion = "Según horario",
                        estado = dto.estado,
                        motivoRechazo = dto.motivoRechazo ?: ""
                    )
                }
                uiState = uiState.copy(solicitudes = listaMapeada)
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Error al cargar solicitudes desde AWS: ${e.message}")
            }
        }
    }

    fun onMotivoRechazoChanged(motivo: String) {
        uiState = uiState.copy(
            motivoRechazo = motivo,
            motivoRechazoError = null
        )
    }

    fun onCancelarRechazo() {
        uiState = uiState.copy(
            motivoRechazo = "",
            motivoRechazoError = null
        )
    }

    private fun validateMotivoRechazo(motivo: String): String? {
        return if (motivo.isBlank()) {
            "La justificación del rechazo es obligatoria."
        } else {
            null
        }
    }

    private fun aplicarCambioLocal(
        id: String,
        estadoActual: String,
        estadoNuevo: String,
        motivo: String = ""
    ) {
        uiState = uiState.copy(
            solicitudes = uiState.solicitudes.map { solicitud ->
                if (solicitud.id == id && solicitud.estado == estadoActual) {
                    solicitud.copy(estado = estadoNuevo, motivoRechazo = motivo)
                } else {
                    solicitud
                }
            }
        )
    }

    fun onAceptarClick(id: String) {
        val idLong = id.toLongOrNull() ?: return
        viewModelScope.launch {
            try {
                repository?.cambiarEstadoSolicitudRemota(idLong, EstadoSolicitud.ACEPTADA)
                aplicarCambioLocal(id, EstadoSolicitud.PENDIENTE, EstadoSolicitud.ACEPTADA)
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Error al aceptar solicitud $id: ${e.message}")
            }
        }
    }

    fun onRechazarClick(id: String, onSuccess: () -> Unit) {
        val error = validateMotivoRechazo(uiState.motivoRechazo)
        if (error != null) {
            uiState = uiState.copy(motivoRechazoError = error)
            return
        }

        val idLong = id.toLongOrNull() ?: return
        val motivoConfirmado = uiState.motivoRechazo

        viewModelScope.launch {
            try {
                repository?.rechazarSolicitudRemota(idLong, motivoConfirmado)
                aplicarCambioLocal(
                    id,
                    EstadoSolicitud.PENDIENTE,
                    EstadoSolicitud.RECHAZADA,
                    motivoConfirmado
                )
                uiState = uiState.copy(
                    motivoRechazo = "",
                    motivoRechazoError = null
                )
                onSuccess()
            } catch (e: Exception) {
                uiState = uiState.copy(
                    motivoRechazoError = "Error de red al rechazar: ${e.message}"
                )
            }
        }
    }

    fun onEntregarClick(id: String) {
        val idLong = id.toLongOrNull() ?: return
        viewModelScope.launch {
            try {
                repository?.cambiarEstadoSolicitudRemota(idLong, EstadoSolicitud.ENTREGADA)
                aplicarCambioLocal(id, EstadoSolicitud.ACEPTADA, EstadoSolicitud.ENTREGADA)
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Error al marcar entregada $id: ${e.message}")
            }
        }
    }

    fun onDevolverClick(id: String) {
        val idLong = id.toLongOrNull() ?: return
        viewModelScope.launch {
            try {
                repository?.cambiarEstadoSolicitudRemota(idLong, EstadoSolicitud.DEVUELTA)
                aplicarCambioLocal(id, EstadoSolicitud.ENTREGADA, EstadoSolicitud.DEVUELTA)
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Error al marcar devuelta $id: ${e.message}")
            }
        }
    }
}