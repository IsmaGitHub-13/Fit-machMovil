package com.fic.mobile_app_base_compose.data.repository

import com.fic.mobile_app_base_compose.data.local.PublicacionDao
import com.fic.mobile_app_base_compose.data.model.Publicacion
import kotlinx.coroutines.flow.Flow

class PublicacionRepository(private val dao: PublicacionDao) {

    fun obtenerFeed(idsUsuarios: List<Int>): Flow<List<Publicacion>> =
        dao.obtenerFeed(idsUsuarios)

    fun obtenerPublicacionesDeUsuario(idUsuario: Int): Flow<List<Publicacion>> =
        dao.obtenerPublicacionesDeUsuario(idUsuario)

    suspend fun publicar(publicacion: Publicacion): Result<Unit> {
        return try {
            dao.publicar(publicacion)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun darLike(idPublicacion: Int) = dao.darLike(idPublicacion)
    suspend fun quitarLike(idPublicacion: Int) = dao.quitarLike(idPublicacion)
    suspend fun eliminar(idPublicacion: Int) = dao.eliminarPublicacion(idPublicacion)
}