package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.fic.mobile_app_base_compose.R

/**
 * Pantalla de formulario para capturar un nuevo hallazgo.
 * No incluye menú inferior para evitar que el usuario pierda su progreso.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(navController: NavHostController) {
    // ESTADOS LOCALES: Guardan lo que el usuario escribe en tiempo real
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.title_new_record)) },
                // Botón para volver atrás en la pila de navegación
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // Layout vertical con espaciado uniforme
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(dimensionResource(R.dimen.margin_large)) // Margen de 24.dp desde dimens
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
        ) {
            // Campo de texto para el título
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.titulo_login)) },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo de texto multilínea para descripción
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.empty_list)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Espaciador flexible que empuja el botón al final de la pantalla
            Spacer(modifier = Modifier.weight(1f))

            // Botón de guardar (Temporalmente solo cierra la pantalla)
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.button_height_standard))
            ) {
                Text(stringResource(R.string.btn_save))
            }
        }
    }
}