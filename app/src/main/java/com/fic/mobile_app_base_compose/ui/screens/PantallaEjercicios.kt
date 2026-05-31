package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // --- PECHO ---
            SeccionEjercicios(
                titulo = "Pecho",
                ejercicios = listOf(
                    "Press Banca",
                    "Peck Deck",
                    "Press Inclinado",
                    "Cruce de Poleas"
                ),
                onAgregar = { ejercicioSeleccionado = it }
            )

            // --- BRAZO ---
            SeccionEjercicios(
                titulo = "Brazo",
                ejercicios = listOf(
                    "Curl con Barra (Bícep)",
                    "Extensión en Polea Alta (Trícep)",
                    "Press Militar con Barra (Deltoides Anterior)",
                    "Elevaciones Laterales (Deltoides Medio)",
                    "Pájaros con Mancuernas (Deltoides Posterior)",
                    "Curl de Muñeca con Barra (Antebrazo)"
                ),
                onAgregar = { ejercicioSeleccionado = it }
            )

            // --- ESPALDA ---
            SeccionEjercicios(
                titulo = "Espalda",
                ejercicios = listOf(
                    "Dominadas Agarre Ancho (Amplitud)",
                    "Jalón al Pecho (Amplitud)",
                    "Remo con Barra (Longitud)",
                    "Remo en Polea Baja (Longitud)"
                ),
                onAgregar = { ejercicioSeleccionado = it }
            )

            // --- PIERNA ---
            SeccionEjercicios(
                titulo = "Pierna",
                ejercicios = listOf(
                    "Sentadilla (Cuádricep)",
                    "Aducción en Máquina (Aductor)",
                    "Curl Femoral Tumbado (Femoral)",
                    "Elevación de Talones (Pantorrilla)",
                    "Hip Thrust (Glúteo)"
                ),
                onAgregar = { ejercicioSeleccionado = it }
            )
        }

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

@Composable
fun SeccionEjercicios(
    titulo: String,
    ejercicios: List<String>,
    onAgregar: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expandido = !expandido },
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
                text = if (expandido) "∧" else "∨",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(end = 12.dp)
            )
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium
            )
        }
        AnimatedVisibility(visible = expandido) {
            Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 12.dp)) {
                ejercicios.forEach { ejercicio ->
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
                        IconButton(onClick = { onAgregar(ejercicio) }) {
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
}