package com.proyecto.appmpazpaniol.model

/**
 * Representa un recurso tecnológico o dispositivo de red en el inventario del Pañol.
 * Utiliza 'data class' en Kotlin para proveer automáticamente métodos como equals(), hashCode() y copy().
 */
data class Equipo(
    val id: String,                  // Identificador único (UUID o código interno)
    val nombre: String,              // Nombre descriptivo (ej. "Proyector Epson EB-X06")
    val marca: String,               // Marca del fabricante
    val modelo: String,              // Modelo técnico
    val categoria: String,           // Categoría: Proyector, Notebook, Robótica, Red, etc.
    val stockTotal: Int,             // Cantidad total física registrada en la escuela
    val stockDisponible: Int,        // Cantidad disponible en tiempo real para préstamo
    val estado: String,              // Estado: Disponible, Reservado, Prestado, En Mantenimiento
    val ubicacionFisica: String      // Ubicación física de resguardo en el pañol o estante
)