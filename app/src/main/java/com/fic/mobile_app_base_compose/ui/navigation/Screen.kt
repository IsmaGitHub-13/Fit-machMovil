package com.fic.mobile_app_base_compose.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("pantalla_login")
    object Registro : Screen("pantalla_registro")

    object Form : Screen("form")
    object List : Screen("list")
    object Catalog : Screen("catalog")

    object Panel : Screen("pantalla_panel")
    object Rutinas : Screen("pantalla_rutinas")
    object Historial : Screen("pantalla_historial")
    object Ejercicios : Screen("pantalla_ejercicios")

    object Feed : Screen("pantalla_feed")
    object Perfil : Screen("pantalla_perfil")
    object Amigos : Screen("pantalla_amigos")
    object PlanProgresion : Screen("pantalla_plan_progresion")
}