package com.fic.mobile_app_base_compose.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ejercicios")
data class Ejercicio(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_ejercicio")
    val idEjercicio: Int = 0,

    @ColumnInfo(name = "nombre")
    val nombre: String = "",

    @ColumnInfo(name = "descripcion")
    val descripcion: String = "",

    @ColumnInfo(name = "tipo_ejercicio")
    val tipoEjercicio: String = "",

    @ColumnInfo(name = "grupo_muscular")
    val grupoMuscular: String = "",

    @ColumnInfo(name = "instrucciones")
    val instrucciones: String = ""
)