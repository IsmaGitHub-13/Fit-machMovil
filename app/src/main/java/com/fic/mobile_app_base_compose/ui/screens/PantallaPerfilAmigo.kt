package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fic.mobile_app_base_compose.data.repository.FirebaseRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfilAmigo(
    nombre: String,
    usuario: String,
    onVolver: () -> Unit
) {
    val firebaseRepository = remember { FirebaseRepository() }
    var rutinas by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(usuario) {
        val resultado = firebaseRepository.obtenerRutinasDeAmigo(
            usuario.removePrefix("@")
        )
        resultado.onSuccess { rutinas = it }
        cargando = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(nombre) },
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
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // — Header con degradado —
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                )
            }

            // — Avatar —
            item {
                Box(modifier = Modifier.offset(y = (-50).dp)) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // — Nombre y usuario —
            item {
                Column(
                    modifier = Modifier.offset(y = (-38).dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        nombre,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        usuario,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // — Título sección rutinas —
            item {
                Text(
                    "Rutinas públicas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // — Estado de carga —
            if (cargando) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (rutinas.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Este usuario no tiene rutinas públicas aún",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // — Lista de rutinas reales de Firebase —
                items(rutinas) { rutina ->
                    val nombreRutina = rutina["nombre"] as? String ?: "Rutina"
                    val nivel = rutina["nivel"] as? String ?: ""
                    val duracion = (rutina["duracionMinutos"] as? Long)?.toInt() ?: 0

                    val colorNivel = when (nivel) {
                        "Intermedio" -> MaterialTheme.colorScheme.secondary
                        "Avanzado" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.tertiary
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    nombreRutina,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Text(
                                    "⏱ $duracion min",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (nivel.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = colorNivel.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        nivel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colorNivel,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}