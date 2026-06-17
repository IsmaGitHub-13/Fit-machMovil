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

    // — Room (local) —
    private val _solicitudesPendientes = MutableStateFlow<List<SolicitudAmistad>>(emptyList())
    val solicitudesPendientes: StateFlow<List<SolicitudAmistad>> = _solicitudesPendientes.asStateFlow()

    private val _idsAmigos = MutableStateFlow<List<Int>>(emptyList())
    val idsAmigos: StateFlow<List<Int>> = _idsAmigos.asStateFlow()

    private val _resultadosBusqueda = MutableStateFlow<List<Usuario>>(emptyList())
    val resultadosBusqueda: StateFlow<List<Usuario>> = _resultadosBusqueda.asStateFlow()

    // — Firebase (red) —
    private val _resultadosFirebase = MutableStateFlow<List<String>>(emptyList())
    val resultadosFirebase: StateFlow<List<String>> = _resultadosFirebase.asStateFlow()

    private val _solicitudesFirebase = MutableStateFlow<List<String>>(emptyList())
    val solicitudesFirebase: StateFlow<List<String>> = _solicitudesFirebase.asStateFlow()

    private val _amigosFirebase = MutableStateFlow<List<String>>(emptyList())
    val amigosFirebase: StateFlow<List<String>> = _amigosFirebase.asStateFlow()

    private val _mensajeAccion = MutableStateFlow<String?>(null)
    val mensajeAccion: StateFlow<String?> = _mensajeAccion.asStateFlow()

    fun cargarDatos(idUsuario: Int) {
        // Room
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

    fun cargarDatosFirebase(miUsuario: String) {
        // Solicitudes pendientes de Firebase
        viewModelScope.launch {
            val resultado = firebaseRepository.obtenerSolicitudesPendientes(miUsuario)
            resultado.onSuccess { lista ->
                _solicitudesFirebase.value = lista.map { it.de }
            }
        }
        // Amigos de Firebase
        viewModelScope.launch {
            val resultado = firebaseRepository.obtenerAmigos(miUsuario)
            resultado.onSuccess { lista ->
                _amigosFirebase.value = lista
            }
        }
    }

    fun aceptarSolicitudFirebase(miUsuario: String, deUsuario: String) {
        viewModelScope.launch {
            val resultado = firebaseRepository.aceptarSolicitud(deUsuario, miUsuario)
            resultado.onSuccess {
                _mensajeAccion.value = "Ahora son amigos"
                cargarDatosFirebase(miUsuario)
            }
            resultado.onFailure {
                _mensajeAccion.value = "Error al aceptar solicitud"
            }
        }
    }

    fun rechazarSolicitudFirebase(miUsuario: String, deUsuario: String) {
        viewModelScope.launch {
            firebaseRepository.rechazarSolicitud(deUsuario, miUsuario)
            cargarDatosFirebase(miUsuario)
        }
    }

    fun buscarUsuarios(query: String, miId: Int) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _resultadosBusqueda.value = emptyList()
                _resultadosFirebase.value = emptyList()
                return@launch
            }
            usuarioRepo.buscarUsuarios("%$query%", miId).collect {
                _resultadosBusqueda.value = it
            }
        }
        viewModelScope.launch {
            if (query.isBlank()) return@launch
            val resultado = firebaseRepository.buscarUsuario(query.trim().lowercase())
            resultado.onSuccess { usuarioFirebase ->
                _resultadosFirebase.value = if (usuarioFirebase != null)
                    listOf(usuarioFirebase.nombreUsuario)
                else emptyList()
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