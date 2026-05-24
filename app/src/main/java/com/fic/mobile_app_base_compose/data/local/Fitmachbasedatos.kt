package com.fic.mobile_app_base_compose.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fic.mobile_app_base_compose.data.model.Ejercicio
import com.fic.mobile_app_base_compose.data.model.LogActividad
import com.fic.mobile_app_base_compose.data.model.Rutina
import com.fic.mobile_app_base_compose.data.model.RutinaEjercicio
import com.fic.mobile_app_base_compose.data.model.Usuario

@Database(
    entities = [
        Usuario::class,
        Rutina::class,
        Ejercicio::class,
        RutinaEjercicio::class,
        LogActividad::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class FitmachBaseDatos : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun rutinaDao(): RutinaDao
    abstract fun ejercicioDao(): EjercicioDao
    abstract fun logActividadDao(): LogActividadDao

    companion object {

        @Volatile
        private var INSTANCIA: FitmachBaseDatos? = null

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