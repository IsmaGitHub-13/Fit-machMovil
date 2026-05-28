package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfil(
    onVolver: () -> Unit,
    onNavegarAAmigos: () -> Unit,
    onNavegarAPlanProgresion: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onNavegarAAmigos,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mis Amigos")
            }

            Button(
                onClick = onNavegarAPlanProgresion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Plan de Progresión")
            }
        }
    }
}