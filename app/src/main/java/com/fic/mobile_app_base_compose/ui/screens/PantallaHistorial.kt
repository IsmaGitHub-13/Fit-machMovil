package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fic.mobile_app_base_compose.data.local.FitmachBaseDatos
import com.fic.mobile_app_base_compose.data.model.LogActividad
import com.fic.mobile_app_base_compose.data.repository.LogActividadRepository
import com.fic.mobile_app_base_compose.viewmodel.LogActividadViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHistorial(
    onVolver: () -> Unit,
    idUsuario: Int = 1
) {
    val contexto = LocalContext.current

    val viewModel: LogActividadViewModel = viewModel(
        factory = LogActividadViewModel.Factory(
            repository = LogActividadRepository(
                FitmachBaseDatos.obtenerInstancia(contexto).logActividadDao()
            )
        )
    )

    val historial by viewModel.obtenerHistorial(idUsuario).collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Actividad") },
                navigationIcon = {
                    TextButton(onClick = onVolver) {
                        Text("Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (historial.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no tienes actividad registrada",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(historial) { log ->
                    CardHistorial(log)
                }
            }
        }
    }
}

@Composable
private fun CardHistorial(log: LogActividad) {
    val fecha = remember(log.fecha) {
        SimpleDateFormat("dd MMM yyyy", Locale("es", "MX")).format(Date(log.fecha))
    }

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
                    text = "Rutina #${log.idRutina}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Duración: ${log.duracionRealMinutos} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (log.calificacion > 0) {
                    Text(
                        text = "Calificación: ${"★".repeat(log.calificacion)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = fecha,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}