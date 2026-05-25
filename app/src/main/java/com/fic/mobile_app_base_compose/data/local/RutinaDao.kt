package com.fic.mobile_app_base_compose.data.local

import androidx.room.*
import com.fic.mobile_app_base_compose.data.model.Rutina
import kotlinx.coroutines.flow.Flow

@Dao
interface RutinaDao {

    // Inserta una rutina nueva. Si ya existe (mismo id), la reemplaza
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(rutina: Rutina)

    // Trae todas las rutinas de un usuario, se actualiza automáticamente con Flow
    @Query("SELECT * FROM rutinas WHERE id_creador = :idUsuario ORDER BY id_rutina DESC")
    fun obtenerPorUsuario(idUsuario: Int): Flow<List<Rutina>>

    // Trae una rutina específica por su ID
    @Query("SELECT * FROM rutinas WHERE id_rutina = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Rutina?

    // Actualiza los datos de una rutina existente
    @Update
    suspend fun actualizar(rutina: Rutina)

    // Elimina una rutina
    @Delete
    suspend fun eliminar(rutina: Rutina)
}