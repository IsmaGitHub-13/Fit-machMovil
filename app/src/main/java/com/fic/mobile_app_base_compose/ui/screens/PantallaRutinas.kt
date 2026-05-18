package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fic.mobile_app_base_compose.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRutinas(onVolver: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.titulo_rutinas)) },
                navigationIcon = {
                    TextButton(onClick = onVolver) {
                        Text(text = stringResource(id = R.string.btn_volver))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Aquí se gestionarán tus rutinas de FitMatch",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}