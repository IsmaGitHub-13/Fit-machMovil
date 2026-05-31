package com.fic.mobile_app_base_compose.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "semanas_progresion")
data class SemanaProgresion(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_semana")
    val idSemana: Int = 0,

    @ColumnInfo(name = "id_plan")
    val idPlan: Int = 0,

    @ColumnInfo(name = "numero_semana")
    val numeroSemana: Int = 0,

    @ColumnInfo(name = "peso_kg")
    val pesoKg: Float = 0f,

    @ColumnInfo(name = "series")
    val series: Int = 0,

    @ColumnInfo(name = "repeticiones")
    val repeticiones: Int = 0,

    @ColumnInfo(name = "completada")
    val completada: Boolean = false,

    @ColumnInfo(name = "fecha_completada")
    val fechaCompletada: Long? = null
)