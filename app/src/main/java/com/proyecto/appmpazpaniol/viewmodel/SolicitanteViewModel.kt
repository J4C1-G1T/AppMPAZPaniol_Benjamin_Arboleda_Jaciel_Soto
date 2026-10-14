package com.proyecto.appmpazpaniol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.appmpazpaniol.model.Equipo
import com.proyecto.appmpazpaniol.viewmodel.Solicitud
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Jerarquía de estados de la interfaz de usuario (UI State) utilizando Sealed Class.
 * Representa los estados posibles del flujo del Solicitante.
 */
sealed class SolicitanteUiState {
    data object Cargando : SolicitanteUiState()
    data class Exito(
        val equipos: List<Equipo>,
        val misSolicitudes: List<Solicitud>,
        val mensajeConfirmacion: String? = null
    ) : SolicitanteUiState()
    data class Error(val mensaje: String) : SolicitanteUiState()
}

/**
 * ViewModel que gestiona la lógica de presentación y el estado para el Rol Solicitante.
 * Sigue el patrón MVVM utilizando StateFlow y Corutinas.
 */
class SolicitanteViewModel : ViewModel() {

    // _uiState es privado y mutable para evitar modificaciones externas no deseadas
    private val _uiState = MutableStateFlow<SolicitanteUiState>(SolicitanteUiState.Cargando)

    // uiState es público e inmutable (StateFlow) para que la UI se suscriba a los cambios
    val uiState: StateFlow<SolicitanteUiState> = _uiState.asStateFlow()

    // Listas internas simuladas en memoria
    private val listaEquiposSimulados = mutableListOf(
        Equipo("1", "Proyector Epson EB-X06", "Epson", "EB-X06", "Proyector", 5, 3, "Disponible", "Estante A1"),
        Equipo("2", "Notebook Lenovo ThinkPad", "Lenovo", "ThinkPad E14", "Computación", 10, 0, "En Mantenimiento", "Estante B2"),
        Equipo("3", "Cámara Canon EOS Rebel T7", "Canon", "EOS Rebel T7", "Audiovisual", 4, 2, "Disponible", "Armario C")
    )

    private val listaSolicitudesSimuladas = mutableListOf(
        Solicitud("101", "1", "Proyector Epson EB-X06", 3, "Uso en asignatura Robótica", "Pendiente")
    )

    init {
        // Cargar los datos iniciales al instanciar el ViewModel
        cargarDatosIniciales()
    }

    /**
     * Simula la carga asíncrona de datos desde un repositorio usando Corutinas.
     */
    fun cargarDatosIniciales() {
        viewModelScope.launch {
            _uiState.value = SolicitanteUiState.Cargando
            delay(1000L) // Simula demora de red/base de datos (1 segundo)

            _uiState.value = SolicitanteUiState.Exito(
                equipos = listaEquiposSimulados.toList(),
                misSolicitudes = listaSolicitudesSimuladas.toList()
            )
        }
    }

    /**
     * Regla de Negocio: Procesa la creación de una solicitud validando la anticipación mínima (3 hrs).
     */
    fun crearSolicitudPréstamo(equipo: Equipo, horasAnticipacion: Int, motivo: String) {
        if (horasAnticipacion < 3) {
            _uiState.value = SolicitanteUiState.Error("Regla de negocio no cumplida: La reserva debe tener al menos 3 horas de anticipación.")
            return
        }

        viewModelScope.launch {
            _uiState.value = SolicitanteUiState.Cargando
            delay(800L) // Simula procesamiento de guardado

            val nuevaSolicitud = Solicitud(
                id = (listaSolicitudesSimuladas.size + 101).toString(),
                equipoId = equipo.id,
                nombreEquipo = equipo.nombre,
                horasAnticipacion = horasAnticipacion,
                motivo = motivo,
                estado = "Pendiente"
            )

            listaSolicitudesSimuladas.add(0, nuevaSolicitud) // Agregar al inicio de la lista

            _uiState.value = SolicitanteUiState.Exito(
                equipos = listaEquiposSimulados.toList(),
                misSolicitudes = listaSolicitudesSimuladas.toList(),
                mensajeConfirmacion = "¡Solicitud creada exitosamente para ${equipo.nombre}!"
            )
        }
    }
}