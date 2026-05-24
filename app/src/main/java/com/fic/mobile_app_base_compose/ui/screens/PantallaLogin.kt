package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fic.mobile_app_base_compose.R
import com.fic.mobile_app_base_compose.data.local.FitmachBaseDatos
import com.fic.mobile_app_base_compose.data.repository.UsuarioRepository
import com.fic.mobile_app_base_compose.viewmodel.LoginUiState
import com.fic.mobile_app_base_compose.viewmodel.LoginViewModel

@Composable
fun PantallaLogin(
    onIngresar: () -> Unit,
    onIrARegistro: () -> Unit
) {
    val contexto = LocalContext.current

    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.Factory(
            repository = UsuarioRepository(
                FitmachBaseDatos.obtenerInstancia(contexto).usuarioDao()
            )
        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Exito) {
            onIngresar()
            viewModel.resetearEstado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "FitMatch",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.titulo_login),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text(stringResource(id = R.string.pista_usuario)) },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState is LoginUiState.Error,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text(stringResource(id = R.string.pista_contrasena)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = uiState is LoginUiState.Error,
            singleLine = true
        )

        if (uiState is LoginUiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (uiState as LoginUiState.Error).mensaje,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.iniciarSesion(usuario, contrasena) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = uiState !is LoginUiState.Cargando
        ) {
            if (uiState is LoginUiState.Cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(stringResource(id = R.string.btn_login))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onIrARegistro) {
            Text(text = "¿No tienes cuenta? Regístrate aquí")
        }
    }
}