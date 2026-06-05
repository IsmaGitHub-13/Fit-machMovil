package com.fic.mobile_app_base_compose.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.perfilDataStore: DataStore<Preferences> by preferencesDataStore(name = "perfil")

class PerfilDataStore(private val contexto: Context) {

    companion object {
        val KEY_DESCRIPCION = stringPreferencesKey("descripcion")
        val KEY_FOTO_URI = stringPreferencesKey("foto_uri")
    }

    val descripcion: Flow<String> = contexto.perfilDataStore.data
        .map { preferences -> preferences[KEY_DESCRIPCION] ?: "" }

    val fotoUri: Flow<String> = contexto.perfilDataStore.data
        .map { preferences -> preferences[KEY_FOTO_URI] ?: "" }

    suspend fun guardarDescripcion(descripcion: String) {
        contexto.perfilDataStore.edit { preferences ->
            preferences[KEY_DESCRIPCION] = descripcion
        }
    }

    suspend fun guardarFotoUri(uri: String) {
        contexto.perfilDataStore.edit { preferences ->
            preferences[KEY_FOTO_URI] = uri
        }
    }
}