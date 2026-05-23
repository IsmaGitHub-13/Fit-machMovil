package com.fic.mobile_app_base_compose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fic.mobile_app_base_compose.ui.screens.PantallaLogin
import com.fic.mobile_app_base_compose.ui.screens.PantallaPanel
import com.fic.mobile_app_base_compose.ui.screens.PantallaRutinas

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // 1. Pantalla de Login de FitMatch
        composable(Screen.Login.route) {
            PantallaLogin(
                onIngresar = {
                    navController.navigate(Screen.Panel.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. Panel Principal de FitMatch
        composable(Screen.Panel.route) {
            PantallaPanel(
                onNavegarARutinas = { navController.navigate(Screen.Rutinas.route) },
                onCerrarSesion = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Panel.route) { inclusive = true }
                    }
                }
            )
        }

        // 3. Módulo de Gestión de Rutinas
        composable(Screen.Rutinas.route) {
            PantallaRutinas(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}