package com.fic.mobile_app_base_compose.data.repository

import com.fic.mobile_app_base_compose.data.local.RutinaDao
import com.fic.mobile_app_base_compose.data.model.Rutina
import kotlinx.coroutines.flow.Flow

/**
 * RutinaRepository: Capa intermedia entre el ViewModel y el DAO.
 * El ViewModel nunca habla directamente con la base de datos.
 */
class RutinaRepository(private val rutinaDao: RutinaDao) {

    // Devuelve el Flow de rutinas del usuario (se actualiza solo al haber cambios)
    fun obtenerRutinas(idUsuario: Int): Flow<List<Rutina>> {
        return rutinaDao.obtenerPorUsuario(idUsuario)
    }

    // Guarda una rutina nueva
    suspend fun guardarRutina(rutina: Rutina): Result<Unit> {
        return try {
            if (rutina.nombre.isBlank()) {
                Result.failure(Exception("El nombre de la rutina no puede estar vacío"))
            } else {
                rutinaDao.insertar(rutina)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error al guardar la rutina: ${e.message}"))
        }
    }

    // Actualiza una rutina existente
    suspend fun actualizarRutina(rutina: Rutina): Result<Unit> {
        return try {
            rutinaDao.actualizar(rutina)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al actualizar: ${e.message}"))
        }
    }

    // Elimina una rutina
    suspend fun eliminarRutina(rutina: Rutina): Result<Unit> {
        return try {
            rutinaDao.eliminar(rutina)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al eliminar: ${e.message}"))
        }
    }
}