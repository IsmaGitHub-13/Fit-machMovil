package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Amigo(val id: Int, val nombre: String, val usuario: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAmigos(
    onVolver: () -> Unit,
    onVerPerfil: (Amigo) -> Unit = {}
) {
    val amigosIniciales = remember {
        mutableStateListOf(
            Amigo(1, "Alan Triston", "@AlanNatural"),
            Amigo(2, "Adan Sauceda", "@Eltaladan"),
            Amigo(3, "Ismael Alcantara", "@CladJustin13"),
            Amigo(4, "Andrik Pia", "@DkDeca")
        )
    }

    var busqueda by remember { mutableStateOf("") }
    var amigoPorEliminar by remember { mutableStateOf<Amigo?>(null) }

    val amigosFiltrados = remember(busqueda, amigosIniciales.toList()) {
        if (busqueda.isBlank()) amigosIniciales
        else amigosIniciales.filter {
            it.nombre.contains(busqueda, ignoreCase = true) ||
                    it.usuario.contains(busqueda, ignoreCase = true)
        }
    }

    if (amigoPorEliminar != null) {
        AlertDialog(
            onDismissRequest = { amigoPorEliminar = null },
            title = { Text("¿Eliminar amigo?") },
            text = { Text("¿Deseas eliminar a ${amigoPorEliminar!!.nombre} de tu lista?") },
            confirmButton = {
                TextButton(onClick = {
                    amigosIniciales.remove(amigoPorEliminar)
                    amigoPorEliminar = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { amigoPorEliminar = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Amigos") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Barra de búsqueda
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                placeholder = { Text("Buscar amigo...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.extraLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (amigosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (busqueda.isBlank()) "Aún no tienes amigos agregados."
                        else "No se encontró ningún amigo con \"$busqueda\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(amigosFiltrados) { amigo ->
                        Card(
                            onClick = { onVerPerfil(amigo) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = MaterialTheme.shapes.extraLarge,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(amigo.nombre,
                                        style = MaterialTheme.typography.titleSmall)
                                    Text(amigo.usuario,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { amigoPorEliminar = amigo }) {
                                    Icon(Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}