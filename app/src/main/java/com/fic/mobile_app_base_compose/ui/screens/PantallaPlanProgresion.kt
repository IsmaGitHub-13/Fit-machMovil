package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fic.mobile_app_base_compose.data.local.FitmachBaseDatos
import com.fic.mobile_app_base_compose.data.model.SemanaProgresion
import com.fic.mobile_app_base_compose.data.repository.PlanProgresionRepository
import com.fic.mobile_app_base_compose.viewmodel.PlanProgresionViewModel
import com.fic.mobile_app_base_compose.viewmodel.PlanUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPlanProgresion(onVolver: () -> Unit) {
    val contexto = LocalContext.current
    val viewModel: PlanProgresionViewModel = viewModel(
        factory = PlanProgresionViewModel.Factory(
            PlanProgresionRepository(FitmachBaseDatos.obtenerInstancia(contexto).planProgresionDao())
        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var ejercicio by remember { mutableStateOf("") }
    var rmActual by remember { mutableStateOf("") }
    var objetivoRm by remember { mutableStateOf("") }
    var nivel by remember { mutableStateOf("principiante") }
    var expandirNivel by remember { mutableStateOf(false) }

    val niveles = listOf("principiante", "intermedio", "avanzado")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Plan de Progresión", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetear()
                        onVolver()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->

        when (val state = uiState) {
            is PlanUiState.PlanGenerado -> {
                // Mostrar plan generado
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "✅ Plan generado",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${state.semanas.size} semanas · $nivel",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        itemsIndexed(state.semanas) { idx, semana ->
                            TarjetaSemana(semana = semana, indice = idx)
                        }
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { viewModel.resetear() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Crear nuevo plan")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

            else -> {
                // Formulario de creación
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Info card
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("¿Qué es el 1RM?", fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "La repetición máxima (1RM) es el peso máximo que puedes levantar " +
                                        "una sola vez con buena técnica. Ingresa tu 1RM actual y tu objetivo " +
                                        "para generar un plan de progresión personalizado.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedTextField(
                        value = ejercicio,
                        onValueChange = { ejercicio = it },
                        label = { Text("Ejercicio (ej: Press de Banca)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = uiState is PlanUiState.Error
                    )

                    OutlinedTextField(
                        value = rmActual,
                        onValueChange = { rmActual = it },
                        label = { Text("1RM actual (kg)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        isError = uiState is PlanUiState.Error
                    )

                    OutlinedTextField(
                        value = objetivoRm,
                        onValueChange = { objetivoRm = it },
                        label = { Text("Objetivo 1RM (kg)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        isError = uiState is PlanUiState.Error
                    )

                    // Selector de nivel
                    ExposedDropdownMenuBox(
                        expanded = expandirNivel,
                        onExpandedChange = { expandirNivel = !expandirNivel }
                    ) {
                        OutlinedTextField(
                            value = nivel.replaceFirstChar { it.uppercase() },
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
                                    text = { Text(opcion.replaceFirstChar { it.uppercase() }) },
                                    onClick = { nivel = opcion; expandirNivel = false }
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = uiState is PlanUiState.Error) {
                        Text(
                            text = (uiState as? PlanUiState.Error)?.mensaje ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.generarPlan(
                                idUsuario = 1,
                                ejercicio = ejercicio,
                                rmActual = rmActual.toFloatOrNull() ?: 0f,
                                objetivoRm = objetivoRm.toFloatOrNull() ?: 0f,
                                nivel = nivel
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = uiState !is PlanUiState.Cargando
                    ) {
                        if (uiState is PlanUiState.Cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Generar plan de progresión")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun TarjetaSemana(semana: SemanaProgresion, indice: Int) {
    val esDeload = semana.repeticiones >= 8 && semana.series <= 2
    val containerColor = when {
        esDeload -> MaterialTheme.colorScheme.surfaceVariant
        indice % 4 == 0 -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Semana ${semana.numeroSemana}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${semana.series} series × ${semana.repeticiones} reps",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (esDeload) {
                    Text("Semana de descarga 🔄",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "${semana.pesoKg} kg",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

