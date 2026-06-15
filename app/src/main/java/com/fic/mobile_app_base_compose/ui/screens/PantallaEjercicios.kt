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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fic.mobile_app_base_compose.R
import com.fic.mobile_app_base_compose.SesionUsuario
import com.fic.mobile_app_base_compose.data.local.FitmachBaseDatos
import com.fic.mobile_app_base_compose.data.model.Rutina
import com.fic.mobile_app_base_compose.data.repository.RutinaRepository
import com.fic.mobile_app_base_compose.viewmodel.RutinaViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.Info


data class InfoEjercicio(val musculo: String, val tips: List<String>)

private val INFO_EJERCICIOS = mapOf(
    "Press Banca" to InfoEjercicio("Pecho (pectoral mayor)", listOf(
        "También trabaja hombros y tríceps como músculos secundarios",
        "Mantén los pies firmes en el piso y los omóplatos retraídos",
        "Controla el descenso de la barra, evita rebotarla en el pecho"
    )),
    "Peck Deck" to InfoEjercicio("Pecho (aislamiento)", listOf(
        "Ejercicio de aislamiento, ideal después de movimientos compuestos",
        "Mantén una ligera flexión en los codos durante todo el movimiento",
        "Controla la fase de regreso, no dejes que el peso te jale"
    )),
    "Press Inclinado" to InfoEjercicio("Pecho superior", listOf(
        "Enfatiza la parte superior del pectoral y el deltoides anterior",
        "Un ángulo de banco entre 15° y 30° es ideal",
        "Evita arquear demasiado la espalda baja"
    )),
    "Cruce de Poleas" to InfoEjercicio("Pecho (aislamiento)", listOf(
        "Excelente para definir y dar forma al pectoral",
        "Mantén una ligera inclinación hacia adelante",
        "Enfócate en apretar el pecho al final del movimiento"
    )),
    "Curl con Barra (Bícep)" to InfoEjercicio("Bíceps", listOf(
        "Movimiento básico para el desarrollo del bíceps braquial",
        "Evita usar el impulso de la espalda para levantar el peso",
        "Mantén los codos pegados al torso durante todo el ejercicio"
    )),
    "Extensión en Polea Alta (Trícep)" to InfoEjercicio("Tríceps", listOf(
        "Aísla efectivamente la cabeza lateral del tríceps",
        "Mantén los codos fijos cerca del cuerpo",
        "Extiende completamente el brazo sin bloquear de golpe"
    )),
    "Press Militar con Barra (Deltoides Anterior)" to InfoEjercicio("Hombro (deltoides anterior)", listOf(
        "Ejercicio compuesto que también activa el core para estabilidad",
        "Mantén la barra en línea recta sobre la cabeza al finalizar",
        "Evita arquear excesivamente la espalda baja"
    )),
    "Elevaciones Laterales (Deltoides Medio)" to InfoEjercicio("Hombro (deltoides medio)", listOf(
        "Ideal para dar amplitud y forma redondeada al hombro",
        "Usa pesos moderados, prioriza la técnica sobre la carga",
        "Sube los brazos hasta la altura de los hombros, no más arriba"
    )),
    "Pájaros con Mancuernas (Deltoides Posterior)" to InfoEjercicio("Hombro (deltoides posterior)", listOf(
        "Ayuda a equilibrar el desarrollo del hombro y mejorar la postura",
        "Inclina el torso hacia adelante manteniendo la espalda recta",
        "Evita usar impulso, el movimiento debe ser controlado"
    )),
    "Curl de Muñeca con Barra (Antebrazo)" to InfoEjercicio("Antebrazo", listOf(
        "Fortalece el agarre, útil para otros ejercicios de tracción",
        "Realiza el movimiento solo con la muñeca, sin mover el codo",
        "Usa pesos ligeros y rangos completos de movimiento"
    )),
    "Dominadas Agarre Ancho (Amplitud)" to InfoEjercicio("Espalda (dorsal ancho)", listOf(
        "Excelente para desarrollar amplitud de espalda",
        "Inicia el movimiento llevando los codos hacia abajo y atrás",
        "Si es muy difícil, usa una banda de asistencia"
    )),
    "Jalón al Pecho (Amplitud)" to InfoEjercicio("Espalda (dorsal ancho)", listOf(
        "Alternativa a las dominadas, permite ajustar el peso",
        "Lleva la barra hacia la parte superior del pecho, no al cuello",
        "Evita inclinarte demasiado hacia atrás para generar impulso"
    )),
    "Remo con Barra (Longitud)" to InfoEjercicio("Espalda media", listOf(
        "Trabaja el grosor y densidad de la espalda media",
        "Mantén la espalda recta y el core activado",
        "Lleva la barra hacia el abdomen, apretando los omóplatos"
    )),
    "Remo en Polea Baja (Longitud)" to InfoEjercicio("Espalda media", listOf(
        "Permite controlar mejor el rango de movimiento que el remo libre",
        "Mantén el torso fijo, el movimiento viene de los brazos y espalda",
        "Aprieta los omóplatos al final de cada repetición"
    )),
    "Sentadilla (Cuádricep)" to InfoEjercicio("Pierna (cuádriceps)", listOf(
        "Ejercicio fundamental que también activa glúteos y core",
        "Mantén las rodillas alineadas con los pies, sin colapsar hacia dentro",
        "Baja hasta que los muslos queden paralelos al piso o más"
    )),
    "Aducción en Máquina (Aductor)" to InfoEjercicio("Pierna (aductores)", listOf(
        "Trabaja la parte interna del muslo, complementa a la sentadilla",
        "Movimiento controlado, sin usar impulso",
        "Útil para estabilidad de cadera en otros ejercicios"
    )),
    "Curl Femoral Tumbado (Femoral)" to InfoEjercicio("Pierna (isquiotibiales)", listOf(
        "Equilibra el desarrollo entre cuádriceps e isquiotibiales",
        "Evita levantar la cadera del banco durante el movimiento",
        "Controla tanto la subida como la bajada del peso"
    )),
    "Elevación de Talones (Pantorrilla)" to InfoEjercicio("Pantorrilla (gastrocnemio)", listOf(
        "Realiza el movimiento completo, desde estiramiento hasta contracción",
        "Una pausa de un segundo arriba mejora la activación muscular",
        "Puede hacerse con peso corporal o con carga adicional"
    )),
    "Hip Thrust (Glúteo)" to InfoEjercicio("Glúteo mayor", listOf(
        "Uno de los mejores ejercicios para activación y fuerza de glúteo",
        "Aprieta los glúteos con fuerza en la parte alta del movimiento",
        "Mantén la barbilla ligeramente hacia el pecho para proteger el cuello"
    ))
)

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

    var ejercicioSeleccionado by remember { mutableStateOf<String?>(null) }
    var ejercicioInfo by remember { mutableStateOf<String?>(null) }
    var rutinaSeleccionada by remember { mutableStateOf<Rutina?>(null) }
    var mostrarConfirmacion by remember { mutableStateOf(false) }

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
                                        ejercicioSeleccionado = ejercicioSeleccionado
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
                    // Por ahora solo cierra — la lógica de RutinaEjercicio se conecta después
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
                onAgregar = { ejercicioInfo = it }
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
                onAgregar = { ejercicioInfo = it }
            )

            SeccionEjercicios(
                titulo = stringResource(R.string.seccion_espalda),
                ejercicios = listOf(
                    stringResource(R.string.ejercicio_dominadas),
                    stringResource(R.string.ejercicio_jalon),
                    stringResource(R.string.ejercicio_remo_barra),
                    stringResource(R.string.ejercicio_remo_polea)
                ),
                onAgregar = { ejercicioInfo = it }
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
                onAgregar = { ejercicioInfo = it }
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
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Información del ejercicio"
                            )
                        }
                    }
                }
            }
        }
    }
}