package com.fic.mobile_app_base_compose.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rutina_ejercicio")
data class RutinaEjercicio(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "id_rutina")
    val idRutina: Int = 0,

    @ColumnInfo(name = "id_ejercicio")
    val idEjercicio: Int = 0,

    @ColumnInfo(name = "orden")
    val orden: Int = 0,

    @ColumnInfo(name = "series")
    val series: Int = 0,

    @ColumnInfo(name = "repeticiones")
    val repeticiones: Int = 0,

    @ColumnInfo(name = "descanso_segundos")
    val descansoSegundos: Int = 0
)