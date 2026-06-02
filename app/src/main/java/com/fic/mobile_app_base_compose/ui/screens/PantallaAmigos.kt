package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
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
import com.fic.mobile_app_base_compose.data.model.SolicitudAmistad
import com.fic.mobile_app_base_compose.data.model.Usuario
import com.fic.mobile_app_base_compose.data.repository.SolicitudAmistadRepository
import com.fic.mobile_app_base_compose.data.repository.UsuarioRepository
import com.fic.mobile_app_base_compose.viewmodel.AmistadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAmigos(
    onVolver: () -> Unit,
    onBuscarUsuarios: () -> Unit,
    onVerPerfil: (Int, String, String) -> Unit = { _, _, _ -> }
) {
    val contexto = LocalContext.current
    val db = FitmachBaseDatos.obtenerInstancia(contexto)

    val viewModel: AmistadViewModel = viewModel(
        factory = AmistadViewModel.Factory(
            solicitudRepo = SolicitudAmistadRepository(db.solicitudAmistadDao()),
            usuarioRepo = UsuarioRepository(db.usuarioDao())
        )
    )

    val solicitudesPendientes by viewModel.solicitudesPendientes.collectAsStateWithLifecycle()
    val idsAmigos by viewModel.idsAmigos.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensajeAccion.collectAsStateWithLifecycle()

    // Cargamos los usuarios amigos en base a sus IDs
    var amigos by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    LaunchedEffect(idsAmigos) {
        // Simplificado: usamos los IDs para mostrar la lista
    }

    LaunchedEffect(Unit) {
        viewModel.cargarDatos(SesionUsuario.idUsuario)
    }

    mensaje?.let {
        LaunchedEffect(it) {
            kotlinx.coroutines.delay(2000)
            viewModel.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Amigos") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onBuscarUsuarios) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Buscar usuarios")
                    }
                }
            )
        },
        snackbarHost = {
            mensaje?.let { msg ->
                Snackbar(modifier = Modifier.padding(16.dp)) { Text(msg) }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // Solicitudes pendientes
            if (solicitudesPendientes.isNotEmpty()) {
                item {
                    Text("Solicitudes pendientes (${solicitudesPendientes.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                }
                items(solicitudesPendientes, key = { it.idSolicitud }) { solicitud ->
                    TarjetaSolicitud(
                        solicitud = solicitud,
                        onAceptar = { viewModel.aceptarSolicitud(solicitud.idSolicitud) },
                        onRechazar = { viewModel.rechazarSolicitud(solicitud.idSolicitud) }
                    )
                }
                item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }
            }

            // Lista de amigos por IDs
            if (idsAmigos.isEmpty() && solicitudesPendientes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Aún no tienes amigos",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            TextButton(onClick = onBuscarUsuarios) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Buscar usuarios")
                            }
                        }
                    }
                }
            } else {
                item {
                    Text("Mis amigos (${idsAmigos.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                }
                items(idsAmigos, key = { it }) { idAmigo ->
                    TarjetaAmigo(
                        idAmigo = idAmigo,
                        onClick = { onVerPerfil(idAmigo, "Usuario", "@usuario$idAmigo") }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaSolicitud(
    solicitud: SolicitudAmistad,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Usuario #${solicitud.idRemitente}",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall)
                Text("Quiere ser tu amigo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onAceptar) {
                Icon(Icons.Default.Check, contentDescription = "Aceptar",
                    tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onRechazar) {
                Icon(Icons.Default.Close, contentDescription = "Rechazar",
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun TarjetaAmigo(idAmigo: Int, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Usuario #$idAmigo",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall)
                Text("@usuario$idAmigo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}