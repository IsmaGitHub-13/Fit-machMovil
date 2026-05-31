package com.fic.mobile_app_base_compose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fic.mobile_app_base_compose.ui.screens.PantallaAmigos
import com.fic.mobile_app_base_compose.ui.screens.PantallaEjercicios
import com.fic.mobile_app_base_compose.ui.screens.PantallaFeed
import com.fic.mobile_app_base_compose.ui.screens.PantallaHistorial
import com.fic.mobile_app_base_compose.ui.screens.PantallaLogin
import com.fic.mobile_app_base_compose.ui.screens.PantallaPanel
import com.fic.mobile_app_base_compose.ui.screens.PantallaPerfil
import com.fic.mobile_app_base_compose.ui.screens.PantallaPerfilAmigo
import com.fic.mobile_app_base_compose.ui.screens.PantallaPlanProgresion
import com.fic.mobile_app_base_compose.ui.screens.PantallaRutinas
import com.fic.mobile_app_base_compose.ui.screens.PantallaRegistro

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            PantallaLogin(
                onIngresar = {
                    navController.navigate(Screen.Panel.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onIrARegistro = { navController.navigate(Screen.Registro.route) }
            )
        }

        composable(Screen.Registro.route) {
            PantallaRegistro(
                onRegistroExitoso = {
                    navController.navigate(Screen.Panel.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.Panel.route) {
            PantallaPanel(
                onNavegarARutinas = { navController.navigate(Screen.Rutinas.route) },
                onNavegarAHistorial = { navController.navigate(Screen.Historial.route) },
                onNavegarAEjercicios = { navController.navigate(Screen.Ejercicios.route) },
                onNavegarAFeed = { navController.navigate(Screen.Feed.route) },
                onNavegarAPerfil = { navController.navigate(Screen.Perfil.route) },
                onCerrarSesion = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Panel.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Rutinas.route) {
            PantallaRutinas(onVolver = { navController.popBackStack() })
        }

        composable(Screen.Historial.route) {
            PantallaHistorial(onVolver = { navController.popBackStack() })
        }

        composable(Screen.Ejercicios.route) {
            PantallaEjercicios(onVolver = { navController.popBackStack() })
        }

        composable(Screen.Feed.route) {
            PantallaFeed(onVolver = { navController.popBackStack() })
        }

        composable(Screen.Perfil.route) {
            PantallaPerfil(
                onVolver = { navController.popBackStack() },
                onNavegarAAmigos = { navController.navigate(Screen.Amigos.route) },
                onNavegarAPlanProgresion = { navController.navigate(Screen.PlanProgresion.route) }
            )
        }

        composable(Screen.Amigos.route) {
            PantallaAmigos(
                onVolver = { navController.popBackStack() },
                onVerPerfil = { amigo ->
                    navController.navigate(
                        Screen.PerfilAmigo.crearRuta(amigo.nombre, amigo.usuario)
                    )
                }
            )
        }

        composable(Screen.PlanProgresion.route) {
            PantallaPlanProgresion(onVolver = { navController.popBackStack() })
        }

        composable(
            route = Screen.PerfilAmigo.route,
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("usuario") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            PantallaPerfilAmigo(
                nombre = backStackEntry.arguments?.getString("nombre") ?: "",
                usuario = backStackEntry.arguments?.getString("usuario") ?: "",
                onVolver = { navController.popBackStack() }
            )
        }
    }
}