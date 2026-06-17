package com.fic.mobile_app_base_compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fic.mobile_app_base_compose.data.model.Rutina
import com.fic.mobile_app_base_compose.data.repository.RutinaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Todos los posibles estados de la pantalla de Rutinas
sealed class RutinaUiState {
    object Inactivo : RutinaUiState()
    object Cargando : RutinaUiState()
    object Exito : RutinaUiState()
    data class Error(val mensaje: String) : RutinaUiState()
}

class RutinaViewModel(private val repository: RutinaRepository) : ViewModel() {

    // Estado de las operaciones (guardar, eliminar, etc.)
    private val _uiState = MutableStateFlow<RutinaUiState>(RutinaUiState.Inactivo)
    val uiState: StateFlow<RutinaUiState> = _uiState.asStateFlow()

    // Lista de rutinas que se muestra en pantalla
    private val _rutinas = MutableStateFlow<List<Rutina>>(emptyList())
    val rutinas: StateFlow<List<Rutina>> = _rutinas.asStateFlow()

    // Carga las rutinas del usuario y las mantiene actualizadas
    fun cargarRutinas(idUsuario: Int) {
        viewModelScope.launch {
            repository.obtenerRutinas(idUsuario)
                .catch { e -> _uiState.value = RutinaUiState.Error(e.message ?: "Error al cargar") }
                .collect { lista -> _rutinas.value = lista }
        }
    }

    fun guardarRutina(nombre: String, descripcion: String, nivel: String,
                      duracion: Int, idUsuario: Int) {
        if (nombre.isBlank()) {
            _uiState.value = RutinaUiState.Error("El nombre es obligatorio")
            return
        }
        viewModelScope.launch {
            _uiState.value = RutinaUiState.Cargando
            val rutina = Rutina(
                nombre = nombre.trim(),
                descripcion = descripcion.trim(),
                nivel = nivel,
                duracionMinutos = duracion,
                idCreador = idUsuario,
                esPublica = true
            )
            val resultado = repository.guardarRutina(rutina)
            _uiState.value = resultado.fold(
                onSuccess = { RutinaUiState.Exito },
                onFailure = { e -> RutinaUiState.Error(e.message ?: "Error") }
            )
        }
    }

    fun actualizarRutina(rutina: Rutina) {
        viewModelScope.launch {
            _uiState.value = RutinaUiState.Cargando
            val resultado = repository.actualizarRutina(rutina)
            _uiState.value = resultado.fold(
                onSuccess = { RutinaUiState.Exito },
                onFailure = { e -> RutinaUiState.Error(e.message ?: "Error") }
            )
        }
    }

    fun eliminarRutina(rutina: Rutina) {
        viewModelScope.launch {
            val resultado = repository.eliminarRutina(rutina)
            if (resultado.isFailure) {
                _uiState.value = RutinaUiState.Error("No se pudo eliminar la rutina")
            }
        }
    }

    fun resetearEstado() {
        _uiState.value = RutinaUiState.Inactivo
    }

    // Factory para poder pasar el repository al ViewModel
    class Factory(private val repository: RutinaRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RutinaViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RutinaViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel desconocido")
        }
    }
}