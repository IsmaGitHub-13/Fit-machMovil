package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class RegistroEjercicio(
    val ejercicio: String,
    val series: Int,
    val repeticiones: Int,
    val fecha: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHistorial(onVolver: () -> Unit) {

    val historial = listOf(
        RegistroEjercicio("Press de Banca", 4, 12, "24 May 2026"),
        RegistroEjercicio("Sentadilla", 3, 15, "23 May 2026"),
        RegistroEjercicio("Peso Muerto", 4, 10, "22 May 2026"),
        RegistroEjercicio("Curl de Bíceps", 3, 12, "21 May 2026"),
        RegistroEjercicio("Plancha", 3, 60, "20 May 2026")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Ejercicios") },
                navigationIcon = {
                    TextButton(onClick = onVolver) {
                        Text("Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(historial) { registro ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = registro.ejercicio,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${registro.series} series × ${registro.repeticiones} reps",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = registro.fecha,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}