package com.fic.mobile_app_base_compose.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "log_actividad")
data class LogActividad(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_log")
    val idLog: Int = 0,

    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int = 0,

    @ColumnInfo(name = "id_rutina")
    val idRutina: Int = 0,

    @ColumnInfo(name = "fecha")
    val fecha: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "duracion_real_minutos")
    val duracionRealMinutos: Int = 0,

    @ColumnInfo(name = "calificacion")
    val calificacion: Int = 0,

    @ColumnInfo(name = "comentarios")
    val comentarios: String = ""
)