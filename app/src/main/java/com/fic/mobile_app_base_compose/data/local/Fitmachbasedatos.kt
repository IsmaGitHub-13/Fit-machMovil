package com.fic.mobile_app_base_compose.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fic.mobile_app_base_compose.data.model.*

@Database(
    entities = [
        Usuario::class,
        Rutina::class,
        Ejercicio::class,
        RutinaEjercicio::class,
        LogActividad::class,
        Publicacion::class,
        Amistad::class,
        PlanProgresion::class,
        SemanaProgresion::class,
        SolicitudAmistad::class,
    ],
    version = 5,
    exportSchema = false
)
abstract class FitmachBaseDatos : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun rutinaDao(): RutinaDao
    abstract fun ejercicioDao(): EjercicioDao
    abstract fun rutinaEjercicioDao(): RutinaEjercicioDao
    abstract fun logActividadDao(): LogActividadDao
    abstract fun publicacionDao(): PublicacionDao
    abstract fun amistadDao(): AmistadDao
    abstract fun planProgresionDao(): PlanProgresionDao
    abstract fun solicitudAmistadDao(): SolicitudAmistadDao

    companion object {
        @Volatile private var INSTANCIA: FitmachBaseDatos? = null

        fun obtenerInstancia(contexto: Context): FitmachBaseDatos {
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    contexto.applicationContext,
                    FitmachBaseDatos::class.java,
                    "fitmach.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCIA = instancia
                instancia
            }
        }
    }
}