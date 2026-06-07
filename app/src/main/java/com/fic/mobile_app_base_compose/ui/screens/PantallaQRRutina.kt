package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fic.mobile_app_base_compose.SesionUsuario
import com.fic.mobile_app_base_compose.data.local.FitmachBaseDatos
import com.fic.mobile_app_base_compose.data.repository.FirebaseRepository
import com.fic.mobile_app_base_compose.util.generarContenidoQR
import com.fic.mobile_app_base_compose.util.generarQR
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaQRRutina(
    rutinaId: Int,
    onVolver: () -> Unit
) {
    val contexto = LocalContext.current
    val db = FitmachBaseDatos.obtenerInstancia(contexto)
    val scope = rememberCoroutineScope()
    val firebaseRepository = remember { FirebaseRepository() }

    var nombreRutina by remember { mutableStateOf("") }
    var publicando by remember { mutableStateOf(false) }
    var publicada by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }

    val contenidoQR = generarContenidoQR(SesionUsuario.nombreUsuario, rutinaId)
    val qrBitmap = remember(contenidoQR) { generarQR(contenidoQR) }

    LaunchedEffect(rutinaId) {
        val rutina = db.rutinaDao().obtenerPorId(rutinaId)
        nombreRutina = rutina?.nombre ?: "Rutina"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compartir Rutina") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = nombreRutina,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Muestra este QR a tu amigo para que pueda agregar tu rutina",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Card(
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "Código QR de la rutina",
                    modifier = Modifier
                        .size(280.dp)
                        .padding(16.dp)
                )
            }

            Text(
                text = "@${SesionUsuario.nombreUsuario}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!publicada) {
                Button(
                    onClick = {
                        scope.launch {
                            publicando = true
                            val rutina = db.rutinaDao().obtenerPorId(rutinaId)
                            if (rutina != null) {
                                val resultado = firebaseRepository.publicarRutina(
                                    nombreUsuario = SesionUsuario.nombreUsuario,
                                    rutinaId = rutinaId,
                                    nombre = rutina.nombre,
                                    descripcion = rutina.descripcion,
                                    nivel = rutina.nivel,
                                    duracionMinutos = rutina.duracionMinutos,
                                    ejercicios = emptyList()
                                )
                                resultado.fold(
                                    onSuccess = {
                                        publicada = true
                                        mensaje = "Rutina publicada — ya puede escanearse"
                                    },
                                    onFailure = {
                                        mensaje = "Error al publicar: ${it.message}"
                                    }
                                )
                            }
                            publicando = false
                        }
                    },
                    enabled = !publicando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (publicando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Publicar rutina en la red")
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ Rutina publicada — tus amigos ya pueden escanear el QR",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }

            mensaje?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}