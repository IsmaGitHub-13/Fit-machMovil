package com.fic.mobile_app_base_compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fic.mobile_app_base_compose.data.model.SolicitudAmistad
import com.fic.mobile_app_base_compose.data.model.Usuario
import com.fic.mobile_app_base_compose.data.repository.FirebaseRepository
import com.fic.mobile_app_base_compose.data.repository.SolicitudAmistadRepository
import com.fic.mobile_app_base_compose.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AmistadViewModel(
    private val solicitudRepo: SolicitudAmistadRepository,
    private val usuarioRepo: UsuarioRepository
) : ViewModel() {

    private val firebaseRepository = FirebaseRepository()

    private val _solicitudesPendientes = MutableStateFlow<List<SolicitudAmistad>>(emptyList())
    val solicitudesPendientes: StateFlow<List<SolicitudAmistad>> = _solicitudesPendientes.asStateFlow()

    private val _idsAmigos = MutableStateFlow<List<Int>>(emptyList())
    val idsAmigos: StateFlow<List<Int>> = _idsAmigos.asStateFlow()

    private val _resultadosBusqueda = MutableStateFlow<List<Usuario>>(emptyList())
    val resultadosBusqueda: StateFlow<List<Usuario>> = _resultadosBusqueda.asStateFlow()

    // Resultados de búsqueda en Firebase (usuarios de otros celulares)
    private val _resultadosFirebase = MutableStateFlow<List<String>>(emptyList())
    val resultadosFirebase: StateFlow<List<String>> = _resultadosFirebase.asStateFlow()

    private val _mensajeAccion = MutableStateFlow<String?>(null)
    val mensajeAccion: StateFlow<String?> = _mensajeAccion.asStateFlow()

    fun cargarDatos(idUsuario: Int) {
        viewModelScope.launch {
            solicitudRepo.obtenerSolicitudesPendientes(idUsuario).collect {
                _solicitudesPendientes.value = it
            }
        }
        viewModelScope.launch {
            solicitudRepo.obtenerIdsAmigos(idUsuario).collect {
                _idsAmigos.value = it
            }
        }
    }

    fun buscarUsuarios(query: String, miId: Int) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _resultadosBusqueda.value = emptyList()
                _resultadosFirebase.value = emptyList()
                return@launch
            }
            // Búsqueda local (Room)
            usuarioRepo.buscarUsuarios("%$query%", miId).collect {
                _resultadosBusqueda.value = it
            }
        }
        // Búsqueda en Firebase (otros celulares)
        viewModelScope.launch {
            if (query.isBlank()) return@launch
            val resultado = firebaseRepository.buscarUsuario(query.trim().lowercase())
            resultado.onSuccess { usuarioFirebase ->
                if (usuarioFirebase != null) {
                    _resultadosFirebase.value = listOf(usuarioFirebase.nombreUsuario)
                } else {
                    _resultadosFirebase.value = emptyList()
                }
            }
        }
    }

    fun enviarSolicitudFirebase(deMiUsuario: String, paraNombreUsuario: String) {
        viewModelScope.launch {
            val resultado = firebaseRepository.enviarSolicitud(deMiUsuario, paraNombreUsuario)
            _mensajeAccion.value = resultado.fold(
                onSuccess = { "Solicitud enviada a @$paraNombreUsuario" },
                onFailure = { it.message }
            )
        }
    }

    fun enviarSolicitud(idRemitente: Int, idDestinatario: Int) {
        viewModelScope.launch {
            val resultado = solicitudRepo.enviarSolicitud(idRemitente, idDestinatario)
            _mensajeAccion.value = resultado.fold(
                onSuccess = { "Solicitud enviada" },
                onFailure = { it.message }
            )
        }
    }

    fun aceptarSolicitud(idSolicitud: Int) {
        viewModelScope.launch {
            solicitudRepo.aceptarSolicitud(idSolicitud)
            _mensajeAccion.value = "Solicitud aceptada"
        }
    }

    fun rechazarSolicitud(idSolicitud: Int) {
        viewModelScope.launch {
            solicitudRepo.rechazarSolicitud(idSolicitud)
        }
    }

    fun limpiarMensaje() { _mensajeAccion.value = null }

    fun eliminarAmigo(miId: Int, idAmigo: Int) {
        viewModelScope.launch {
            solicitudRepo.eliminarAmigo(miId, idAmigo)
            _mensajeAccion.value = "Amigo eliminado"
        }
    }

    class Factory(
        private val solicitudRepo: SolicitudAmistadRepository,
        private val usuarioRepo: UsuarioRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AmistadViewModel(solicitudRepo, usuarioRepo) as T
        }
    }
}