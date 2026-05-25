package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fic.mobile_app_base_compose.data.local.FitmachBaseDatos
import com.fic.mobile_app_base_compose.data.model.Rutina
import com.fic.mobile_app_base_compose.data.repository.RutinaRepository
import com.fic.mobile_app_base_compose.viewmodel.RutinaUiState
import com.fic.mobile_app_base_compose.viewmodel.RutinaViewModel

// ID de usuario fijo para pruebas (cuando haya login real se cambia)
private const val ID_USUARIO_PRUEBA = 1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRutinas(onVolver: () -> Unit) {

    // --- Setup del ViewModel ---
    val contexto = LocalContext.current
    val db = remember { FitmachBaseDatos.obtenerInstancia(contexto) }
    val repository = remember { RutinaRepository(db.rutinaDao()) }
    val viewModel: RutinaViewModel = viewModel(factory = RutinaViewModel.Factory(repository))

    val rutinas by viewModel.rutinas.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // Carga las rutinas al abrir la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarRutinas(ID_USUARIO_PRUEBA)
    }

    // --- Estados para los diálogos ---
    var mostrarDialogoCrear by remember { mutableStateOf(false) }
    var rutinaAEditar by remember { mutableStateOf<Rutina?>(null) }

    // --- Pantalla principal ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Rutinas") },
                navigationIcon = {
                    TextButton(onClick = onVolver) { Text("Volver") }
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

            // Mensaje de error
            if (uiState is RutinaUiState.Error) {
                Text(
                    text = (uiState as RutinaUiState.Error).mensaje,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Lista vacía
            if (rutinas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No tienes rutinas aún.\nPresiona + para crear una.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Lista de rutinas
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(rutinas) { rutina ->
                        TarjetaRutina(
                            rutina = rutina,
                            onEditar = { rutinaAEditar = rutina },
                            onEliminar = { viewModel.eliminarRutina(rutina) }
                        )
                    }
                }
            }
        }
    }

    // --- Diálogo: Crear rutina ---
    if (mostrarDialogoCrear) {
        DialogoRutina(
            titulo = "Nueva Rutina",
            onConfirmar = { nombre, descripcion, nivel, duracion ->
                viewModel.guardarRutina(nombre, descripcion, nivel, duracion, ID_USUARIO_PRUEBA)
                mostrarDialogoCrear = false
            },
            onCancelar = { mostrarDialogoCrear = false }
        )
    }

    // --- Diálogo: Editar rutina ---
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

// --- Tarjeta de cada rutina en la lista ---
@Composable
fun TarjetaRutina(rutina: Rutina, onEditar: () -> Unit, onEliminar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = rutina.nombre, style = MaterialTheme.typography.titleMedium)
                if (rutina.descripcion.isNotBlank()) {
                    Text(
                        text = rutina.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Nivel: ${rutina.nivel}  •  ${rutina.duracionMinutos} min",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

// --- Diálogo reutilizable para crear y editar ---
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

                // Selector de nivel
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