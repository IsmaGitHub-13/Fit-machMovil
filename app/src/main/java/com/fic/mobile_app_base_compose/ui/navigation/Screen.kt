package com.fic.mobile_app_base_compose.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("pantalla_login")
    object Registro : Screen("pantalla_registro")

    // Rutas base requeridas obligatoriamente por el menú del profesor
    object Form : Screen("form")
    object List : Screen("list")
    object Catalog : Screen("catalog")

    // Rutas de FitMatch
    object Panel : Screen("pantalla_panel")
    object Rutinas : Screen("pantalla_rutinas")
    object Historial : Screen("pantalla_historial")
}