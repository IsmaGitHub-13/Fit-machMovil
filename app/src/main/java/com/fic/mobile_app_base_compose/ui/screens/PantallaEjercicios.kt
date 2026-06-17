package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fic.mobile_app_base_compose.R
import com.fic.mobile_app_base_compose.SesionUsuario
import com.fic.mobile_app_base_compose.data.local.FitmachBaseDatos
import com.fic.mobile_app_base_compose.data.model.Rutina
import com.fic.mobile_app_base_compose.data.repository.RutinaRepository
import com.fic.mobile_app_base_compose.viewmodel.RutinaViewModel

data class InfoEjercicio(val musculo: String, val tips: String)

@Composable
fun PantallaEjercicios(onVolver: () -> Unit) {

    val contexto = LocalContext.current
    val db = remember { FitmachBaseDatos.obtenerInstancia(contexto) }
    val rutinaRepository = remember { RutinaRepository(db.rutinaDao()) }
    val rutinaViewModel: RutinaViewModel = viewModel(
        factory = RutinaViewModel.Factory(rutinaRepository)
    )

    val rutinas by rutinaViewModel.rutinas.collectAsState()
    val idUsuario = if (SesionUsuario.idUsuario != 0) SesionUsuario.idUsuario else 1

    LaunchedEffect(Unit) {
        rutinaViewModel.cargarRutinas(idUsuario)
    }

    // Mapa de info usando stringResource
    val infoEjercicios = mapOf(
        stringResource(R.string.ejercicio_press_banca) to InfoEjercicio(
            stringResource(R.string.info_press_banca_musculo),
            stringResource(R.string.info_press_banca_tips)
        ),
        stringResource(R.string.ejercicio_peck_deck) to InfoEjercicio(
            stringResource(R.string.info_peck_deck_musculo),
            stringResource(R.string.info_peck_deck_tips)
        ),
        stringResource(R.string.ejercicio_press_inclinado) to InfoEjercicio(
            stringResource(R.string.info_press_inclinado_musculo),
            stringResource(R.string.info_press_inclinado_tips)
        ),
        stringResource(R.string.ejercicio_cruce_poleas) to InfoEjercicio(
            stringResource(R.string.info_cruce_poleas_musculo),
            stringResource(R.string.info_cruce_poleas_tips)
        ),
        stringResource(R.string.ejercicio_curl_barra) to InfoEjercicio(
            stringResource(R.string.info_curl_barra_musculo),
            stringResource(R.string.info_curl_barra_tips)
        ),
        stringResource(R.string.ejercicio_extension_polea) to InfoEjercicio(
            stringResource(R.string.info_extension_polea_musculo),
            stringResource(R.string.info_extension_polea_tips)
        ),
        stringResource(R.string.ejercicio_press_militar) to InfoEjercicio(
            stringResource(R.string.info_press_militar_musculo),
            stringResource(R.string.info_press_militar_tips)
        ),
        stringResource(R.string.ejercicio_elevaciones_laterales) to InfoEjercicio(
            stringResource(R.string.info_elevaciones_laterales_musculo),
            stringResource(R.string.info_elevaciones_laterales_tips)
        ),
        stringResource(R.string.ejercicio_pajaros) to InfoEjercicio(
            stringResource(R.string.info_pajaros_musculo),
            stringResource(R.string.info_pajaros_tips)
        ),
        stringResource(R.string.ejercicio_curl_muneca) to InfoEjercicio(
            stringResource(R.string.info_curl_muneca_musculo),
            stringResource(R.string.info_curl_muneca_tips)
        ),
        stringResource(R.string.ejercicio_dominadas) to InfoEjercicio(
            stringResource(R.string.info_dominadas_musculo),
            stringResource(R.string.info_dominadas_tips)
        ),
        stringResource(R.string.ejercicio_jalon) to InfoEjercicio(
            stringResource(R.string.info_jalon_musculo),
            stringResource(R.string.info_jalon_tips)
        ),
        stringResource(R.string.ejercicio_remo_barra) to InfoEjercicio(
            stringResource(R.string.info_remo_barra_musculo),
            stringResource(R.string.info_remo_barra_tips)
        ),
        stringResource(R.string.ejercicio_remo_polea) to InfoEjercicio(
            stringResource(R.string.info_remo_polea_musculo),
            stringResource(R.string.info_remo_polea_tips)
        ),
        stringResource(R.string.ejercicio_sentadilla) to InfoEjercicio(
            stringResource(R.string.info_sentadilla_musculo),
            stringResource(R.string.info_sentadilla_tips)
        ),
        stringResource(R.string.ejercicio_aduccion) to InfoEjercicio(
            stringResource(R.string.info_aduccion_musculo),
            stringResource(R.string.info_aduccion_tips)
        ),
        stringResource(R.string.ejercicio_curl_femoral) to InfoEjercicio(
            stringResource(R.string.info_curl_femoral_musculo),
            stringResource(R.string.info_curl_femoral_tips)
        ),
        stringResource(R.string.ejercicio_elevacion_talones) to InfoEjercicio(
            stringResource(R.string.info_elevacion_talones_musculo),
            stringResource(R.string.info_elevacion_talones_tips)
        ),
        stringResource(R.string.ejercicio_hip_thrust) to InfoEjercicio(
            stringResource(R.string.info_hip_thrust_musculo),
            stringResource(R.string.info_hip_thrust_tips)
        )
    )

    var ejercicioInfoSeleccionado by remember { mutableStateOf<String?>(null) }
    var ejercicioSeleccionado by remember { mutableStateOf<String?>(null) }
    var rutinaSeleccionada by remember { mutableStateOf<Rutina?>(null) }
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    // Diálogo — información del ejercicio
    ejercicioInfoSeleccionado?.let { nombreEjercicio ->
        val info = infoEjercicios[nombreEjercicio]
        AlertDialog(
            onDismissRequest = { ejercicioInfoSeleccionado = null },
            title = {
                Text(nombreEjercicio, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (info != null) {
                        // Músculo
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("💪", style = MaterialTheme.typography.titleMedium)
                                Column {
                                    Text(
                                        "Músculo principal",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        info.musculo,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                        // Tips
                        Text(
                            "Consejos:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        info.tips.split("\n").forEach { tip ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("•", color = MaterialTheme.colorScheme.primary)
                                Text(tip, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    } else {
                        Text("No hay información disponible para este ejercicio.")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { ejercicioInfoSeleccionado = null }) {
                    Text("Cerrar")
                }
            }
        )
    }

    // Diálogo — elegir rutina
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
                    if (rutinas.isEmpty()) {
                        Text(
                            text = stringResource(R.string.dialog_sin_rutinas),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "Selecciona una rutina:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        rutinas.forEach { rutina ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        rutinaSeleccionada = rutina
                                        mostrarConfirmacion = true
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (rutinaSeleccionada?.idRutina == rutina.idRutina)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = rutina.nombre,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            text = "${rutina.nivel} · ${rutina.duracionMinutos} min",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (rutinaSeleccionada != null) {
                            mostrarConfirmacion = true
                        } else {
                            ejercicioSeleccionado = null
                        }
                    },
                    enabled = rutinaSeleccionada != null || rutinas.isEmpty()
                ) {
                    Text(if (rutinas.isEmpty()) stringResource(R.string.btn_cancelar) else stringResource(R.string.btn_confirmar))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    ejercicioSeleccionado = null
                    rutinaSeleccionada = null
                }) {
                    Text(stringResource(R.string.btn_cancelar))
                }
            }
        )
    }

    // Diálogo — confirmación final
    if (mostrarConfirmacion && rutinaSeleccionada != null && ejercicioSeleccionado != null) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = { Text("¿Agregar ejercicio?") },
            text = {
                Text("Se agregará \"$ejercicioSeleccionado\" a la rutina \"${rutinaSeleccionada!!.nombre}\"")
            },
            confirmButton = {
                TextButton(onClick = {
                    mostrarConfirmacion = false
                    ejercicioSeleccionado = null
                    rutinaSeleccionada = null
                }) {
                    Text("Agregar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarConfirmacion = false
                    rutinaSeleccionada = null
                }) {
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
                onInfo = { ejercicioInfoSeleccionado = it },
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
                onInfo = { ejercicioInfoSeleccionado = it },
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
                onInfo = { ejercicioInfoSeleccionado = it },
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
                onInfo = { ejercicioInfoSeleccionado = it },
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
    onInfo: (String) -> Unit,
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
                        IconButton(onClick = { onInfo(ejercicio) }) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Información del ejercicio",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}