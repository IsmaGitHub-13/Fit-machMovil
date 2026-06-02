package com.fic.mobile_app_base_compose.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fic.mobile_app_base_compose.data.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registrarUsuario(usuario: Usuario)

    @Query("""
        SELECT * FROM usuarios 
        WHERE nombre_usuario_login = :identificador 
        OR correo_electronico = :identificador 
        LIMIT 1
    """)
    suspend fun buscarPorIdentificador(identificador: String): Usuario?

    @Query("SELECT COUNT(*) FROM usuarios WHERE correo_electronico = :correo")
    suspend fun existeCorreo(correo: String): Int

    @Query("SELECT COUNT(*) FROM usuarios WHERE nombre_usuario_login = :nombreUsuario")
    suspend fun existeNombreUsuario(nombreUsuario: String): Int

    @Query("SELECT * FROM usuarios ORDER BY fecha_registro DESC")
    fun obtenerTodosLosUsuarios(): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios WHERE id_usuario = :id LIMIT 1")
    suspend fun obtenerUsuarioPorId(id: Int): Usuario?

    // Búsqueda de usuarios para la función social — excluye al usuario actual
    @Query("""
        SELECT * FROM usuarios 
        WHERE (nombre LIKE :query OR apellido_paterno LIKE :query OR nombre_usuario_login LIKE :query)
        AND id_usuario != :miId
        ORDER BY nombre ASC
    """)
    fun buscarUsuarios(query: String, miId: Int): Flow<List<Usuario>>
}