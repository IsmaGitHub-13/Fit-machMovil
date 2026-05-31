package com.fic.mobile_app_base_compose.data.repository

import com.fic.mobile_app_base_compose.data.local.PublicacionDao
import com.fic.mobile_app_base_compose.data.model.Publicacion
import kotlinx.coroutines.flow.Flow

class PublicacionRepository(private val publicacionDao: PublicacionDao) {

    fun obtenerFeed(idsAmigos: List<Int>): Flow<List<Publicacion>> =
        publicacionDao.obtenerFeedDeAmigos(idsAmigos)

    fun obtenerPublicacionesDeUsuario(idUsuario: Int): Flow<List<Publicacion>> =
        publicacionDao.obtenerPublicacionesDeUsuario(idUsuario)

    suspend fun publicar(publicacion: Publicacion) =
        publicacionDao.publicar(publicacion)

    suspend fun darLike(idPublicacion: Int) =
        publicacionDao.darLike(idPublicacion)

    suspend fun eliminar(idPublicacion: Int) =
        publicacionDao.eliminarPublicacion(idPublicacion)
}