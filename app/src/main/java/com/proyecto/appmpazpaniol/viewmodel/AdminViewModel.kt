package com.proyecto.appmpazpaniol.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.proyecto.appmpazpaniol.model.AdminModel
import com.proyecto.appmpazpaniol.model.EstadoSolicitud
import com.proyecto.appmpazpaniol.model.SolicitudAdmin

/**
 * Gestiona en memoria las solicitudes y sus cambios de estado.
 */
class AdminViewModel : ViewModel() {
    // pendiente: reemplazar por datos reales de Room
    var uiState by mutableStateOf(
        AdminModel(
            solicitudes = listOf(
                SolicitudAdmin(
                    "1",
                    "Profesor Prueba 1",
                    "Docente",
                    "Proyector Epson",
                    "Sala 101",
                    "20/10/2026",
                    "09:00",
                    "12:00",
                    EstadoSolicitud.PENDIENTE
                ),
                SolicitudAdmin(
                    "2",
                    "Profesor Prueba 2",
                    "Docente",
                    "Notebook Lenovo",
                    "Sala 204",
                    "20/10/2026",
                    "10:00",
                    "13:00",
                    EstadoSolicitud.ACEPTADA
                ),
                SolicitudAdmin(
                    "3",
                    "Profesor Prueba 3",
                    "Docente",
                    "Cámara Canon",
                    "Sala 305",
                    "21/10/2026",
                    "08:30",
                    "11:30",
                    EstadoSolicitud.RECHAZADA,
                    motivoRechazo = "Equipo no disponible en ese horario."
                ),
                SolicitudAdmin(
                    "4",
                    "Profesor Prueba 4",
                    "Docente",
                    "Micrófono inalámbrico",
                    "Sala 112",
                    "21/10/2026",
                    "14:00",
                    "16:00",
                    EstadoSolicitud.ENTREGADA
                ),
                SolicitudAdmin(
                    "5",
                    "Profesor Prueba 5",
                    "Docente",
                    "Parlante portátil",
                    "Sala 208",
                    "22/10/2026",
                    "09:30",
                    "12:30",
                    EstadoSolicitud.DEVUELTA
                )
            )
        )
    )
        private set

    /**
     * Actualiza la justificación del rechazo y limpia su error.
     */
    fun onMotivoRechazoChanged(motivo: String) {
        uiState = uiState.copy(
            motivoRechazo = motivo,
            motivoRechazoError = null
        )
    }

    /**
     * Limpia el motivo de rechazo y su error.
     */
    fun onCancelarRechazo() {
        uiState = uiState.copy(
            motivoRechazo = "",
            motivoRechazoError = null
        )
    }

    private fun validateMotivoRechazo(motivo: String): String? {
        if (motivo.isBlank()) {
            return "La justificación del rechazo es obligatoria."
        } else {
            return null
        }
    }

    private fun cambiarEstado(
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

    /**
     * Acepta una solicitud pendiente.
     */
    fun onAceptarClick(id: String) {
        // pendiente: descontar stock al aceptar y recuperarlo al devolver (requiere Room/PaniolRepository)
        cambiarEstado(id, EstadoSolicitud.PENDIENTE, EstadoSolicitud.ACEPTADA)
    }

    /**
     * Rechaza una solicitud pendiente si tiene una justificación válida.
     */
    fun onRechazarClick(id: String, onSuccess: () -> Unit) {
        val error = validateMotivoRechazo(uiState.motivoRechazo)
        if (error != null) {
            uiState = uiState.copy(motivoRechazoError = error)
            return
        }

        cambiarEstado(
            id,
            EstadoSolicitud.PENDIENTE,
            EstadoSolicitud.RECHAZADA,
            uiState.motivoRechazo
        )
        uiState = uiState.copy(
            motivoRechazo = "",
            motivoRechazoError = null
        )
        onSuccess()
    }

    /**
     * Marca como entregada una solicitud aceptada.
     */
    fun onEntregarClick(id: String) {
        // pendiente: guardar fecha y hora de entrega y devolución.
        cambiarEstado(id, EstadoSolicitud.ACEPTADA, EstadoSolicitud.ENTREGADA)
    }

    /**
     * Marca como devuelta una solicitud entregada.
     */
    fun onDevolverClick(id: String) {
        // pendiente: descontar stock al aceptar y recuperarlo al devolver (requiere Room/PaniolRepository)
        cambiarEstado(id, EstadoSolicitud.ENTREGADA, EstadoSolicitud.DEVUELTA)
    }
}
