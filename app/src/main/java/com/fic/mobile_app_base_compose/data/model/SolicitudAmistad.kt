package com.fic.mobile_app_base_compose.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solicitudes_amistad")
data class SolicitudAmistad(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_solicitud")
    val idSolicitud: Int = 0,

    @ColumnInfo(name = "id_remitente")
    val idRemitente: Int = 0,

    @ColumnInfo(name = "id_destinatario")
    val idDestinatario: Int = 0,

    // "pendiente", "aceptada", "rechazada"
    @ColumnInfo(name = "estado")
    val estado: String = "pendiente",

    @ColumnInfo(name = "fecha")
    val fecha: Long = System.currentTimeMillis()
)