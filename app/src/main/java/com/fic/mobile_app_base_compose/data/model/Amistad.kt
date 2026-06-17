package com.fic.mobile_app_base_compose.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "amistades")
data class Amistad(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_amistad")
    val idAmistad: Int = 0,

    // El usuario que envía la solicitud (Tú)
    @ColumnInfo(name = "id_usuario_origen")
    val idUsuarioOrigen: Int,

    // El usuario que recibe la solicitud (Tu amigo)
    @ColumnInfo(name = "id_usuario_destino")
    val idUsuarioDestino: Int,

    // Estados posibles: "PENDIENTE" o "ACEPTADA"
    @ColumnInfo(name = "estado")
    val estado: String = "PENDIENTE",

    @ColumnInfo(name = "fecha_amistad")
    val fechaAmistad: Long = System.currentTimeMillis()
)