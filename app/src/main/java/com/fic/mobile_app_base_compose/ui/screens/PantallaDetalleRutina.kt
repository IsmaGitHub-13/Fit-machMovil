package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fic.mobile_app_base_compose.R
import com.fic.mobile_app_base_compose.SesionUsuario
import com.fic.mobile_app_base_compose.data.local.FitmachBaseDatos
import com.fic.mobile_app_base_compose.data.model.LogActividad
import com.fic.mobile_app_base_compose.data.model.RutinaEjercicio
import com.fic.mobile_app_base_compose.data.repository.LogActividadRepository
import com.fic.mobile_app_base_compose.data.repository.RutinaEjercicioRepository
import com.fic.mobile_app_base_compose.viewmodel.LogActividadViewModel
import com.fic.mobile_app_base_compose.viewmodel.RutinaEjercicioViewModel

// Ejercicios disponibles para agregar (del catálogo)
private val EJERCICIOS_CATALOGO = listOf(
    Pair(1, "Press de Banca"),
    Pair(2, "Sentadilla"),
    Pair(3, "Peso Muerto"),
    Pair(4, "Press Militar"),
    Pair(5, "Dominadas"),
    Pair(6, "Jalón al Pecho"),
    Pair(7, "Remo con Barra"),
    Pair(8, "Curl de Bíceps"),
    Pair(9, "Extensión de Tríceps"),
    Pair(10, "Elevaciones Laterales"),
    Pair(11, "Peck Deck"),
    Pair(12, "Hip Thrust"),
    Pair(13, "Curl Femoral"),
    Pair(14, "Elevación de Talones"),
    Pair(15, "Plancha")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleRutina(
    rutinaId: Int,
    nombreRutina: String,
    onVolver: () -> Unit
) {
    val contexto = LocalContext.current
    val db = FitmachBaseDatos.obtenerInstancia(contexto)

    val viewModel: RutinaEjercicioViewModel = viewModel(
        factory = RutinaEjercicioViewModel.Factory(
            RutinaEjercicioRepository(db.rutinaEjercicioDao())
        )
    )

    val logViewModel: LogActividadViewModel = viewModel(
        factory = LogActividadViewModel.Factory(
            LogActividadRepository(db.logActividadDao())
        )
    )

    val ejercicios by viewModel.ejercicios.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    var mostrarDialogo by remember { mutableStateOf(false) }
    var mostrarDialogoCompletar by remember { mutableStateOf(false) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }

    val mensajeRegistrada = stringResource(R.string.mensaje_rutina_registrada)

    LaunchedEffect(rutinaId) { viewModel.cargarEjercicios(rutinaId) }

    error?.let {
        LaunchedEffect(it) {
            kotlinx.coroutines.delay(2000)
            viewModel.limpiarError()
        }
    }

    mensajeExito?.let {
        LaunchedEffect(it) {
            kotlinx.coroutines.delay(2000)
            mensajeExito = null
        }
    }

    if (mostrarDialogo) {
        DialogoAgregarEjercicio(
            onAgregar = { idEjercicio, nombreEjercicio, series, reps, descanso ->
                viewModel.agregarEjercicio(rutinaId, idEjercicio, nombreEjercicio, series, reps, descanso)
                mostrarDialogo = false
            },
            onCancelar = { mostrarDialogo = false }
        )
    }

    if (mostrarDialogoCompletar) {
        DialogoCompletarRutina(
            onConfirmar = { duracion, calificacion ->
                logViewModel.registrarActividad(
                    LogActividad(
                        idUsuario = SesionUsuario.idUsuario,
                        idRutina = rutinaId,
                        duracionRealMinutos = duracion,
                        calificacion = calificacion
                    )
                )
                mostrarDialogoCompletar = false
                mensajeExito = mensajeRegistrada
            },
            onCancelar = { mostrarDialogoCompletar = false }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(nombreRutina, fontWeight = FontWeight.Bold, maxLines = 1)
                        Text(stringResource(R.string.detalle_rutina_subtitulo),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.btn_volver))
                    }
                }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (ejercicios.isNotEmpty()) {
                    ExtendedFloatingActionButton(
                        onClick = { mostrarDialogoCompletar = true },
                        icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                        text = { Text(stringResource(R.string.btn_completar)) }
                    )
                }
                FloatingActionButton(onClick = { mostrarDialogo = true }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.agregar_ejercicio_fab))
                }
            }
        },
        snackbarHost = {
            Column {
                error?.let { msg -> Snackbar(modifier = Modifier.padding(16.dp)) { Text(msg) } }
                mensajeExito?.let { msg ->
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) { Text(msg) }
                }
            }
        }
    ) { paddingValues ->
        if (ejercicios.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Text(stringResource(R.string.rutina_sin_ejercicios),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(stringResource(R.string.rutina_agregar_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Card(colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${ejercicios.size}", style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(stringResource(R.string.ejercicios_total),
                                    style = MaterialTheme.typography.labelSmall)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${ejercicios.sumOf { it.series }}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(stringResource(R.string.series_total),
                                    style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                items(ejercicios, key = { it.id }) { ejercicio ->
                    TarjetaEjercicioEnRutina(
                        ejercicio = ejercicio,
                        onEliminar = { viewModel.eliminarEjercicio(ejercicio.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun TarjetaEjercicioEnRutina(ejercicio: RutinaEjercicio, onEliminar: () -> Unit) {
    val nombreEjercicio = EJERCICIOS_CATALOGO.find { it.first == ejercicio.idEjercicio }?.second
        ?: "Ejercicio #${ejercicio.idEjercicio}"

    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text("${ejercicio.orden}", style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(nombreEjercicio, fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipInfo("${ejercicio.series} series")
                    ChipInfo("${ejercicio.repeticiones} reps")
                    if (ejercicio.descansoSegundos > 0)
                        ChipInfo("${ejercicio.descansoSegundos}s descanso")
                }
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.btn_eliminar),
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun ChipInfo(texto: String) {
    Surface(shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant) {
        Text(texto, style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoAgregarEjercicio(
    onAgregar: (Int, String, Int, Int, Int) -> Unit,
    onCancelar: () -> Unit
) {
    var ejercicioSeleccionado by remember { mutableStateOf(EJERCICIOS_CATALOGO.first()) }
    var series by remember { mutableStateOf("3") }
    var repeticiones by remember { mutableStateOf("12") }
    var descanso by remember { mutableStateOf("60") }
    var expandirEjercicio by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(stringResource(R.string.agregar_ejercicio_titulo)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expandirEjercicio,
                    onExpandedChange = { expandirEjercicio = !expandirEjercicio }
                ) {
                    OutlinedTextField(
                        value = ejercicioSeleccionado.second,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.campo_ejercicio_nombre)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirEjercicio) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandirEjercicio,
                        onDismissRequest = { expandirEjercicio = false }
                    ) {
                        EJERCICIOS_CATALOGO.forEach { ejercicio ->
                            DropdownMenuItem(
                                text = { Text(ejercicio.second) },
                                onClick = { ejercicioSeleccionado = ejercicio; expandirEjercicio = false }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = series,
                        onValueChange = { if (it.length <= 2) series = it },
                        label = { Text(stringResource(R.string.campo_series)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f), singleLine = true
                    )
                    OutlinedTextField(
                        value = repeticiones,
                        onValueChange = { if (it.length <= 3) repeticiones = it },
                        label = { Text(stringResource(R.string.campo_repeticiones)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f), singleLine = true
                    )
                }

                OutlinedTextField(
                    value = descanso,
                    onValueChange = { if (it.length <= 3) descanso = it },
                    label = { Text(stringResource(R.string.campo_descanso_seg)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onAgregar(
                    ejercicioSeleccionado.first,
                    ejercicioSeleccionado.second,
                    series.toIntOrNull() ?: 3,
                    repeticiones.toIntOrNull() ?: 12,
                    descanso.toIntOrNull() ?: 60
                )
            }) { Text(stringResource(R.string.btn_agregar)) }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text(stringResource(R.string.btn_cancelar)) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoCompletarRutina(
    onConfirmar: (Int, Int) -> Unit,
    onCancelar: () -> Unit
) {
    var duracion by remember { mutableStateOf("30") }
    var calificacion by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(stringResource(R.string.dialog_completar_titulo)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = duracion,
                    onValueChange = { if (it.length <= 3) duracion = it },
                    label = { Text(stringResource(R.string.campo_duracion_real)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(stringResource(R.string.label_calificacion))
                Row {
                    (1..5).forEach { estrella ->
                        IconButton(onClick = { calificacion = estrella }) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (estrella <= calificacion) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirmar(duracion.toIntOrNull() ?: 30, calificacion)
            }) { Text(stringResource(R.string.btn_guardar)) }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text(stringResource(R.string.btn_cancelar)) }
        }
    )
}