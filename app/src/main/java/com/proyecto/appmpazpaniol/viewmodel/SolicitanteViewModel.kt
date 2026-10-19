package com.proyecto.appmpazpaniol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.appmpazpaniol.data.PaniolRepository
import com.proyecto.appmpazpaniol.data.SolicitudEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Representa los distintos estados de la validación al intentar crear una solicitud.
 * Utiliza una Sealed Class para garantizar un conjunto cerrado de estados (Patrón MVVM).
 */
sealed class ValidacionState {
    object Idle : ValidacionState()
    object Exito : ValidacionState()
    data class Error(val mensaje: String) : ValidacionState()
}

/**
 * ViewModel para el rol de Solicitante.
 * Administra la lógica de presentación y las reglas de negocio de solicitudes.
 */
class SolicitanteViewModel(private val repository: PaniolRepository) : ViewModel() {

    // Estado reactivo interno (Mutable)
    private val _estadoValidacion = MutableStateFlow<ValidacionState>(ValidacionState.Idle)

    // Estado reactivo público e inmutable expuesto a las vistas (Compose)
    val estadoValidacion: StateFlow<ValidacionState> = _estadoValidacion.asStateFlow()

    /**
     * Aplica las Reglas de Negocio requeridas antes de persistir una solicitud en la Base de Datos.
     *
     * @param equipoId ID del equipo seleccionado.
     * @param nombreEquipo Nombre descriptivo del equipo.
     * @param horasAnticipacion Anticipación con la que se pide el préstamo (Regla: mínimo 3 hrs).
     * @param motivo Explicación del uso del equipo.
     */
    fun crearSolicitudPrestamo(
        equipoId: String,
        nombreEquipo: String,
        horasAnticipacion: Int,
        motivo: String
    ) {
        // Regla 1: El motivo no puede estar vacío
        if (motivo.isBlank()) {
            _estadoValidacion.value = ValidacionState.Error("Debe ingresar un motivo para el préstamo.")
            return
        }

        // Regla de Negocio Principal (Tarjeta Trello): Anticipación mínima de 3 horas
        if (horasAnticipacion < 3) {
            _estadoValidacion.value = ValidacionState.Error("La solicitud debe realizarse con al menos 3 horas de anticipación.")
            return
        }

        // Si supera las validaciones, se procesa de forma asíncrona mediante Corutinas en Room
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

    /**
     * Reinicia el estado de validación tras ser consumido por la vista.
     */
    fun limpiarEstadoValidacion() {
        _estadoValidacion.value = ValidacionState.Idle
    }
}