package com.fic.mobile_app_base_compose.data.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

data class UsuarioFirebase(
    val nombreUsuario: String = "",
    val correo: String = ""
)

data class SolicitudFirebase(
    val de: String = "",
    val para: String = "",
    val estado: String = "pendiente" // pendiente, aceptada, rechazada
)

class FirebaseRepository {

    private val db = Firebase.firestore
    private val usuariosRef = db.collection("usuarios")
    private val solicitudesRef = db.collection("solicitudes")
    private val amigosRef = db.collection("amigos")

    // Registrar usuario en Firestore al hacer login
    suspend fun registrarUsuario(nombreUsuario: String, correo: String): Result<Unit> {
        return try {
            val datos = hashMapOf(
                "nombreUsuario" to nombreUsuario,
                "correo" to correo
            )
            usuariosRef.document(nombreUsuario).set(datos).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Buscar usuario por nombre
    suspend fun buscarUsuario(nombreUsuario: String): Result<UsuarioFirebase?> {
        return try {
            val doc = usuariosRef.document(nombreUsuario).get().await()
            if (doc.exists()) {
                Result.success(doc.toObject(UsuarioFirebase::class.java))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Enviar solicitud de amistad
    suspend fun enviarSolicitud(deMiUsuario: String, paraNombreUsuario: String): Result<Unit> {
        return try {
            val solicitudId = "${deMiUsuario}_${paraNombreUsuario}"
            val datos = hashMapOf(
                "de" to deMiUsuario,
                "para" to paraNombreUsuario,
                "estado" to "pendiente"
            )
            solicitudesRef.document(solicitudId).set(datos).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener solicitudes pendientes que me llegaron
    suspend fun obtenerSolicitudesPendientes(miUsuario: String): Result<List<SolicitudFirebase>> {
        return try {
            val docs = solicitudesRef
                .whereEqualTo("para", miUsuario)
                .whereEqualTo("estado", "pendiente")
                .get().await()
            val solicitudes = docs.toObjects(SolicitudFirebase::class.java)
            Result.success(solicitudes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Aceptar solicitud
    suspend fun aceptarSolicitud(deMiUsuario: String, paraNombreUsuario: String): Result<Unit> {
        return try {
            val solicitudId = "${deMiUsuario}_${paraNombreUsuario}"
            // Actualizar estado de solicitud
            solicitudesRef.document(solicitudId)
                .update("estado", "aceptada").await()
            // Crear relación de amistad en ambas direcciones
            amigosRef.document("${deMiUsuario}_${paraNombreUsuario}")
                .set(hashMapOf("usuario1" to deMiUsuario, "usuario2" to paraNombreUsuario)).await()
            amigosRef.document("${paraNombreUsuario}_${deMiUsuario}")
                .set(hashMapOf("usuario1" to paraNombreUsuario, "usuario2" to deMiUsuario)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener lista de amigos
    suspend fun obtenerAmigos(miUsuario: String): Result<List<String>> {
        return try {
            val docs = amigosRef
                .whereEqualTo("usuario1", miUsuario)
                .get().await()
            val amigos = docs.documents.map { it.getString("usuario2") ?: "" }
            Result.success(amigos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Rechazar solicitud
    suspend fun rechazarSolicitud(deMiUsuario: String, paraNombreUsuario: String): Result<Unit> {
        return try {
            val solicitudId = "${deMiUsuario}_${paraNombreUsuario}"
            solicitudesRef.document(solicitudId)
                .update("estado", "rechazada").await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

