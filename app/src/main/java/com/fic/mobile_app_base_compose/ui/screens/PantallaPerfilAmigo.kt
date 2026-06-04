package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fic.mobile_app_base_compose.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfilAmigo(
    nombre: String,
    usuario: String,
    onVolver: () -> Unit
) {
    val rutinasPublicas = listOf(
        Triple(stringResource(R.string.rutina_pecho_tricep), stringResource(R.string.nivel_intermedio), 60),
        Triple(stringResource(R.string.rutina_full_body), stringResource(R.string.nivel_avanzado), 90),
        Triple(stringResource(R.string.rutina_cardio_hiit), stringResource(R.string.nivel_principiante), 30),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(nombre) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.btn_volver))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            Column(
                modifier = Modifier.offset(y = (-38).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(nombre, style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold)
                Text(usuario, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    stringResource(R.string.rutinas_publicas),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                rutinasPublicas.forEach { (nombreRutina, nivel, duracion) ->
                    val nivelIntermedio = stringResource(R.string.nivel_intermedio)
                    val nivelAvanzado = stringResource(R.string.nivel_avanzado)
                    val colorNivel = when (nivel) {
                        nivelIntermedio -> MaterialTheme.colorScheme.secondary
                        nivelAvanzado -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.tertiary
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                            Icon(Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(nombreRutina, fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.labelLarge)
                                Text("⏱ $duracion min",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = colorNivel.copy(alpha = 0.15f)
                            ) {
                                Text(nivel, style = MaterialTheme.typography.labelSmall,
                                    color = colorNivel, fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}