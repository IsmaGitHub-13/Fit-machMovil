package com.fic.mobile_app_base_compose.data.repository

import com.fic.mobile_app_base_compose.data.local.RutinaEjercicioDao
import com.fic.mobile_app_base_compose.data.model.RutinaEjercicio
import kotlinx.coroutines.flow.Flow

class RutinaEjercicioRepository(private val dao: RutinaEjercicioDao) {

    fun obtenerEjerciciosDeLaRutina(idRutina: Int): Flow<List<RutinaEjercicio>> =
        dao.obtenerEjerciciosDeLaRutina(idRutina)

    suspend fun agregarEjercicio(rutinaEjercicio: RutinaEjercicio): Result<Unit> {
        return try {
            dao.agregarEjercicio(rutinaEjercicio)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarEjercicio(id: Int) = dao.eliminarEjercicio(id)

    suspend fun contarEjercicios(idRutina: Int): Int = dao.contarEjerciciosDeLaRutina(idRutina)
}