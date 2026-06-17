package com.fic.mobile_app_base_compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fic.mobile_app_base_compose.data.model.PlanProgresion
import com.fic.mobile_app_base_compose.data.model.SemanaProgresion
import com.fic.mobile_app_base_compose.data.repository.PlanProgresionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PlanUiState {
    object Inactivo : PlanUiState()
    object Cargando : PlanUiState()
    data class PlanGenerado(val semanas: List<SemanaProgresion>) : PlanUiState()
    data class Error(val mensaje: String) : PlanUiState()
}

class PlanProgresionViewModel(private val repository: PlanProgresionRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<PlanUiState>(PlanUiState.Inactivo)
    val uiState: StateFlow<PlanUiState> = _uiState.asStateFlow()

    private val _planesActivos = MutableStateFlow<List<PlanProgresion>>(emptyList())
    val planesActivos: StateFlow<List<PlanProgresion>> = _planesActivos.asStateFlow()

    private val _semanasPlan = MutableStateFlow<List<SemanaProgresion>>(emptyList())
    val semanasPlan: StateFlow<List<SemanaProgresion>> = _semanasPlan.asStateFlow()

    fun cargarPlanes(idUsuario: Int) {
        viewModelScope.launch {
            repository.obtenerPlanesActivos(idUsuario).collect {
                _planesActivos.value = it
            }
        }
    }

    fun cargarSemanas(idPlan: Int) {
        viewModelScope.launch {
            repository.obtenerSemanas(idPlan).collect {
                _semanasPlan.value = it
            }
        }
    }

    /**
     * Genera un plan de progresión basado en el 1RM usando periodización lineal.
     * Principiante: +2.5kg cada semana
     * Intermedio: +2.5kg cada 2 semanas
     * Avanzado: periodización ondulatoria (heavy/light/medium)
     */
    fun generarPlan(
        idUsuario: Int,
        ejercicio: String,
        rmActual: Float,
        objetivoRm: Float,
        nivel: String
    ) {
        if (ejercicio.isBlank()) {
            _uiState.value = PlanUiState.Error("Escribe el nombre del ejercicio")
            return
        }
        if (rmActual <= 0f) {
            _uiState.value = PlanUiState.Error("El 1RM debe ser mayor a 0")
            return
        }
        if (objetivoRm <= rmActual) {
            _uiState.value = PlanUiState.Error("El objetivo debe ser mayor al 1RM actual")
            return
        }

        _uiState.value = PlanUiState.Cargando

        val semanas = generarSemanas(rmActual, objetivoRm, nivel)
        val plan = PlanProgresion(
            idUsuario = idUsuario,
            ejercicio = ejercicio,
            rmInicial = rmActual,
            objetivoRm = objetivoRm,
            nivel = nivel,
            semanasTotales = semanas.size
        )

        viewModelScope.launch {
            repository.crearPlanConSemanas(plan, semanas)
            _uiState.value = PlanUiState.PlanGenerado(semanas)
        }
    }

    private fun generarSemanas(rmActual: Float, objetivoRm: Float, nivel: String): List<SemanaProgresion> {
        val semanas = mutableListOf<SemanaProgresion>()
        var pesoActual = rmActual

        return when (nivel) {
            "principiante" -> {
                // +2.5kg por semana, 3 series x 5 reps al 80% del peso actual
                var semana = 1
                while (pesoActual < objetivoRm) {
                    semanas.add(SemanaProgresion(
                        numeroSemana = semana,
                        pesoKg = (pesoActual * 0.80f).redondear(),
                        series = 3,
                        repeticiones = 5
                    ))
                    pesoActual += 2.5f
                    semana++
                    if (semana > 16) break
                }
                semanas
            }
            "intermedio" -> {
                // Alternancia: semana pesada (85%, 4x4) y semana media (75%, 4x6)
                var semana = 1
                while (pesoActual < objetivoRm) {
                    val esPesada = semana % 2 != 0
                    semanas.add(SemanaProgresion(
                        numeroSemana = semana,
                        pesoKg = if (esPesada) (pesoActual * 0.85f).redondear() else (pesoActual * 0.75f).redondear(),
                        series = 4,
                        repeticiones = if (esPesada) 4 else 6
                    ))
                    if (semana % 2 == 0) pesoActual += 2.5f
                    semana++
                    if (semana > 16) break
                }
                semanas
            }
            else -> {
                // Avanzado: ciclo 3 semanas (heavy/medium/light) + deload
                val ciclo = listOf(
                    Triple(0.90f, 5, 3),  // Heavy: 90% 5x3
                    Triple(0.80f, 4, 5),  // Medium: 80% 4x5
                    Triple(0.70f, 3, 8),  // Light: 70% 3x8
                    Triple(0.60f, 2, 5)   // Deload: 60% 2x5
                )
                var semana = 1
                var cicloIdx = 0
                while (pesoActual < objetivoRm) {
                    val (pct, series, reps) = ciclo[cicloIdx % 4]
                    semanas.add(SemanaProgresion(
                        numeroSemana = semana,
                        pesoKg = (pesoActual * pct).redondear(),
                        series = series,
                        repeticiones = reps
                    ))
                    if (cicloIdx % 4 == 3) pesoActual += 2.5f
                    cicloIdx++
                    semana++
                    if (semana > 20) break
                }
                semanas
            }
        }
    }

    private fun Float.redondear(): Float = (Math.round(this * 2) / 2.0f)

    fun resetear() { _uiState.value = PlanUiState.Inactivo }

    class Factory(private val repository: PlanProgresionRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PlanProgresionViewModel(repository) as T
        }
    }
}