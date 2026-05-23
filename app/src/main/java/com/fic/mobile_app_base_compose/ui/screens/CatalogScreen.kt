package com.fic.mobile_app_base_compose.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.fic.mobile_app_base_compose.R
import com.fic.mobile_app_base_compose.ui.components.BottomMenuBar

/**
 * Pantalla que muestra un catálogo de especies locales.
 * Utiliza una lista eficiente (LazyColumn) para mostrar gran cantidad de datos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(navController: NavHostController) {
    // 1. FUENTE DE DATOS (Hardcoded temporalmente, en el futuro vendrá de una API o BD)
    val especiesDemo = listOf(
        "Iguana Verde (Iguana iguana)",
        "Caimán del río (Caiman crocodilus)",
        "Palo Blanco (Ipomoea arborescens)"
    )

    // 2. ESTRUCTURA BASE (Scaffold)
    Scaffold(
        // Barra superior alineada al centro (Estilo minimalista de M3)
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.title_catalog)) }
            )
        },
        // Inserción del menú de navegación inferior compartido
        bottomBar = { BottomMenuBar(navController) }
    ) { paddingValues ->

        // 3. LISTA OPTIMIZADA (LazyColumn)
        // LazyColumn es equivalente al RecyclerView en XML. Solo renderiza lo que es visible.
        LazyColumn(modifier = Modifier.padding(paddingValues)) {

            // Iteramos sobre la lista de especies
            items(especiesDemo) { especie ->

                // Elemento de lista estándar de Material Design 3
                ListItem(
                    headlineContent = {
                        Text(especie, fontWeight = FontWeight.Bold)
                    },
                    supportingContent = {
                        // Texto descriptivo desde strings.xml
                        Text(stringResource(R.string.catalog_status))
                    }
                )

                // Línea divisoria con márgenes obtenidos de dimens.xml
                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.margin_medium)
                    )
                )
            }
        }
    }
}