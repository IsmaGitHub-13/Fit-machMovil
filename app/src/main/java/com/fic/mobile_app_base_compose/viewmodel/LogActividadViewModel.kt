package com.fic.mobile_app_base_compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fic.mobile_app_base_compose.data.model.LogActividad
import com.fic.mobile_app_base_compose.data.repository.LogActividadRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LogActividadViewModel(private val repository: LogActividadRepository) : ViewModel() {

    fun obtenerHistorial(idUsuario: Int): StateFlow<List<LogActividad>> {
        return repository.obtenerHistorial(idUsuario)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun registrarActividad(log: LogActividad) {
        viewModelScope.launch {
            repository.registrarActividad(log)
        }
    }

    fun eliminarActividad(idLog: Int) {
        viewModelScope.launch {
            repository.eliminarActividad(idLog)
        }
    }

    class Factory(private val repository: LogActividadRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LogActividadViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LogActividadViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel desconocido")
        }
    }
}
