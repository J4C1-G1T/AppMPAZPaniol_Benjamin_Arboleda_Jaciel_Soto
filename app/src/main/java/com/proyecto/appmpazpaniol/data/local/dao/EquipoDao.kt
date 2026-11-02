package com.proyecto.appmpazpaniol.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.proyecto.appmpazpaniol.data.local.entity.EquipoEntity

@Dao
interface EquipoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipo(equipo: EquipoEntity)

    @Query("SELECT * FROM equipos")
    suspend fun getAllEquipos(): List<EquipoEntity>

    @Query("SELECT * FROM equipos WHERE id = :id LIMIT 1")
    suspend fun getEquipoById(id: String): EquipoEntity?

    @Query("UPDATE equipos SET estado = :estado WHERE id = :id")
    suspend fun updateEstado(id: String, estado: String)

    @Query("UPDATE equipos SET stockDisponible = :stockDisponible WHERE id = :id")
    suspend fun updateStockDisponible(id: String, stockDisponible: Int)
}
