package com.fic.mobile_app_base_compose.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fic.mobile_app_base_compose.R
import com.fic.mobile_app_base_compose.ui.navigation.Screen

/**
 * Componente de menú inferior (Bottom Navigation) reutilizable.
 *
 * @param navController El controlador de navegación que gestiona el cambio entre pantallas.
 */
@Composable
fun BottomMenuBar(navController: NavHostController) {
    // 1. OBTENER EL ESTADO ACTUAL DE LA NAVEGACIÓN
    // 'navBackStackEntry' nos da acceso a la pantalla que está actualmente en pantalla.
    // Usamos 'by' (delegado) para que la UI se refresque automáticamente al navegar.
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // 2. IDENTIFICAR EL DESTINO ACTUAL
    // Obtenemos la ruta actual para saber qué botón del menú debe aparecer como "seleccionado".
    val currentDestination = navBackStackEntry?.destination

    // Contenedor principal del menú inferior de Material Design 3
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        // Usamos recursos estáticos (dimens.xml) para la elevación (sombra)
        tonalElevation = dimensionResource(id = R.dimen.card_elevation)
    ) {

        // --- OPCIÓN 1: BITÁCORA ---
        NavigationBarItem(
            // Icono con soporte para lectura de derecha a izquierda (AutoMirrored)
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    contentDescription = stringResource(id = R.string.nav_bitacora)
                )
            },
            // Etiqueta de texto obtenida de strings.xml
            label = { Text(stringResource(id = R.string.nav_bitacora)) },

            // LÓGICA DE SELECCIÓN:
            // El botón brilla (está seleccionado) si la ruta actual coincide con la ruta definida en Screen.List.
            // Usamos 'hierarchy' para que funcione incluso si hay sub-rutas dentro de esta sección.
            selected = currentDestination?.hierarchy?.any { it.route == Screen.List.route } == true,

            onClick = {
                // LÓGICA DE NAVEGACIÓN PROFESIONAL:
                navController.navigate(Screen.List.route) {
                    // popUpTo: Limpia el historial hasta el inicio para evitar acumular pantallas.
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true // Guarda el scroll y estado de la pantalla que dejas.
                    }
                    // launchSingleTop: Evita abrir la misma pantalla varias veces si presionas el botón repetidamente.
                    launchSingleTop = true
                    // restoreState: Recupera el estado guardado al regresar a esta pantalla.
                    restoreState = true
                }
            }
        )

        // --- OPCIÓN 2: CATÁLOGO ---
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.nav_catalog)
                )
            },
            label = { Text(stringResource(id = R.string.nav_catalog)) },
            selected = currentDestination?.hierarchy?.any { it.route == Screen.Catalog.route } == true,
            onClick = {
                navController.navigate(Screen.Catalog.route) {
                    // Aplicamos la misma lógica de navegación limpia
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}