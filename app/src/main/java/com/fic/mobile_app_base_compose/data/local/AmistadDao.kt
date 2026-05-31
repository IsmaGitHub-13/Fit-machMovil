package com.fic.mobile_app_base_compose.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fic.mobile_app_base_compose.data.model.Amistad
import com.fic.mobile_app_base_compose.data.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface AmistadDao {

    // 1. Enviar una solicitud de amistad (Se guarda en 'PENDIENTE' por defecto)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enviarSolicitud(amistad: Amistad)

    // 2. Aceptar solicitud (Cambiamos el estado a 'ACEPTADA')
    @Query("""
        UPDATE amistades 
        SET estado = 'ACEPTADA' 
        WHERE id_usuario_origen = :idAmigo AND id_usuario_destino = :miId
    """)
    suspend fun aceptarSolicitud(idAmigo: Int, miId: Int)

    // 3. Ver las solicitudes que tengo PENDIENTES por aceptar
    // Trae los datos del Usuario que me envió la solicitud para mostrarlos en una lista
    @Query("""
        SELECT * FROM usuarios 
        WHERE id_usuario IN (
            SELECT id_usuario_origen FROM amistades 
            WHERE id_usuario_destino = :miId AND estado = 'PENDIENTE'
        )
    """)
    fun obtenerSolicitudesPendientes(miId: Int): Flow<List<Usuario>>

    // 4. Obtener mi lista de amigos reales (Aquellos con estado 'ACEPTADA')
    // Busca tanto si yo mandé la solicitud y la aceptaron, como si me la mandaron y la acepté
    @Query("""
        SELECT * FROM usuarios 
        WHERE id_usuario IN (
            SELECT id_usuario_destino FROM amistades WHERE id_usuario_origen = :miId AND estado = 'ACEPTADA'
            UNION
            SELECT id_usuario_origen FROM amistades WHERE id_usuario_destino = :miId AND estado = 'ACEPTADA'
        )
    """)
    fun obtenerMisAmigos(miId: Int): Flow<List<Usuario>>

    // 5. Eliminar o rechazar una amistad/solicitud
    @Query("""
        DELETE FROM amistades 
        WHERE (id_usuario_origen = :miId AND id_usuario_destino = :idAmigo)
        OR (id_usuario_origen = :idAmigo AND id_usuario_destino = :miId)
    """)
    suspend fun eliminarAmistad(miId: Int, idAmigo: Int)

    // 6. Verificar el estado actual entre dos usuarios (Útil para cambiar el texto del botón a "Agregar", "Pendiente" o "Amigos")
    @Query("""
        SELECT estado FROM amistades 
        WHERE (id_usuario_origen = :miId AND id_usuario_destino = :idAmigo)
        OR (id_usuario_origen = :idAmigo AND id_usuario_destino = :miId)
        LIMIT 1
    """)
    suspend fun obtenerEstadoAmistad(miId: Int, idAmigo: Int): String?
}