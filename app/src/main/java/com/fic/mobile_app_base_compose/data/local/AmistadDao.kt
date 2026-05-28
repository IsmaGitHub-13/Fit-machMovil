package com.fic.mobile_app_base_compose.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fic.mobile_app_base_compose.data.model.Amistad
import kotlinx.coroutines.flow.Flow

@Dao
interface AmistadDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun agregarAmigo(amistad: Amistad)

    @Query("SELECT id_amigo FROM amistades WHERE id_usuario = :idUsuario")
    fun obtenerIdsAmigos(idUsuario: Int): Flow<List<Int>>

    @Query("SELECT * FROM amistades WHERE id_usuario = :idUsuario")
    fun obtenerAmistades(idUsuario: Int): Flow<List<Amistad>>

    @Query("DELETE FROM amistades WHERE id_usuario = :idUsuario AND id_amigo = :idAmigo")
    suspend fun eliminarAmigo(idUsuario: Int, idAmigo: Int)

    @Query("SELECT COUNT(*) FROM amistades WHERE id_usuario = :idUsuario AND id_amigo = :idAmigo")
    suspend fun sonAmigos(idUsuario: Int, idAmigo: Int): Int
}