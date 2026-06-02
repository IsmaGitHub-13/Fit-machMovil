package com.fic.mobile_app_base_compose.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fic.mobile_app_base_compose.data.model.SolicitudAmistad
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudAmistadDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun enviarSolicitud(solicitud: SolicitudAmistad)

    @Query("SELECT * FROM solicitudes_amistad WHERE id_destinatario = :idUsuario AND estado = 'pendiente'")
    fun obtenerSolicitudesPendientes(idUsuario: Int): Flow<List<SolicitudAmistad>>

    @Query("UPDATE solicitudes_amistad SET estado = :estado WHERE id_solicitud = :idSolicitud")
    suspend fun actualizarEstado(idSolicitud: Int, estado: String)

    @Query("""
        SELECT COUNT(*) FROM solicitudes_amistad 
        WHERE ((id_remitente = :idA AND id_destinatario = :idB) 
            OR (id_remitente = :idB AND id_destinatario = :idA))
        AND estado != 'rechazada'
    """)
    suspend fun existeRelacion(idA: Int, idB: Int): Int

    @Query("""
        SELECT id_remitente FROM solicitudes_amistad 
        WHERE id_destinatario = :idUsuario AND estado = 'aceptada'
        UNION
        SELECT id_destinatario FROM solicitudes_amistad 
        WHERE id_remitente = :idUsuario AND estado = 'aceptada'
    """)
    fun obtenerIdsAmigos(idUsuario: Int): Flow<List<Int>>
}