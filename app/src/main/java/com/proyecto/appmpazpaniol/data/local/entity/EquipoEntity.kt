package com.proyecto.appmpazpaniol.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.proyecto.appmpazpaniol.model.Equipo

@Entity(tableName = "equipos")
data class EquipoEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val marca: String,
    val modelo: String,
    val categoria: String,
    val stockTotal: Int,
    val stockDisponible: Int,
    val estado: String,
    val ubicacionFisica: String
) {
    /**
     * Convierte esta entidad persistida al modelo de equipo de la aplicación.
     */
    fun toEquipo(): Equipo {
        return Equipo(
            id = id,
            nombre = nombre,
            marca = marca,
            modelo = modelo,
            categoria = categoria,
            stockTotal = stockTotal,
            stockDisponible = stockDisponible,
            estado = estado,
            ubicacionFisica = ubicacionFisica
        )
    }
}
