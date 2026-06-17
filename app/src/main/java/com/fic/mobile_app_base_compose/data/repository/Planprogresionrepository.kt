package com.fic.mobile_app_base_compose.data.repository

import com.fic.mobile_app_base_compose.data.local.PlanProgresionDao
import com.fic.mobile_app_base_compose.data.model.PlanProgresion
import com.fic.mobile_app_base_compose.data.model.SemanaProgresion
import kotlinx.coroutines.flow.Flow

class PlanProgresionRepository(private val dao: PlanProgresionDao) {

    fun obtenerPlanesActivos(idUsuario: Int): Flow<List<PlanProgresion>> =
        dao.obtenerPlanesActivos(idUsuario)

    fun obtenerSemanas(idPlan: Int): Flow<List<SemanaProgresion>> =
        dao.obtenerSemanasDePlan(idPlan)

    suspend fun crearPlanConSemanas(plan: PlanProgresion, semanas: List<SemanaProgresion>) {
        val idPlan = dao.crearPlan(plan)
        dao.insertarSemanas(semanas.map { it.copy(idPlan = idPlan.toInt()) })
    }

    suspend fun completarSemana(semana: SemanaProgresion) =
        dao.actualizarSemana(semana.copy(completada = true, fechaCompletada = System.currentTimeMillis()))

    suspend fun desactivarPlan(idPlan: Int) =
        dao.desactivarPlan(idPlan)
}