package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fic.mobile_app_base_compose.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPanel(onNavegarARutinas: () -> Unit, onNavegarAHistorial: () -> Unit, onCerrarSesion: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { valoresRelleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(valoresRelleno)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.bienvenida_usuario),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = stringResource(id = R.string.subtitulo_panel),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavegarARutinas,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(stringResource(id = R.string.menu_rutinas))
            }
            Button(
                onClick = onNavegarAHistorial,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Historial de Ejercicios")
            }
            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = onCerrarSesion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.btn_cerrar_sesion), color = MaterialTheme.colorScheme.error)
            }
        }
    }
}