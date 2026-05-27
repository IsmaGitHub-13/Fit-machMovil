package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PantallaEjercicios(onVolver: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Ejercicios",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp)
        )

        // Boton Pecho
        var pechoExpandido by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { pechoExpandido = !pechoExpandido },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = MaterialTheme.shapes.large
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (pechoExpandido) "∧" else "∨",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "Pecho",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            AnimatedVisibility(visible = pechoExpandido) {
                Column(modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)) {
                    Text("Press Banca", style = MaterialTheme.typography.bodyMedium)
                    Text("Peck Deck", style = MaterialTheme.typography.bodyMedium)
                    Text("Press Inclinado", style = MaterialTheme.typography.bodyMedium)
                    Text("Cruce de Poleas", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onVolver,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Volver")
        }
    }
}