package com.proyecto.appmpazpaniol.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.proyecto.appmpazpaniol.data.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {
    // Registra un usuario y falla si ya existe el identificador.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerUser(user: UsuarioEntity)

    // Busca un usuario por su correo electrónico.
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UsuarioEntity?
}
