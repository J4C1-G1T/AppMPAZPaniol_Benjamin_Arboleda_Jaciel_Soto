package com.proyecto.appmpazpaniol.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.proyecto.appmpazpaniol.data.local.entity.EquipoEntity

@Dao
interface EquipoDao {
    // Inserta o reemplaza el equipo con el mismo código.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipo(equipo: EquipoEntity)

    // Obtiene todos los equipos guardados.
    @Query("SELECT * FROM equipos")
    suspend fun getAllEquipos(): List<EquipoEntity>

    // Busca un equipo por su código.
    @Query("SELECT * FROM equipos WHERE id = :id LIMIT 1")
    suspend fun getEquipoById(id: String): EquipoEntity?

    // Actualiza el estado del equipo.
    @Query("UPDATE equipos SET estado = :estado WHERE id = :id")
    suspend fun updateEstado(id: String, estado: String)

    // Actualiza el stock disponible del equipo.
    @Query("UPDATE equipos SET stockDisponible = :stockDisponible WHERE id = :id")
    suspend fun updateStockDisponible(id: String, stockDisponible: Int)
}
