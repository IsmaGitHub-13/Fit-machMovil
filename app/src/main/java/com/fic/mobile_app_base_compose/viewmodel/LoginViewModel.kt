package com.fic.mobile_app_base_compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fic.mobile_app_base_compose.SesionUsuario
import com.fic.mobile_app_base_compose.data.model.Usuario
import com.fic.mobile_app_base_compose.data.repository.FirebaseRepository
import com.fic.mobile_app_base_compose.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

sealed class LoginUiState {
    object Inactivo : LoginUiState()
    object Cargando : LoginUiState()
    data class Exito(val usuario: Usuario) : LoginUiState()
    data class Error(val mensaje: String) : LoginUiState()
}

class LoginViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Inactivo)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val firebaseRepository = FirebaseRepository()

    fun iniciarSesion(identificador: String, contrasena: String) {
        if (identificador.isBlank()) { _uiState.value = LoginUiState.Error("Escribe tu usuario o correo"); return }
        if (contrasena.isBlank()) { _uiState.value = LoginUiState.Error("Escribe tu contraseña"); return }
        if (contrasena.length < 6) { _uiState.value = LoginUiState.Error("La contraseña debe tener al menos 6 caracteres"); return }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Cargando
            val resultado = repository.iniciarSesion(
                identificador = identificador.trim(),
                passwordHash = hashearContrasena(contrasena)
            )
            _uiState.value = resultado.fold(
                onSuccess = { usuario ->
                    SesionUsuario.iniciar(
                        id = usuario.idUsuario,
                        nombreUsuarioLogin = usuario.nombreUsuarioLogin,
                        nombreCompleto = "${usuario.nombre} ${usuario.apellidoPaterno}"
                    )
                    // Sincronizar con Firebase
                    firebaseRepository.registrarUsuario(
                        nombreUsuario = usuario.nombreUsuarioLogin,
                        correo = usuario.correoElectronico
                    )
                    LoginUiState.Exito(usuario)
                },
                onFailure = { LoginUiState.Error(it.message ?: "Error desconocido") }
            )
        }
    }

    fun registrarUsuario(nombre: String, apellidoPaterno: String, correo: String,
                         nombreUsuario: String, contrasena: String, confirmarContrasena: String) {
        if (nombre.isBlank() || apellidoPaterno.isBlank()) { _uiState.value = LoginUiState.Error("El nombre y apellido son obligatorios"); return }
        if (!correo.contains("@") || !correo.contains(".")) { _uiState.value = LoginUiState.Error("Escribe un correo electrónico válido"); return }
        if (nombreUsuario.length < 4) { _uiState.value = LoginUiState.Error("El nombre de usuario debe tener al menos 4 caracteres"); return }
        if (contrasena.length < 6) { _uiState.value = LoginUiState.Error("La contraseña debe tener al menos 6 caracteres"); return }
        if (contrasena != confirmarContrasena) { _uiState.value = LoginUiState.Error("Las contraseñas no coinciden"); return }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Cargando
            val nuevoUsuario = Usuario(
                nombre = nombre.trim(),
                apellidoPaterno = apellidoPaterno.trim(),
                correoElectronico = correo.trim().lowercase(),
                nombreUsuarioLogin = nombreUsuario.trim().lowercase(),
                passwordHash = hashearContrasena(contrasena),
                rol = "usuario_regular"
            )
            val resultado = repository.registrarUsuario(nuevoUsuario)
            _uiState.value = resultado.fold(
                onSuccess = { idGenerado ->
                    SesionUsuario.iniciar(
                        id = idGenerado,
                        nombreUsuarioLogin = nuevoUsuario.nombreUsuarioLogin,
                        nombreCompleto = "${nuevoUsuario.nombre} ${nuevoUsuario.apellidoPaterno}"
                    )
                    firebaseRepository.registrarUsuario(
                        nombreUsuario = nuevoUsuario.nombreUsuarioLogin,
                        correo = nuevoUsuario.correoElectronico
                    )
                    LoginUiState.Exito(nuevoUsuario.copy(idUsuario = idGenerado))
                },
                onFailure = { LoginUiState.Error(it.message ?: "Error al registrar") }
            )
        }
    }

    fun resetearEstado() { _uiState.value = LoginUiState.Inactivo }

    private fun hashearContrasena(contrasena: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(contrasena.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    class Factory(private val repository: UsuarioRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
    }
}