package com.fic.mobile_app_base_compose

object SesionUsuario {
    var idUsuario: Int = 0
    var nombreUsuario: String = ""
    var nombre: String = ""

    fun iniciar(id: Int, nombreUsuarioLogin: String, nombreCompleto: String) {
        idUsuario = id
        nombreUsuario = nombreUsuarioLogin
        nombre = nombreCompleto
    }

    fun cerrar() {
        idUsuario = 0
        nombreUsuario = ""
        nombre = ""
    }
}