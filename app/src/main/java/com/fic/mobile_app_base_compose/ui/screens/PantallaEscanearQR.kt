package com.fic.mobile_app_base_compose.ui.screens

import android.Manifest
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.fic.mobile_app_base_compose.SesionUsuario
import com.fic.mobile_app_base_compose.data.local.FitmachBaseDatos
import com.fic.mobile_app_base_compose.data.model.Rutina
import com.fic.mobile_app_base_compose.data.repository.FirebaseRepository
import com.fic.mobile_app_base_compose.util.parsearQR
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PantallaEscanearQR(onVolver: () -> Unit) {
    val contexto = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val db = FitmachBaseDatos.obtenerInstancia(contexto)
    val scope = rememberCoroutineScope()
    val firebaseRepository = remember { FirebaseRepository() }

    val permisoCamara = rememberPermissionState(Manifest.permission.CAMERA)
    var escaneado by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var rutinaEncontrada by remember { mutableStateOf<Map<String, Any>?>(null) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!permisoCamara.status.isGranted) {
            permisoCamara.launchPermissionRequest()
        }
    }

    if (mostrarDialogo && rutinaEncontrada != null) {
        val nombreRutina = rutinaEncontrada!!["nombre"] as? String ?: "Rutina"
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("¿Agregar esta rutina?") },
            text = { Text("¿Deseas agregar \"$nombreRutina\" a tus rutinas?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        val datos = rutinaEncontrada!!
                        val nuevaRutina = Rutina(
                            nombre = datos["nombre"] as? String ?: "",
                            descripcion = datos["descripcion"] as? String ?: "",
                            nivel = datos["nivel"] as? String ?: "",
                            duracionMinutos = (datos["duracionMinutos"] as? Long)?.toInt() ?: 0,
                            idCreador = SesionUsuario.idUsuario,
                            esPublica = false
                        )
                        db.rutinaDao().insertar(nuevaRutina)
                        mensaje = "¡Rutina agregada exitosamente!"
                        mostrarDialogo = false
                        rutinaEncontrada = null
                    }
                }) {
                    Text("Agregar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    escaneado = false
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escanear QR") },
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
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!permisoCamara.status.isGranted) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Se necesita permiso de cámara",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(onClick = { permisoCamara.launchPermissionRequest() }) {
                            Text("Dar permiso")
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            val executor = Executors.newSingleThreadExecutor()

                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                val scanner = BarcodeScanning.getClient()
                                val analisis = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()

                                analisis.setAnalyzer(executor) { imageProxy ->
                                    if (!escaneado) {
                                        val mediaImage = imageProxy.image
                                        if (mediaImage != null) {
                                            val imagen = InputImage.fromMediaImage(
                                                mediaImage,
                                                imageProxy.imageInfo.rotationDegrees
                                            )
                                            scanner.process(imagen)
                                                .addOnSuccessListener { barcodes ->
                                                    for (barcode in barcodes) {
                                                        if (barcode.format == Barcode.FORMAT_QR_CODE) {
                                                            val contenido = barcode.rawValue ?: continue
                                                            val parsed = parsearQR(contenido)
                                                            if (parsed != null && !escaneado) {
                                                                escaneado = true
                                                                val (creador, rutinaId) = parsed
                                                                scope.launch {
                                                                    val resultado = firebaseRepository.obtenerRutina(creador, rutinaId)
                                                                    resultado.onSuccess { datos ->
                                                                        if (datos != null) {
                                                                            rutinaEncontrada = datos
                                                                            mostrarDialogo = true
                                                                        } else {
                                                                            mensaje = "Rutina no encontrada en la red"
                                                                            escaneado = false
                                                                        }
                                                                    }
                                                                    resultado.onFailure {
                                                                        mensaje = "Error al buscar la rutina"
                                                                        escaneado = false
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                .addOnCompleteListener { imageProxy.close() }
                                        } else {
                                            imageProxy.close()
                                        }
                                    } else {
                                        imageProxy.close()
                                    }
                                }

                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview,
                                    analisis
                                )
                            }, ContextCompat.getMainExecutor(ctx))

                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Marco guía
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.size(250.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = androidx.compose.ui.graphics.Color.Transparent
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                3.dp,
                                MaterialTheme.colorScheme.primary
                            ),
                            shape = MaterialTheme.shapes.large
                        ) {}
                    }
                }

                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = mensaje ?: "Apunta la cámara al código QR de la rutina",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (mensaje != null) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    if (escaneado && mensaje != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = {
                            escaneado = false
                            mensaje = null
                        }) {
                            Text("Escanear otro")
                        }
                    }
                }
            }
        }
    }
}