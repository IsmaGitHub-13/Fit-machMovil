package com.fic.mobile_app_base_compose.data.repository

import com.fic.mobile_app_base_compose.data.local.SolicitudAmistadDao
import com.fic.mobile_app_base_compose.data.model.SolicitudAmistad
import kotlinx.coroutines.flow.Flow

class SolicitudAmistadRepository(private val dao: SolicitudAmistadDao) {

    fun obtenerSolicitudesPendientes(idUsuario: Int): Flow<List<SolicitudAmistad>> =
        dao.obtenerSolicitudesPendientes(idUsuario)

    fun obtenerIdsAmigos(idUsuario: Int): Flow<List<Int>> =
        dao.obtenerIdsAmigos(idUsuario)

    suspend fun enviarSolicitud(idRemitente: Int, idDestinatario: Int): Result<Unit> {
        return try {
            val yaExiste = dao.existeRelacion(idRemitente, idDestinatario)
            if (yaExiste > 0) return Result.failure(Exception("Ya existe una solicitud o ya son amigos"))
            dao.enviarSolicitud(SolicitudAmistad(
                idRemitente = idRemitente,
                idDestinatario = idDestinatario
            ))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun aceptarSolicitud(idSolicitud: Int) =
        dao.actualizarEstado(idSolicitud, "aceptada")

    suspend fun rechazarSolicitud(idSolicitud: Int) =
        dao.actualizarEstado(idSolicitud, "rechazada")
}