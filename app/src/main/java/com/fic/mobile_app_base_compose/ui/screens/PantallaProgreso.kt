package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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
fun PantallaProgreso(
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
                title = { Text("Mi Progreso") },
                navigationIcon = {
                    TextButton(onClick = onVolver) { Text("Volver") }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { TarjetaResumen(historial) }
            item { TarjetaGraficaDuracion(historial) }
            item { TarjetaGraficaCalificacion(historial) }
        }
    }
}

@Composable
private fun TarjetaResumen(historial: List<LogActividad>) {
    val totalSesiones = historial.size
    val totalMinutos = historial.sumOf { it.duracionRealMinutos }
    val promedioCalificacion = if (historial.isNotEmpty())
        historial.filter { it.calificacion > 0 }.map { it.calificacion }.average() else 0.0

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.TrendingUp, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary)
                Text("Resumen General", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
            }
            HorizontalDivider()
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ColumnaEstadistica("Sesiones", "$totalSesiones")
                ColumnaEstadistica("Minutos\nTotales", "$totalMinutos")
                ColumnaEstadistica("Calificación\nPromedio",
                    if (promedioCalificacion > 0) String.format("%.1f ★", promedioCalificacion) else "N/A")
            }
        }
    }
}

@Composable
private fun ColumnaEstadistica(label: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun TarjetaGraficaDuracion(historial: List<LogActividad>) {
    val datos = historial.takeLast(7).map { it.duracionRealMinutos.toFloat() }
    val colorLinea = MaterialTheme.colorScheme.primary

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Duración por Sesión (últimas 7)",
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (datos.size < 2) {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentAlignment = Alignment.Center) {
                    Text("Necesitas al menos 2 sesiones para ver la gráfica",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                GraficaLinea(datos = datos, color = colorLinea, unidad = "min")
            }
        }
    }
}

@Composable
private fun TarjetaGraficaCalificacion(historial: List<LogActividad>) {
    val datos = historial.filter { it.calificacion > 0 }.takeLast(7)
        .map { it.calificacion.toFloat() }
    val colorLinea = MaterialTheme.colorScheme.tertiary

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Calificación por Sesión (últimas 7)",
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (datos.size < 2) {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentAlignment = Alignment.Center) {
                    Text("Necesitas al menos 2 sesiones calificadas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                GraficaLinea(datos = datos, color = colorLinea, unidad = "★", maxValor = 5f)
            }
        }
    }
}

@Composable
private fun GraficaLinea(
    datos: List<Float>,
    color: Color,
    unidad: String,
    maxValor: Float? = null
) {
    val max = maxValor ?: (datos.max() * 1.2f).coerceAtLeast(1f)
    val min = 0f

    Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
        val ancho = size.width
        val alto = size.height
        val pasoX = ancho / (datos.size - 1)
        val rangoY = max - min

        // Líneas de referencia
        val lineasRef = 4
        for (i in 0..lineasRef) {
            val y = alto - (i.toFloat() / lineasRef) * alto
            drawLine(color = Color.LightGray, start = Offset(0f, y),
                end = Offset(ancho, y), strokeWidth = 1.dp.toPx())
        }

        // Path de la línea
        val path = Path()
        datos.forEachIndexed { index, valor ->
            val x = index * pasoX
            val y = alto - ((valor - min) / rangoY) * alto
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path = path, color = color, style = Stroke(width = 3.dp.toPx()))

        // Puntos
        datos.forEachIndexed { index, valor ->
            val x = index * pasoX
            val y = alto - ((valor - min) / rangoY) * alto
            drawCircle(color = color, radius = 5.dp.toPx(), center = Offset(x, y))
            drawCircle(color = Color.White, radius = 3.dp.toPx(), center = Offset(x, y))
        }
    }

    // Etiquetas
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        datos.forEach { valor ->
            Text("${valor.toInt()}$unidad", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}