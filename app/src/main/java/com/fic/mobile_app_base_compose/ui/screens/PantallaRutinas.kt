package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fic.mobile_app_base_compose.SesionUsuario
import com.fic.mobile_app_base_compose.data.local.FitmachBaseDatos
import com.fic.mobile_app_base_compose.data.model.Rutina
import com.fic.mobile_app_base_compose.data.repository.RutinaRepository
import com.fic.mobile_app_base_compose.viewmodel.RutinaUiState
import com.fic.mobile_app_base_compose.viewmodel.RutinaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRutinas(
    onVolver: () -> Unit,
    onCompartirQR: (Int) -> Unit = {},
    onEscanearQR: () -> Unit = {},
    onVerDetalle: Function<Unit>
) {
    val contexto = LocalContext.current
    val db = remember { FitmachBaseDatos.obtenerInstancia(contexto) }
    val repository = remember { RutinaRepository(db.rutinaDao()) }
    val viewModel: RutinaViewModel = viewModel(factory = RutinaViewModel.Factory(repository))

    val rutinas by viewModel.rutinas.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val idUsuario = if (SesionUsuario.idUsuario != 0) SesionUsuario.idUsuario else 1

    LaunchedEffect(Unit) {
        viewModel.cargarRutinas(idUsuario)
    }

    var mostrarDialogoCrear by remember { mutableStateOf(false) }
    var rutinaAEditar by remember { mutableStateOf<Rutina?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Rutinas") },
                navigationIcon = {
                    TextButton(onClick = onVolver) { Text("Volver") }
                },
                actions = {
                    IconButton(onClick = onEscanearQR) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = "Escanear QR",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogoCrear = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva rutina")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (uiState is RutinaUiState.Error) {
                Text(
                    text = (uiState as RutinaUiState.Error).mensaje,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (rutinas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No tienes rutinas aún.\nPresiona + para crear una.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(rutinas) { rutina ->
                        TarjetaRutina(
                            rutina = rutina,
                            onEditar = { rutinaAEditar = rutina },
                            onEliminar = { viewModel.eliminarRutina(rutina) },
                            onCompartirQR = { onCompartirQR(rutina.idRutina) }
                        )
                    }
                }
            }
        }
    }

    if (mostrarDialogoCrear) {
        DialogoRutina(
            titulo = "Nueva Rutina",
            onConfirmar = { nombre, descripcion, nivel, duracion ->
                viewModel.guardarRutina(nombre, descripcion, nivel, duracion, idUsuario)
                mostrarDialogoCrear = false
            },
            onCancelar = { mostrarDialogoCrear = false }
        )
    }

    rutinaAEditar?.let { rutina ->
        DialogoRutina(
            titulo = "Editar Rutina",
            nombreInicial = rutina.nombre,
            descripcionInicial = rutina.descripcion,
            nivelInicial = rutina.nivel,
            duracionInicial = rutina.duracionMinutos.toString(),
            onConfirmar = { nombre, descripcion, nivel, duracion ->
                viewModel.actualizarRutina(
                    rutina.copy(
                        nombre = nombre,
                        descripcion = descripcion,
                        nivel = nivel,
                        duracionMinutos = duracion
                    )
                )
                rutinaAEditar = null
            },
            onCancelar = { rutinaAEditar = null }
        )
    }
}

@Composable
fun TarjetaRutina(
    rutina: Rutina,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    onCompartirQR: () -> Unit = {}
) {
    val (colorNivel, emojiNivel) = when (rutina.nivel) {
        "Principiante" -> Pair(MaterialTheme.colorScheme.tertiary, "🟢")
        "Intermedio"   -> Pair(MaterialTheme.colorScheme.secondary, "🟡")
        "Avanzado"     -> Pair(MaterialTheme.colorScheme.error, "🔴")
        else           -> Pair(MaterialTheme.colorScheme.primary, "⚪")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        color = colorNivel,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = rutina.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (rutina.descripcion.isNotBlank()) {
                        Text(
                            text = rutina.descripcion,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = colorNivel.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "$emojiNivel ${rutina.nivel}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = colorNivel,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "⏱ ${rutina.duracionMinutos} min",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        if (rutina.esPublica) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "🌐 Pública",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    IconButton(onClick = onCompartirQR, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.QrCode,
                            contentDescription = "Compartir QR",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onEditar, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onEliminar, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoRutina(
    titulo: String,
    nombreInicial: String = "",
    descripcionInicial: String = "",
    nivelInicial: String = "Principiante",
    duracionInicial: String = "30",
    onConfirmar: (String, String, String, Int) -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by remember { mutableStateOf(nombreInicial) }
    var descripcion by remember { mutableStateOf(descripcionInicial) }
    var nivel by remember { mutableStateOf(nivelInicial) }
    var duracion by remember { mutableStateOf(duracionInicial) }

    val niveles = listOf("Principiante", "Intermedio", "Avanzado")
    var expandirNivel by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expandirNivel,
                    onExpandedChange = { expandirNivel = !expandirNivel }
                ) {
                    OutlinedTextField(
                        value = nivel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Nivel") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirNivel) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandirNivel,
                        onDismissRequest = { expandirNivel = false }
                    ) {
                        niveles.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = { nivel = opcion; expandirNivel = false }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = duracion,
                    onValueChange = { if (it.all { c -> c.isDigit() }) duracion = it },
                    label = { Text("Duración (minutos)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirmar(nombre, descripcion, nivel, duracion.toIntOrNull() ?: 30)
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}