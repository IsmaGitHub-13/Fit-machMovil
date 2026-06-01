package com.fic.mobile_app_base_compose.data.repository

import com.fic.mobile_app_base_compose.data.local.UsuarioDao
import com.fic.mobile_app_base_compose.data.model.Usuario
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun registrarUsuario(usuario: Usuario): Result<Unit> {
        return try {
            if (usuarioDao.existeCorreo(usuario.correoElectronico) > 0) {
                return Result.failure(Exception("Este correo ya está registrado"))
            }
            if (usuarioDao.existeNombreUsuario(usuario.nombreUsuarioLogin) > 0) {
                return Result.failure(Exception("Este nombre de usuario ya está en uso"))
            }
            usuarioDao.registrarUsuario(usuario)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al registrar usuario: ${e.message}"))
        }
    }

    suspend fun iniciarSesion(identificador: String, passwordHash: String): Result<Usuario> {
        return try {
            // Se convierte a lowercase para coincidir con como se guardó en el registro
            val identificadorNormalizado = identificador.trim().lowercase()

            val usuario = usuarioDao.buscarPorIdentificador(identificadorNormalizado)
                ?: return Result.failure(Exception("Usuario no encontrado"))

            if (usuario.passwordHash != passwordHash) {
                return Result.failure(Exception("Contraseña incorrecta"))
            }

            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(Exception("Error al iniciar sesión: ${e.message}"))
        }
    }

    fun obtenerTodosLosUsuarios(): Flow<List<Usuario>> {
        return usuarioDao.obtenerTodosLosUsuarios()
    }
}