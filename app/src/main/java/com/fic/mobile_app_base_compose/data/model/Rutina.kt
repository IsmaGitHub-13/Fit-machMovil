package com.fic.mobile_app_base_compose.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rutinas")
data class Rutina(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_rutina")
    val idRutina: Int = 0,

    @ColumnInfo(name = "nombre")
    val nombre: String = "",

    @ColumnInfo(name = "descripcion")
    val descripcion: String = "",

    @ColumnInfo(name = "nivel")
    val nivel: String = "",

    @ColumnInfo(name = "duracion_minutos")
    val duracionMinutos: Int = 0,

    @ColumnInfo(name = "id_creador")
    val idCreador: Int = 0,

    @ColumnInfo(name = "es_publica")
    val esPublica: Boolean = true
)