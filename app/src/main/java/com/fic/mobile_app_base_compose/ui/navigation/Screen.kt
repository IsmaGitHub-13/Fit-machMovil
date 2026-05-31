package com.fic.mobile_app_base_compose.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("pantalla_login")
    object Registro : Screen("pantalla_registro")

    // Rutas base del profesor
    object Form : Screen("form")
    object List : Screen("list")
    object Catalog : Screen("catalog")

    // FitMatch - existentes
    object Panel : Screen("pantalla_panel")
    object Rutinas : Screen("pantalla_rutinas")
    object Historial : Screen("pantalla_historial")
    object Ejercicios : Screen("pantalla_ejercicios")

    // FitMatch - nuevas
    object Feed : Screen("pantalla_feed")
    object Perfil : Screen("pantalla_perfil")
    object Amigos : Screen("pantalla_amigos")
    object PlanProgresion : Screen("pantalla_plan_progresion")
    object PerfilAmigo : Screen("pantalla_perfil_amigo/{nombre}/{usuario}") {
        fun crearRuta(nombre: String, usuario: String) =
            "pantalla_perfil_amigo/$nombre/$usuario"
    }
}