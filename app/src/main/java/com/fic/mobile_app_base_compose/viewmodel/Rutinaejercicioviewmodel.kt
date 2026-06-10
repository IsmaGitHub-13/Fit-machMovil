package com.fic.mobile_app_base_compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fic.mobile_app_base_compose.data.model.RutinaEjercicio
import com.fic.mobile_app_base_compose.data.repository.RutinaEjercicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RutinaEjercicioViewModel(private val repository: RutinaEjercicioRepository) : ViewModel() {

    private val _ejercicios = MutableStateFlow<List<RutinaEjercicio>>(emptyList())
    val ejercicios: StateFlow<List<RutinaEjercicio>> = _ejercicios.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargarEjercicios(idRutina: Int) {
        viewModelScope.launch {
            repository.obtenerEjerciciosDeLaRutina(idRutina).collect {
                _ejercicios.value = it
            }
        }
    }

    fun agregarEjercicio(
        idRutina: Int,
        idEjercicio: Int,
        nombreEjercicio: String,
        series: Int,
        repeticiones: Int,
        descansoSegundos: Int
    ) {
        if (series <= 0) { _error.value = "Las series deben ser mayor a 0"; return }
        if (repeticiones <= 0) { _error.value = "Las repeticiones deben ser mayor a 0"; return }

        viewModelScope.launch {
            val orden = _ejercicios.value.size + 1
            val resultado = repository.agregarEjercicio(
                RutinaEjercicio(
                    idRutina = idRutina,
                    idEjercicio = idEjercicio,
                    orden = orden,
                    series = series,
                    repeticiones = repeticiones,
                    descansoSegundos = descansoSegundos
                )
            )
            if (resultado.isFailure) _error.value = "Error al agregar ejercicio"
        }
    }

    fun eliminarEjercicio(id: Int) {
        viewModelScope.launch { repository.eliminarEjercicio(id) }
    }

    fun limpiarError() { _error.value = null }

    class Factory(private val repository: RutinaEjercicioRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return RutinaEjercicioViewModel(repository) as T
        }
    }
}