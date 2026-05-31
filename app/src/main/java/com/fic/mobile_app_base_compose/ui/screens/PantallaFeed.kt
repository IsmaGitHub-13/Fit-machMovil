package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

data class PublicacionMock(
    val id: Int,
    val nombreUsuario: String,
    val userTag: String,
    val descripcion: String,
    val nombreRutina: String,
    val nivel: String,
    val duracionMin: Int,
    val likes: Int,
    val fecha: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFeed(onVolver: () -> Unit) {

    val publicaciones = remember {
        listOf(
            PublicacionMock(1, "Alan Triston", "@AlanNatural",
                "Nueva semana, nueva rutina 💪 Semana de volumen para pecho.",
                "Rutina Pecho Volumen", "Intermedio", 60, 12,
                System.currentTimeMillis() - 3600000),
            PublicacionMock(2, "Adan Sauceda", "@Eltaladan",
                "Primer día de powerlifting, empezando con sentadillas 🏋️",
                "Powerlifting Semana 1", "Avanzado", 90, 8,
                System.currentTimeMillis() - 7200000),
            PublicacionMock(3, "Andrik Pía", "@DkDeca",
                "Rutina rápida antes del trabajo, 45 minutos y listo ⚡",
                "Full Body Express", "Principiante", 45, 5,
                System.currentTimeMillis() - 86400000),
            PublicacionMock(4, "Alan Triston", "@AlanNatural",
                "Terminé mi plan de 12 semanas. Subí 15kg en press banca 🔥",
                "Progresión Press Banca", "Intermedio", 75, 24,
                System.currentTimeMillis() - 172800000),
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Feed", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(publicaciones) { pub ->
                TarjetaPublicacion(pub)
            }
        }
    }
}

@Composable
private fun TarjetaPublicacion(pub: PublicacionMock) {
    var liked by remember { mutableStateOf(false) }
    var likesCount by remember { mutableStateOf(pub.likes) }

    val fecha = remember {
        val diff = System.currentTimeMillis() - pub.fecha
        when {
            diff < 3600000 -> "Hace ${diff / 60000} min"
            diff < 86400000 -> "Hace ${diff / 3600000}h"
            else -> SimpleDateFormat("dd MMM", Locale("es", "MX")).format(Date(pub.fecha))
        }
    }

    val colorNivel = when (pub.nivel) {
        "Principiante" -> MaterialTheme.colorScheme.tertiary
        "Intermedio" -> MaterialTheme.colorScheme.secondary
        "Avanzado" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header usuario
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(pub.nombreUsuario, fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall)
                    Text(pub.userTag, style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(fecha, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Descripción
            Text(pub.descripcion, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(10.dp))

            // Card de rutina
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(pub.nombreRutina, fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelLarge)
                        Text("⏱ ${pub.duracionMin} min",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = colorNivel.copy(alpha = 0.15f)
                    ) {
                        Text(pub.nivel, style = MaterialTheme.typography.labelSmall,
                            color = colorNivel, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer likes
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        liked = !liked
                        likesCount = if (liked) pub.likes + 1 else pub.likes
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        if (liked) Icons.Default.FavoriteBorder else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (liked) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text("$likesCount", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}