package com.proyecto.appmpazpaniol.model

data class Equipo(
    val id: String,
    val nombre: String,
    val marca: String,
    val modelo: String,
    val categoria: String,
    val stockTotal: Int,
    val stockDisponible: Int,
    val estado: String,
    val ubicacionFisica: String
)