package com.fic.mobile_app_base_compose.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fic.mobile_app_base_compose.data.model.Usuario

/**
 * @Database: Define las tablas de la base de datos de FitMatch y su versión.
 * Cada vez que se modifique el esquema (agregar columnas, tablas, etc.)
 * se debe incrementar el número de versión.
 */
@Database(
    entities = [Usuario::class],
    version = 1,
    exportSchema = false
)
abstract class FitmachBaseDatos : RoomDatabase() {

    // Expone el DAO de usuarios para realizar operaciones en esa tabla
    abstract fun usuarioDao(): UsuarioDao

    companion object {

        // @Volatile asegura que la instancia sea visible por cualquier hilo del procesador
        @Volatile
        private var INSTANCIA: FitmachBaseDatos? = null

        /**
         * Patrón Singleton: Evita abrir el archivo de base de datos múltiples veces
         * de forma simultánea, lo cual corrompería los datos del dispositivo.
         *
         * @param contexto El contexto de la aplicación.
         * @return La única instancia de la base de datos.
         */
        fun obtenerInstancia(contexto: Context): FitmachBaseDatos {
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    contexto.applicationContext,
                    FitmachBaseDatos::class.java,
                    "fitmach.db"
                ).build()
                INSTANCIA = instancia
                instancia
            }
        }
    }
}