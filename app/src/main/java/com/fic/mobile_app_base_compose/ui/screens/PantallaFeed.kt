package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fic.mobile_app_base_compose.SesionUsuario
import com.fic.mobile_app_base_compose.data.local.FitmachBaseDatos
import com.fic.mobile_app_base_compose.data.model.Publicacion
import com.fic.mobile_app_base_compose.data.repository.PublicacionRepository
import com.fic.mobile_app_base_compose.data.repository.SolicitudAmistadRepository
import com.fic.mobile_app_base_compose.viewmodel.FeedViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFeed(onVolver: () -> Unit) {
    val contexto = LocalContext.current
    val db = FitmachBaseDatos.obtenerInstancia(contexto)

    val viewModel: FeedViewModel = viewModel(
        factory = FeedViewModel.Factory(
            publicacionRepo = PublicacionRepository(db.publicacionDao()),
            amistadRepo = SolicitudAmistadRepository(db.solicitudAmistadDao())
        )
    )

    val publicaciones by viewModel.publicaciones.collectAsStateWithLifecycle()
    val publicando by viewModel.publicando.collectAsStateWithLifecycle()

    var mostrarDialogoPost by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.cargarFeed(SesionUsuario.idUsuario)
    }

    if (mostrarDialogoPost) {
        DialogoNuevaPublicacion(
            publicando = publicando,
            onPublicar = { descripcion, rutina ->
                viewModel.publicar(SesionUsuario.idUsuario, descripcion, rutina)
                mostrarDialogoPost = false
            },
            onCancelar = { mostrarDialogoPost = false }
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogoPost = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva publicación")
            }
        }
    ) { paddingValues ->
        if (publicaciones.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Aún no hay publicaciones",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Agrega amigos o sé el primero en publicar 🏋️",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(publicaciones, key = { it.idPublicacion }) { pub ->
                    TarjetaPublicacionReal(
                        publicacion = pub,
                        esMia = pub.idUsuario == SesionUsuario.idUsuario,
                        onLike = { viewModel.darLike(pub.idPublicacion) },
                        onQuitarLike = { viewModel.quitarLike(pub.idPublicacion) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaPublicacionReal(
    publicacion: Publicacion,
    esMia: Boolean,
    onLike: () -> Unit,
    onQuitarLike: () -> Unit
) {
    var liked by remember { mutableStateOf(false) }

    val fecha = remember(publicacion.fecha) {
        val diff = System.currentTimeMillis() - publicacion.fecha
        when {
            diff < 3600000 -> "Hace ${diff / 60000} min"
            diff < 86400000 -> "Hace ${diff / 3600000}h"
            else -> SimpleDateFormat("dd MMM", Locale("es", "MX")).format(Date(publicacion.fecha))
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = if (esMia) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null,
                            tint = if (esMia) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        if (esMia) SesionUsuario.nombre else "Usuario #${publicacion.idUsuario}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        if (esMia) "@${SesionUsuario.nombreUsuario}" else "",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(fecha, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(publicacion.descripcion, style = MaterialTheme.typography.bodyMedium)

            if (publicacion.comentarios.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant) {
                    Row(modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Text(publicacion.comentarios, fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = { if (liked) { onQuitarLike(); liked = false } else { onLike(); liked = true } },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (liked) "Quitar like" else "Dar like",
                        tint = if (liked) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text("${publicacion.likes}", style = MaterialTheme.typography.labelMedium,
                    color = if (liked) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun DialogoNuevaPublicacion(
    publicando: Boolean,
    onPublicar: (String, String) -> Unit,
    onCancelar: () -> Unit
) {
    var descripcion by remember { mutableStateOf("") }
    var rutina by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Nueva publicación") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("¿Qué entrenaste hoy?") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
                OutlinedTextField(
                    value = rutina,
                    onValueChange = { rutina = it },
                    label = { Text("Nombre de la rutina (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onPublicar(descripcion, rutina) },
                enabled = descripcion.isNotBlank() && !publicando
            ) {
                if (publicando) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text("Publicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}