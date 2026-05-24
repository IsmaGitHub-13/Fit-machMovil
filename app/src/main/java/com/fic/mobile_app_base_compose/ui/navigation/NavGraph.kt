package com.fic.mobile_app_base_compose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fic.mobile_app_base_compose.ui.screens.PantallaLogin
import com.fic.mobile_app_base_compose.ui.screens.PantallaPanel
import com.fic.mobile_app_base_compose.ui.screens.PantallaRegistro
import com.fic.mobile_app_base_compose.ui.screens.PantallaRutinas

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // 1. Pantalla de Login
        composable(Screen.Login.route) {
            PantallaLogin(
                onIngresar = {
                    navController.navigate(Screen.Panel.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onIrARegistro = {
                    navController.navigate(Screen.Registro.route)
                }
            )
        }

        // 2. Pantalla de Registro
        composable(Screen.Registro.route) {
            PantallaRegistro(
                onRegistroExitoso = {
                    navController.navigate(Screen.Panel.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // 3. Panel Principal
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

        // 4. Módulo de Rutinas
        composable(Screen.Rutinas.route) {
            PantallaRutinas(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}