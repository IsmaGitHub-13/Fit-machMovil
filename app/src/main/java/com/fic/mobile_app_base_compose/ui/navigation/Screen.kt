package com.fic.mobile_app_base_compose.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("pantalla_login")

    // Rutas base requeridas obligatoriamente por el menú del profesor
    object Form : Screen("form")
    object List : Screen("list")
    object Catalog : Screen("catalog")

    // Tus rutas nuevas de FitMatch
    object Panel : Screen("pantalla_panel")
    object Rutinas : Screen("pantalla_rutinas")
}