package com.fic.mobile_app_base_compose.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [
        Index(value = ["correo_electronico"], unique = true),
        Index(value = ["nombre_usuario_login"], unique = true)
    ]
)
data class Usuario(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int = 0,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "apellido_paterno")
    val apellidoPaterno: String,

    @ColumnInfo(name = "apellido_materno")
    val apellidoMaterno: String = "",

    @ColumnInfo(name = "correo_electronico")
    val correoElectronico: String,

    @ColumnInfo(name = "nombre_usuario_login")
    val nombreUsuarioLogin: String,

    /**
     * La contraseña NUNCA se guarda en texto plano.
     * Se almacena el hash generado con BCrypt o SHA-256.
     */
    @ColumnInfo(name = "password_hash")
    val passwordHash: String,

    /**
     * Roles disponibles: "admin", "entrenador", "usuario_regular"
     * Por defecto, todo usuario nuevo es "usuario_regular".
     */
    @ColumnInfo(name = "rol")
    val rol: String = "usuario_regular",

    @ColumnInfo(name = "fecha_registro")
    val fechaRegistro: Long = System.currentTimeMillis()
)