package com.proyecto.appmpazpaniol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.appmpazpaniol.data.PaniolRepository
import com.proyecto.appmpazpaniol.data.SolicitudEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ValidacionState {
    object Idle : ValidacionState()
    object Exito : ValidacionState()
    data class Error(val mensaje: String) : ValidacionState()
}

class SolicitanteViewModel(private val repository: PaniolRepository) : ViewModel() {

    private val _estadoValidacion = MutableStateFlow<ValidacionState>(ValidacionState.Idle)

    val estadoValidacion: StateFlow<ValidacionState> = _estadoValidacion.asStateFlow()

    /**
     * Valida las reglas de préstamo y guarda la solicitud de forma asíncrona.
     */
    fun crearSolicitudPrestamo(
        equipoId: String,
        nombreEquipo: String,
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
                val nuevaSolicitud = SolicitudEntity(
                    equipoId = equipoId,
                    nombreEquipo = nombreEquipo,
                    horasAnticipacion = horasAnticipacion,
                    motivo = motivo,
                    estado = "Pendiente"
                )
                repository.guardarSolicitud(nuevaSolicitud)
                _estadoValidacion.value = ValidacionState.Exito
            } catch (e: Exception) {
                _estadoValidacion.value = ValidacionState.Error("Error al guardar: ${e.message}")
            }
        }
    }

    fun limpiarEstadoValidacion() {
        _estadoValidacion.value = ValidacionState.Idle
    }
}