package com.fic.mobile_app_base_compose.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fic.mobile_app_base_compose.data.model.RutinaEjercicio
import kotlinx.coroutines.flow.Flow

@Dao
interface RutinaEjercicioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarEjercicio(rutinaEjercicio: RutinaEjercicio)

    @Query("SELECT * FROM rutina_ejercicio WHERE id_rutina = :idRutina ORDER BY orden ASC")
    fun obtenerEjerciciosDeLaRutina(idRutina: Int): Flow<List<RutinaEjercicio>>

    @Query("DELETE FROM rutina_ejercicio WHERE id = :id")
    suspend fun eliminarEjercicio(id: Int)

    @Query("DELETE FROM rutina_ejercicio WHERE id_rutina = :idRutina")
    suspend fun eliminarEjerciciosDeLaRutina(idRutina: Int)

    @Query("SELECT COUNT(*) FROM rutina_ejercicio WHERE id_rutina = :idRutina")
    suspend fun contarEjerciciosDeLaRutina(idRutina: Int): Int
}