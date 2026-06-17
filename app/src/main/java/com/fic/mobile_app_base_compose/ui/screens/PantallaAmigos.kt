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
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fic.mobile_app_base_compose.R
import com.fic.mobile_app_base_compose.SesionUsuario
import com.fic.mobile_app_base_compose.data.local.FitmachBaseDatos
import com.fic.mobile_app_base_compose.data.model.SolicitudAmistad
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
    val solicitudesFirebase by viewModel.solicitudesFirebase.collectAsStateWithLifecycle()
    val amigosFirebase by viewModel.amigosFirebase.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensajeAccion.collectAsStateWithLifecycle()

    var amigoPorEliminar by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.cargarDatos(SesionUsuario.idUsuario)
        viewModel.cargarDatosFirebase(SesionUsuario.nombreUsuario)
    }

    mensaje?.let {
        LaunchedEffect(it) {
            kotlinx.coroutines.delay(2000)
            viewModel.limpiarMensaje()
        }
    }

    if (amigoPorEliminar != null) {
        AlertDialog(
            onDismissRequest = { amigoPorEliminar = null },
            title = { Text(stringResource(R.string.dialog_eliminar_amigo_titulo)) },
            text = { Text("¿Deseas eliminar a Usuario #$amigoPorEliminar de tu lista de amigos?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarAmigo(SesionUsuario.idUsuario, amigoPorEliminar!!)
                    amigoPorEliminar = null
                }) {
                    Text(stringResource(R.string.btn_eliminar_amigo), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { amigoPorEliminar = null }) {
                    Text(stringResource(R.string.btn_cancelar))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_amigos)) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.btn_volver))
                    }
                },
                actions = {
                    IconButton(onClick = onBuscarUsuarios) {
                        Icon(Icons.Default.PersonAdd, contentDescription = stringResource(R.string.btn_buscar_usuarios))
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

            // — Solicitudes de Firebase —
            if (solicitudesFirebase.isNotEmpty()) {
                item {
                    Text(
                        "${stringResource(R.string.solicitudes_pendientes)} (${solicitudesFirebase.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                items(solicitudesFirebase, key = { "fb_$it" }) { deUsuario ->
                    TarjetaSolicitudFirebase(
                        deUsuario = deUsuario,
                        onAceptar = {
                            viewModel.aceptarSolicitudFirebase(SesionUsuario.nombreUsuario, deUsuario)
                        },
                        onRechazar = {
                            viewModel.rechazarSolicitudFirebase(SesionUsuario.nombreUsuario, deUsuario)
                        }
                    )
                }
                item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }
            }

            // — Solicitudes locales (Room) —
            if (solicitudesPendientes.isNotEmpty()) {
                item {
                    Text(
                        "${stringResource(R.string.solicitudes_pendientes)} (${solicitudesPendientes.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
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

            // — Amigos de Firebase —
            if (amigosFirebase.isNotEmpty()) {
                item {
                    Text(
                        "${stringResource(R.string.mis_amigos)} (${amigosFirebase.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                items(amigosFirebase, key = { "fb_amigo_$it" }) { nombreUsuario ->
                    TarjetaAmigoFirebase(
                        nombreUsuario = nombreUsuario,
                        onClick = { onVerPerfil(0, nombreUsuario, "@$nombreUsuario") }
                    )
                }
                item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }
            }

            // — Amigos locales (Room) —
            if (idsAmigos.isNotEmpty()) {
                items(idsAmigos, key = { it }) { idAmigo ->
                    TarjetaAmigo(
                        idAmigo = idAmigo,
                        onClick = { onVerPerfil(idAmigo, "Usuario #$idAmigo", "@usuario$idAmigo") },
                        onEliminar = { amigoPorEliminar = idAmigo }
                    )
                }
            }

            // — Sin nada —
            if (solicitudesFirebase.isEmpty() && solicitudesPendientes.isEmpty() &&
                amigosFirebase.isEmpty() && idsAmigos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                stringResource(R.string.sin_amigos),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(onClick = onBuscarUsuarios) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.btn_buscar_usuarios))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaSolicitudFirebase(
    deUsuario: String,
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
                Text("@$deUsuario", fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall)
                Text(stringResource(R.string.quiere_ser_amigo),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onAceptar) {
                Icon(Icons.Default.Check, contentDescription = stringResource(R.string.btn_aceptar),
                    tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onRechazar) {
                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.btn_rechazar),
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun TarjetaAmigoFirebase(
    nombreUsuario: String,
    onClick: () -> Unit
) {
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
                Text("@$nombreUsuario", fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall)
                Text("Usuario de FitMatch",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                Text("Usuario #${solicitud.idRemitente}", fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall)
                Text(stringResource(R.string.quiere_ser_amigo),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onAceptar) {
                Icon(Icons.Default.Check, contentDescription = stringResource(R.string.btn_aceptar),
                    tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onRechazar) {
                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.btn_rechazar),
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun TarjetaAmigo(
    idAmigo: Int,
    onClick: () -> Unit,
    onEliminar: () -> Unit
) {
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
                Text("Usuario #$idAmigo", fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall)
                Text("@usuario$idAmigo", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.PersonRemove,
                    contentDescription = stringResource(R.string.btn_eliminar_amigo),
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}