package com.fic.mobile_app_base_compose.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plan_progresion")
data class PlanProgresion(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_plan")
    val idPlan: Int = 0,

    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int = 0,

    @ColumnInfo(name = "ejercicio")
    val ejercicio: String = "",

    @ColumnInfo(name = "rm_inicial")
    val rmInicial: Float = 0f,

    @ColumnInfo(name = "objetivo_rm")
    val objetivoRm: Float = 0f,

    @ColumnInfo(name = "nivel")
    val nivel: String = "principiante",

    @ColumnInfo(name = "semanas_totales")
    val semanasTotales: Int = 12,

    @ColumnInfo(name = "fecha_inicio")
    val fechaInicio: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "activo")
    val activo: Boolean = true
)