package com.proyecto.appmpazpaniol.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.proyecto.appmpazpaniol.data.local.dao.EquipoDao
import com.proyecto.appmpazpaniol.data.local.dao.UsuarioDao
import com.proyecto.appmpazpaniol.data.local.entity.EquipoEntity
import com.proyecto.appmpazpaniol.data.local.entity.UsuarioEntity

/**
 * Base de Datos Room de la aplicación MPAZ Pañol.
 */
// versión subida al agregar UsuarioEntity
@Database(
    entities = [SolicitudEntity::class, EquipoEntity::class, UsuarioEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun solicitudDao(): SolicitudDao

    abstract fun equipoDao(): EquipoDao

    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Retorna la instancia única Singleton de la base de datos.
         */
        fun obtenerBaseDatos(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mpaz_paniol_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}