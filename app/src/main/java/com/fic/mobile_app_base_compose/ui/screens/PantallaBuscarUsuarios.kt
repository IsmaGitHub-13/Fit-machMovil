package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
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
import com.fic.mobile_app_base_compose.data.repository.SolicitudAmistadRepository
import com.fic.mobile_app_base_compose.data.repository.UsuarioRepository
import com.fic.mobile_app_base_compose.viewmodel.AmistadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBuscarUsuarios(onVolver: () -> Unit) {
    val contexto = LocalContext.current
    val db = FitmachBaseDatos.obtenerInstancia(contexto)

    val viewModel: AmistadViewModel = viewModel(
        factory = AmistadViewModel.Factory(
            solicitudRepo = SolicitudAmistadRepository(db.solicitudAmistadDao()),
            usuarioRepo = UsuarioRepository(db.usuarioDao())
        )
    )

    val resultados by viewModel.resultadosBusqueda.collectAsStateWithLifecycle()
    val idsAmigos by viewModel.idsAmigos.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensajeAccion.collectAsStateWithLifecycle()

    var busqueda by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.cargarDatos(SesionUsuario.idUsuario)
    }

    LaunchedEffect(busqueda) {
        viewModel.buscarUsuarios(busqueda, SesionUsuario.idUsuario)
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
                title = { Text("Buscar usuarios") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                placeholder = { Text("Busca por nombre o usuario...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.extraLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (busqueda.isBlank()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Escribe un nombre o usuario para buscar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (resultados.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontró ningún usuario",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(resultados, key = { it.idUsuario }) { usuario ->
                        val esAmigo = idsAmigos.contains(usuario.idUsuario)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
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
                                    Text("${usuario.nombre} ${usuario.apellidoPaterno}",
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.titleSmall)
                                    Text("@${usuario.nombreUsuarioLogin}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (esAmigo) {
                                    Icon(Icons.Default.Check, contentDescription = "Ya son amigos",
                                        tint = MaterialTheme.colorScheme.primary)
                                } else {
                                    IconButton(onClick = {
                                        viewModel.enviarSolicitud(
                                            SesionUsuario.idUsuario,
                                            usuario.idUsuario
                                        )
                                    }) {
                                        Icon(Icons.Default.PersonAdd,
                                            contentDescription = "Agregar amigo",
                                            tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}