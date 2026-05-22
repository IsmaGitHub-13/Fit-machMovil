package com.fic.mobile_app_base_compose.data.repository

import com.fic.mobile_app_base_compose.data.local.UsuarioDao
import com.fic.mobile_app_base_compose.data.model.Usuario
import kotlinx.coroutines.flow.Flow

/**
 * UsuarioRepository: Capa intermedia entre el ViewModel y el DAO.
 * El ViewModel nunca habla directamente con la base de datos,
 * siempre lo hace a través del Repository.
 *
 * @param usuarioDao El objeto de acceso a datos inyectado desde la base de datos.
 */
class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    /**
     * Intenta registrar un nuevo usuario en la base de datos.
     * Antes de insertar, valida que el correo y nombre de usuario no estén tomados.
     *
     * @return Result.success si se registró correctamente,
     *         Result.failure con el mensaje de error si algo falló.
     */
    suspend fun registrarUsuario(usuario: Usuario): Result<Unit> {
        return try {
            // Validar que el correo no esté en uso
            if (usuarioDao.existeCorreo(usuario.correoElectronico) > 0) {
                return Result.failure(Exception("Este correo ya está registrado"))
            }
            // Validar que el nombre de usuario no esté en uso
            if (usuarioDao.existeNombreUsuario(usuario.nombreUsuarioLogin) > 0) {
                return Result.failure(Exception("Este nombre de usuario ya está en uso"))
            }
            usuarioDao.registrarUsuario(usuario)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al registrar usuario: ${e.message}"))
        }
    }

    /**
     * Verifica las credenciales del usuario al iniciar sesión.
     * Acepta tanto nombre de usuario como correo electrónico.
     *
     * @param identificador Nombre de usuario o correo electrónico.
     * @param passwordHash  Hash de la contraseña ingresada.
     * @return Result.success con el Usuario si las credenciales son correctas,
     *         Result.failure si no se encontró o la contraseña no coincide.
     */
    suspend fun iniciarSesion(identificador: String, passwordHash: String): Result<Usuario> {
        return try {
            val usuario = usuarioDao.buscarPorIdentificador(identificador)
                ?: return Result.failure(Exception("Usuario no encontrado"))

            if (usuario.passwordHash != passwordHash) {
                return Result.failure(Exception("Contraseña incorrecta"))
            }

            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(Exception("Error al iniciar sesión: ${e.message}"))
        }
    }

    /**
     * Obtiene todos los usuarios registrados como un Flow reactivo.
     * Solo debe usarse con rol "admin".
     */
    fun obtenerTodosLosUsuarios(): Flow<List<Usuario>> {
        return usuarioDao.obtenerTodosLosUsuarios()
    }
}