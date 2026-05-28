package com.fic.mobile_app_base_compose.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "publicaciones")
data class Publicacion(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_publicacion")
    val idPublicacion: Int = 0,

    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int = 0,

    @ColumnInfo(name = "descripcion")
    val descripcion: String = "",

    @ColumnInfo(name = "id_rutina")
    val idRutina: Int = 0,

    @ColumnInfo(name = "likes")
    val likes: Int = 0,

    @ColumnInfo(name = "comentarios")
    val comentarios: String = "",

    @ColumnInfo(name = "fecha")
    val fecha: Long = System.currentTimeMillis()
)