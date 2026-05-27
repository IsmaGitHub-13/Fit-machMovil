package com.fic.mobile_app_base_compose.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fic.mobile_app_base_compose.data.model.LogActividad
import kotlinx.coroutines.flow.Flow

@Dao
interface LogActividadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registrarActividad(log: LogActividad)

    @Query("SELECT * FROM log_actividad WHERE id_usuario = :idUsuario ORDER BY fecha DESC")
    fun obtenerHistorialPorUsuario(idUsuario: Int): Flow<List<LogActividad>>

    @Query("SELECT * FROM log_actividad WHERE id_usuario = :idUsuario ORDER BY fecha DESC LIMIT 1")
    suspend fun obtenerUltimaActividad(idUsuario: Int): LogActividad?

    @Query("SELECT COUNT(*) FROM log_actividad WHERE id_usuario = :idUsuario")
    suspend fun contarActividadesPorUsuario(idUsuario: Int): Int

    @Query("DELETE FROM log_actividad WHERE id_log = :idLog")
    suspend fun eliminarActividad(idLog: Int)
}
