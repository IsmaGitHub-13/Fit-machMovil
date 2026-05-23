package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.fic.mobile_app_base_compose.R
import com.fic.mobile_app_base_compose.ui.navigation.Screen
import com.fic.mobile_app_base_compose.ui.components.BottomMenuBar

/**
 * Pantalla principal que muestra los hallazgos registrados por el usuario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.nav_bitacora)) }
            )
        },
        bottomBar = { BottomMenuBar(navController) },

        // Botón de Acción Flotante (FAB) para agregar nuevos registros
        floatingActionButton = {
            FloatingActionButton(
                // Al hacer clic, navegamos a la pantalla del Formulario
                onClick = { navController.navigate(Screen.Form.route) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.nav_bitacora)
                )
            }
        }
    ) { paddingValues ->
        // Contenedor centrado para mostrar mensaje de lista vacía
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.empty_list),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}