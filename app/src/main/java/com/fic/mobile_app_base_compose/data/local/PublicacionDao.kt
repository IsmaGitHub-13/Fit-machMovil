package com.fic.mobile_app_base_compose.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fic.mobile_app_base_compose.data.model.Publicacion
import kotlinx.coroutines.flow.Flow

@Dao
interface PublicacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun publicar(publicacion: Publicacion)

    @Query("SELECT * FROM publicaciones WHERE id_usuario IN (:idsUsuarios) ORDER BY fecha DESC")
    fun obtenerFeed(idsUsuarios: List<Int>): Flow<List<Publicacion>>

    @Query("SELECT * FROM publicaciones WHERE id_usuario = :idUsuario ORDER BY fecha DESC")
    fun obtenerPublicacionesDeUsuario(idUsuario: Int): Flow<List<Publicacion>>

    @Query("UPDATE publicaciones SET likes = likes + 1 WHERE id_publicacion = :idPublicacion")
    suspend fun darLike(idPublicacion: Int)

    @Query("UPDATE publicaciones SET likes = likes - 1 WHERE id_publicacion = :idPublicacion AND likes > 0")
    suspend fun quitarLike(idPublicacion: Int)

    @Query("DELETE FROM publicaciones WHERE id_publicacion = :idPublicacion")
    suspend fun eliminarPublicacion(idPublicacion: Int)
}