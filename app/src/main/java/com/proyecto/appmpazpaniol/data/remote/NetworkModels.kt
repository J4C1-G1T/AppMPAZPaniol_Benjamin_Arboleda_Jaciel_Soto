package com.proyecto.appmpazpaniol.data.remote



// 1. Representa el JSON que devuelve :8082/api/equipos
data class EquipoDto(
    val id: String,
    val nombre: String,
    val marca: String,
    val modelo: String,
    val categoria: String
)

// 2. Representa el JSON que devuelve :8081/api/inventario
data class InventarioDto(
    val equipoId: String,
    val stockTotal: Int,
    val stockDisponible: Int,
    val estado: String,
    val ubicacionFisica: String
)

// 3. Representa el JSON que envía y recibe :8080/api/solicitudes
data class SolicitudDto(
    val id: Long? = null, // Nullable porque al crear (POST) PostgreSQL lo autogenera
    val equipoId: String,
    val solicitanteEmail: String,
    val cargo: String,
    val sala: String,
    val fechaReserva: String,
    val estado: String = "PENDIENTE",
    val motivoRechazo: String? = null
)