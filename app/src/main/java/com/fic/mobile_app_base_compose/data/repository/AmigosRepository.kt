package com.fic.mobile_app_base_compose.data.repository

import com.fic.mobile_app_base_compose.data.local.AmistadDao
import com.fic.mobile_app_base_compose.data.local.UsuarioDao
import com.fic.mobile_app_base_compose.data.model.Amistad
import com.fic.mobile_app_base_compose.data.model.Usuario
import kotlinx.coroutines.flow.Flow

class AmigosRepository(
    private val usuarioDao: UsuarioDao,
    private val amidadDao: AmistadDao
) {

    // 1. Buscar usuarios en la app (para la barra de búsqueda)
    fun buscarUsuarios(query: String, miId: Int): Flow<List<Usuario>> {
        return usuarioDao.buscarUsuarios("%$query%", miId)
    }

    // 2. Enviar una nueva solicitud de amistad
    suspend fun enviarSolicitud(miId: Int, idAmigo: Int) {
        val nuevaSolicitud = Amistad(
            idUsuarioOrigen = miId,
            idUsuarioDestino = idAmigo,
            estado = "PENDIENTE"
        )
        amidadDao.enviarSolicitud(nuevaSolicitud)
    }

    // 3. Aceptar una solicitud que recibí
    suspend fun aceptarSolicitud(idAmigo: Int, miId: Int) {
        amidadDao.aceptarSolicitud(idAmigo, miId)
    }

    // 4. Ver mis amigos actuales (Aceptados)
    fun obtenerMisAmigos(miId: Int): Flow<List<Usuario>> {
        return amidadDao.obtenerMisAmigos(miId)
    }

    // 5. Ver solicitudes pendientes por aceptar
    fun obtenerSolicitudesPendientes(miId: Int): Flow<List<Usuario>> {
        return amidadDao.obtenerSolicitudesPendientes(miId)
    }

    // 6. Eliminar un amigo o rechazar solicitud
    suspend fun eliminarAmistad(miId: Int, idAmigo: Int) {
        amidadDao.eliminarAmistad(miId, idAmigo)
    }

    // 7. Saber el estado entre dos usuarios ("PENDIENTE", "ACEPTADA" o null)
    suspend fun obtenerEstadoAmistad(miId: Int, idAmigo: Int): String? {
        return amidadDao.obtenerEstadoAmistad(miId, idAmigo)
    }
}