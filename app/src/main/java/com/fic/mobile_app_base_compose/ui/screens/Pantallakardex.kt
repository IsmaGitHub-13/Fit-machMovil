package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fic.mobile_app_base_compose.R
import com.fic.mobile_app_base_compose.SesionUsuario
import com.fic.mobile_app_base_compose.data.local.FitmachBaseDatos
import com.fic.mobile_app_base_compose.data.model.LogActividad
import com.fic.mobile_app_base_compose.data.model.Rutina
import com.fic.mobile_app_base_compose.data.repository.LogActividadRepository
import com.fic.mobile_app_base_compose.data.repository.RutinaRepository
import com.fic.mobile_app_base_compose.viewmodel.LogActividadViewModel
import com.fic.mobile_app_base_compose.viewmodel.RutinaViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaKardex(onVolver: () -> Unit) {
    val contexto = LocalContext.current
    val db = FitmachBaseDatos.obtenerInstancia(contexto)

    val rutinaViewModel: RutinaViewModel = viewModel(
        key = "kardex_rutina",
        factory = RutinaViewModel.Factory(RutinaRepository(db.rutinaDao()))
    )
    val logViewModel: LogActividadViewModel = viewModel(
        key = "kardex_log",
        factory = LogActividadViewModel.Factory(LogActividadRepository(db.logActividadDao()))
    )

    val rutinas by rutinaViewModel.rutinas.collectAsStateWithLifecycle()
    val historial by logViewModel.obtenerHistorial(SesionUsuario.idUsuario).collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { rutinaViewModel.cargarRutinas(SesionUsuario.idUsuario) }

    // Estadísticas
    val totalSesiones = historial.size
    val totalMinutos = historial.sumOf { it.duracionRealMinutos }
    val promedioCalif = if (historial.any { it.calificacion > 0 })
        historial.filter { it.calificacion > 0 }.map { it.calificacion }.average() else 0.0

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.titulo_kardex), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.btn_volver))
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Header perfil
            item {
                Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(
                    Brush.horizontalGradient(colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )), shape = RoundedCornerShape(16.dp)
                ), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Person, contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(40.dp))
                        Text(SesionUsuario.nombre, style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                        Text("@${SesionUsuario.nombreUsuario}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    }
                }
            }

            // Estadísticas generales
            item {
                Text(stringResource(R.string.kardex_seccion_estadisticas),
                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TarjetaEstadistica(modifier = Modifier.weight(1f),
                        icono = Icons.Default.FitnessCenter,
                        valor = "$totalSesiones",
                        etiqueta = stringResource(R.string.label_sesiones))
                    TarjetaEstadistica(modifier = Modifier.weight(1f),
                        icono = Icons.Default.Timer,
                        valor = "$totalMinutos",
                        etiqueta = stringResource(R.string.label_minutos_totales_kardex))
                    TarjetaEstadistica(modifier = Modifier.weight(1f),
                        icono = Icons.Default.Star,
                        valor = if (promedioCalif > 0) String.format("%.1f", promedioCalif) else "N/A",
                        etiqueta = stringResource(R.string.label_promedio))
                }
            }

            // Rutinas creadas
            item {
                Text(stringResource(R.string.kardex_seccion_rutinas, rutinas.size),
                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            if (rutinas.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.kardex_sin_rutinas),
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(rutinas) { rutina -> TarjetaRutinaKardex(rutina) }
            }

            // Historial reciente
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.kardex_seccion_historial, historial.size),
                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            if (historial.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.kardex_sin_historial),
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(historial.take(10)) { log -> TarjetaLogKardex(log) }
                if (historial.size > 10) {
                    item {
                        Text(stringResource(R.string.kardex_mas_registros, historial.size - 10),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp))
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun TarjetaEstadistica(modifier: Modifier, icono: ImageVector, valor: String, etiqueta: String) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp))
            Text(valor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary)
            Text(etiqueta, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TarjetaRutinaKardex(rutina: Rutina) {
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.FitnessCenter, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(rutina.nombre, fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall)
                Text("${rutina.nivel} · ${rutina.duracionMinutos} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (rutina.esPublica) {
                Icon(Icons.Default.Public, contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun TarjetaLogKardex(log: LogActividad) {
    val fecha = remember(log.fecha) {
        SimpleDateFormat("dd MMM yyyy", Locale("es", "MX")).format(Date(log.fecha))
    }
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(1.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Rutina #${log.idRutina}", style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold)
                Text("${log.duracionRealMinutos} min · $fecha",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (log.calificacion > 0) {
                Text("${"★".repeat(log.calificacion)}",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}