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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fic.mobile_app_base_compose.R

@Composable
fun PantallaEjercicios(onVolver: () -> Unit) {

    var ejercicioSeleccionado by remember { mutableStateOf<String?>(null) }

    if (ejercicioSeleccionado != null) {
        AlertDialog(
            onDismissRequest = { ejercicioSeleccionado = null },
            title = { Text(stringResource(R.string.dialog_agregar_rutina_titulo)) },
            text = {
                Column {
                    Text(
                        text = ejercicioSeleccionado!!,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.dialog_sin_rutinas),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { ejercicioSeleccionado = null }) {
                    Text(stringResource(R.string.btn_confirmar))
                }
            },
            dismissButton = {
                TextButton(onClick = { ejercicioSeleccionado = null }) {
                    Text(stringResource(R.string.btn_cancelar))
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
            text = stringResource(R.string.titulo_ejercicios),
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
            SeccionEjercicios(
                titulo = stringResource(R.string.seccion_pecho),
                ejercicios = listOf(
                    stringResource(R.string.ejercicio_press_banca),
                    stringResource(R.string.ejercicio_peck_deck),
                    stringResource(R.string.ejercicio_press_inclinado),
                    stringResource(R.string.ejercicio_cruce_poleas)
                ),
                onAgregar = { ejercicioSeleccionado = it }
            )

            SeccionEjercicios(
                titulo = stringResource(R.string.seccion_brazo),
                ejercicios = listOf(
                    stringResource(R.string.ejercicio_curl_barra),
                    stringResource(R.string.ejercicio_extension_polea),
                    stringResource(R.string.ejercicio_press_militar),
                    stringResource(R.string.ejercicio_elevaciones_laterales),
                    stringResource(R.string.ejercicio_pajaros),
                    stringResource(R.string.ejercicio_curl_muneca)
                ),
                onAgregar = { ejercicioSeleccionado = it }
            )

            SeccionEjercicios(
                titulo = stringResource(R.string.seccion_espalda),
                ejercicios = listOf(
                    stringResource(R.string.ejercicio_dominadas),
                    stringResource(R.string.ejercicio_jalon),
                    stringResource(R.string.ejercicio_remo_barra),
                    stringResource(R.string.ejercicio_remo_polea)
                ),
                onAgregar = { ejercicioSeleccionado = it }
            )

            SeccionEjercicios(
                titulo = stringResource(R.string.seccion_pierna),
                ejercicios = listOf(
                    stringResource(R.string.ejercicio_sentadilla),
                    stringResource(R.string.ejercicio_aduccion),
                    stringResource(R.string.ejercicio_curl_femoral),
                    stringResource(R.string.ejercicio_elevacion_talones),
                    stringResource(R.string.ejercicio_hip_thrust)
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
            Text(stringResource(R.string.btn_volver))
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
                                contentDescription = stringResource(R.string.btn_agregar_rutina)
                            )
                        }
                    }
                }
            }
        }
    }
}