package com.fic.mobile_app_base_compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fic.mobile_app_base_compose.data.model.Publicacion
import com.fic.mobile_app_base_compose.data.repository.PublicacionRepository
import com.fic.mobile_app_base_compose.data.repository.SolicitudAmistadRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FeedViewModel(
    private val publicacionRepo: PublicacionRepository,
    private val amistadRepo: SolicitudAmistadRepository
) : ViewModel() {

    private val _publicaciones = MutableStateFlow<List<Publicacion>>(emptyList())
    val publicaciones: StateFlow<List<Publicacion>> = _publicaciones.asStateFlow()

    private val _publicando = MutableStateFlow(false)
    val publicando: StateFlow<Boolean> = _publicando.asStateFlow()

    fun cargarFeed(idUsuario: Int) {
        viewModelScope.launch {
            // Observa los IDs de amigos y recarga el feed cuando cambian
            amistadRepo.obtenerIdsAmigos(idUsuario).collect { idsAmigos ->
                val todosIds = (idsAmigos + idUsuario).distinct()
                publicacionRepo.obtenerFeed(todosIds).collect { lista ->
                    _publicaciones.value = lista
                }
            }
        }
    }

    fun publicar(idUsuario: Int, descripcion: String, nombreRutina: String) {
        if (descripcion.isBlank()) return
        viewModelScope.launch {
            _publicando.value = true
            publicacionRepo.publicar(
                Publicacion(
                    idUsuario = idUsuario,
                    descripcion = descripcion.trim(),
                    idRutina = 0,
                    comentarios = nombreRutina.trim()
                )
            )
            _publicando.value = false
        }
    }

    fun darLike(idPublicacion: Int) {
        viewModelScope.launch { publicacionRepo.darLike(idPublicacion) }
    }

    fun quitarLike(idPublicacion: Int) {
        viewModelScope.launch { publicacionRepo.quitarLike(idPublicacion) }
    }

    class Factory(
        private val publicacionRepo: PublicacionRepository,
        private val amistadRepo: SolicitudAmistadRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return FeedViewModel(publicacionRepo, amistadRepo) as T
        }
    }
}