package com.fic.mobile_app_base_compose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fic.mobile_app_base_compose.ui.screens.PantallaHistorial
import com.fic.mobile_app_base_compose.ui.screens.PantallaEjercicios
import com.fic.mobile_app_base_compose.ui.screens.PantallaLogin
import com.fic.mobile_app_base_compose.ui.screens.PantallaPanel
import com.fic.mobile_app_base_compose.ui.screens.PantallaProgreso
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
                onNavegarAHistorial = { navController.navigate(Screen.Historial.route) },
                onNavegarAEjercicios = { navController.navigate(Screen.Ejercicios.route) },
                onNavegarAProgreso = { navController.navigate(Screen.Progreso.route) },
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

        // 5. Historial de Ejercicios
        composable(Screen.Historial.route) {
            PantallaHistorial(
                onVolver = { navController.popBackStack() }
            )
        }

        // 6. Módulo de Ejercicios
        composable(Screen.Ejercicios.route) {
            PantallaEjercicios(
                onVolver = { navController.popBackStack() }
            )
        }

        // 7. Pantalla de Progreso
        composable(Screen.Progreso.route) {
            PantallaProgreso(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}