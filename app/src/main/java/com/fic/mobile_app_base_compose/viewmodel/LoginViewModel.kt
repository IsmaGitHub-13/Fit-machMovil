package com.fic.mobile_app_base_compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fic.mobile_app_base_compose.data.model.Usuario
import com.fic.mobile_app_base_compose.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

/**
 * Estado de la UI para la pantalla de Login y Registro.
 * Sealed class permite manejar todos los posibles estados de forma segura.
 */
sealed class LoginUiState {
    object Inactivo : LoginUiState()       // Estado inicial, sin acción
    object Cargando : LoginUiState()       // Esperando respuesta de la BD
    data class Exito(val usuario: Usuario) : LoginUiState()  // Login/Registro exitoso
    data class Error(val mensaje: String) : LoginUiState()   // Algo salió mal
}

/**
 * LoginViewModel: Maneja toda la lógica de negocio del inicio de sesión y registro.
 * La UI (PantallaLogin) solo observa el estado y llama funciones, nunca toca la BD.
 *
 * @param repository El repositorio de usuarios inyectado.
 */
class LoginViewModel(private val repository: UsuarioRepository) : ViewModel() {

    // Estado interno mutable (solo el ViewModel puede modificarlo)
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Inactivo)

    // Estado público de solo lectura que observa la UI
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Inicia sesión con el identificador (usuario o correo) y contraseña.
     * Valida los campos antes de consultar la base de datos.
     */
    fun iniciarSesion(identificador: String, contrasena: String) {
        // Validaciones de campos vacíos
        if (identificador.isBlank()) {
            _uiState.value = LoginUiState.Error("Escribe tu usuario o correo")
            return
        }
        if (contrasena.isBlank()) {
            _uiState.value = LoginUiState.Error("Escribe tu contraseña")
            return
        }
        if (contrasena.length < 6) {
            _uiState.value = LoginUiState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }

        // Llamada a la base de datos en un hilo secundario (coroutine)
        viewModelScope.launch {
            _uiState.value = LoginUiState.Cargando
            val resultado = repository.iniciarSesion(
                identificador = identificador.trim(),
                passwordHash = hashearContrasena(contrasena)
            )
            _uiState.value = resultado.fold(
                onSuccess = { usuario -> LoginUiState.Exito(usuario) },
                onFailure = { error -> LoginUiState.Error(error.message ?: "Error desconocido") }
            )
        }
    }

    /**
     * Registra un nuevo usuario con los datos proporcionados.
     * Valida todos los campos antes de guardar en la base de datos.
     */
    fun registrarUsuario(
        nombre: String,
        apellidoPaterno: String,
        correo: String,
        nombreUsuario: String,
        contrasena: String,
        confirmarContrasena: String
    ) {
        // Validaciones
        if (nombre.isBlank() || apellidoPaterno.isBlank()) {
            _uiState.value = LoginUiState.Error("El nombre y apellido son obligatorios")
            return
        }
        if (!correo.contains("@") || !correo.contains(".")) {
            _uiState.value = LoginUiState.Error("Escribe un correo electrónico válido")
            return
        }
        if (nombreUsuario.length < 4) {
            _uiState.value = LoginUiState.Error("El nombre de usuario debe tener al menos 4 caracteres")
            return
        }
        if (contrasena.length < 6) {
            _uiState.value = LoginUiState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }
        if (contrasena != confirmarContrasena) {
            _uiState.value = LoginUiState.Error("Las contraseñas no coinciden")
            return
        }

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
                onSuccess = { LoginUiState.Exito(nuevoUsuario) },
                onFailure = { error -> LoginUiState.Error(error.message ?: "Error al registrar") }
            )
        }
    }

    /**
     * Regresa el estado a Inactivo, por ejemplo al cerrar un diálogo de error.
     */
    fun resetearEstado() {
        _uiState.value = LoginUiState.Inactivo
    }

    /**
     * Convierte la contraseña a un hash SHA-256 antes de guardarla o compararla.
     * La contraseña en texto plano NUNCA se almacena en la base de datos.
     */
    private fun hashearContrasena(contrasena: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(contrasena.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Factory: Permite crear el ViewModel con parámetros (el repository).
     * Android no permite constructores con parámetros en ViewModels sin un Factory.
     */
    class Factory(private val repository: UsuarioRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel desconocido")
        }
    }
}