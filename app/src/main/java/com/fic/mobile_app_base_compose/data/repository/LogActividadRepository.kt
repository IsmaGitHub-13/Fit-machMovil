package com.fic.mobile_app_base_compose.data.repository

import com.fic.mobile_app_base_compose.data.local.LogActividadDao
import com.fic.mobile_app_base_compose.data.model.LogActividad
import kotlinx.coroutines.flow.Flow

class LogActividadRepository(private val logActividadDao: LogActividadDao) {

    fun obtenerHistorial(idUsuario: Int): Flow<List<LogActividad>> {
        return logActividadDao.obtenerHistorialPorUsuario(idUsuario)
    }

    suspend fun registrarActividad(log: LogActividad): Result<Unit> {
        return try {
            logActividadDao.registrarActividad(log)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun contarActividades(idUsuario: Int): Int {
        return logActividadDao.contarActividadesPorUsuario(idUsuario)
    }

    suspend fun eliminarActividad(idLog: Int) {
        logActividadDao.eliminarActividad(idLog)
    }
}
