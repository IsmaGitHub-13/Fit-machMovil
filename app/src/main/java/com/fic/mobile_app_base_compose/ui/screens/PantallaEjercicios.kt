package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PantallaEjercicios(onVolver: () -> Unit) {

    var ejercicioSeleccionado by remember { mutableStateOf<String?>(null) }

    if (ejercicioSeleccionado != null) {
        AlertDialog(
            onDismissRequest = { ejercicioSeleccionado = null },
            title = { Text("¿Deseas agregar este ejercicio a tu rutina?") },
            text = {
                Column {
                    Text(
                        text = ejercicioSeleccionado!!,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No tienes rutinas creadas aún.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { ejercicioSeleccionado = null }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { ejercicioSeleccionado = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

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
                Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 12.dp)) {
                    listOf("Press Banca", "Peck Deck", "Press Inclinado", "Cruce de Poleas").forEach { ejercicio ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = ejercicio,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { ejercicioSeleccionado = ejercicio }) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Agregar a rutina"
                                )
                            }
                        }
                    }
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