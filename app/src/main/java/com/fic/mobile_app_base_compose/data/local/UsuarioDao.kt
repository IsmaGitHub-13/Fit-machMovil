package com.fic.mobile_app_base_compose.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fic.mobile_app_base_compose.data.model.Usuario
import kotlinx.coroutines.flow.Flow

/**
 * @Dao: Objeto de Acceso a Datos para la tabla "usuarios".
 * Define todas las operaciones que se pueden hacer con los usuarios en la BD.
 */
@Dao
interface UsuarioDao {

    /**
     * Registra un nuevo usuario en la base de datos.
     * OnConflictStrategy.ABORT lanza un error si el correo o usuario ya existe
     * (gracias a los índices únicos definidos en la entidad).
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registrarUsuario(usuario: Usuario)

    /**
     * Busca un usuario por su nombre de login O por su correo electrónico.
     * Usado en el inicio de sesión para soportar ambos métodos de entrada.
     */
    @Query("""
        SELECT * FROM usuarios 
        WHERE nombre_usuario_login = :identificador 
        OR correo_electronico = :identificador 
        LIMIT 1
    """)
    suspend fun buscarPorIdentificador(identificador: String): Usuario?

    /**
     * Verifica si ya existe un usuario con ese correo electrónico.
     * Usado en el registro para validar duplicados antes de intentar insertar.
     */
    @Query("SELECT COUNT(*) FROM usuarios WHERE correo_electronico = :correo")
    suspend fun existeCorreo(correo: String): Int

    /**
     * Verifica si ya existe un usuario con ese nombre de login.
     * Usado en el registro para validar duplicados antes de intentar insertar.
     */
    @Query("SELECT COUNT(*) FROM usuarios WHERE nombre_usuario_login = :nombreUsuario")
    suspend fun existeNombreUsuario(nombreUsuario: String): Int

    /**
     * Obtiene todos los usuarios registrados.
     * Retorna un Flow para que la UI se actualice automáticamente ante cambios.
     * Solo accesible para el rol "admin".
     */
    @Query("SELECT * FROM usuarios ORDER BY fecha_registro DESC")
    fun obtenerTodosLosUsuarios(): Flow<List<Usuario>>

    /**
     * Obtiene un usuario por su ID.
     */
    @Query("SELECT * FROM usuarios WHERE id_usuario = :id LIMIT 1")
    suspend fun obtenerUsuarioPorId(id: Int): Usuario?
}