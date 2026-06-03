package com.fic.mobile_app_base_compose.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fic.mobile_app_base_compose.data.model.PlanProgresion
import com.fic.mobile_app_base_compose.data.model.SemanaProgresion
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanProgresionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun crearPlan(plan: PlanProgresion): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarSemanas(semanas: List<SemanaProgresion>)

    @Query("SELECT * FROM plan_progresion WHERE id_usuario = :idUsuario AND activo = 1")
    fun obtenerPlanesActivos(idUsuario: Int): Flow<List<PlanProgresion>>

    @Query("SELECT * FROM plan_progresion WHERE id_usuario = :idUsuario")
    fun obtenerTodosLosPlanes(idUsuario: Int): Flow<List<PlanProgresion>>

    @Query("SELECT * FROM semanas_progresion WHERE id_plan = :idPlan ORDER BY numero_semana ASC")
    fun obtenerSemanasDePlan(idPlan: Int): Flow<List<SemanaProgresion>>

    @Update
    suspend fun actualizarSemana(semana: SemanaProgresion)

    @Query("UPDATE plan_progresion SET activo = 0 WHERE id_plan = :idPlan")
    suspend fun desactivarPlan(idPlan: Int)
}