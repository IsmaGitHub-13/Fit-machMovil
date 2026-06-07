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
    // Publicar rutina en Firebase para compartir por QR
    suspend fun publicarRutina(
        nombreUsuario: String,
        rutinaId: Int,
        nombre: String,
        descripcion: String,
        nivel: String,
        duracionMinutos: Int,
        ejercicios: List<Map<String, Any>>
    ): Result<Unit> {
        return try {
            val datos = hashMapOf(
                "idRutina" to rutinaId,
                "creador" to nombreUsuario,
                "nombre" to nombre,
                "descripcion" to descripcion,
                "nivel" to nivel,
                "duracionMinutos" to duracionMinutos,
                "ejercicios" to ejercicios
            )
            db.collection("rutinas")
                .document("${nombreUsuario}_$rutinaId")
                .set(datos).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener rutina de Firebase por creador e ID
    suspend fun obtenerRutina(nombreUsuario: String, rutinaId: Int): Result<Map<String, Any>?> {
        return try {
            val doc = db.collection("rutinas")
                .document("${nombreUsuario}_$rutinaId")
                .get().await()
            Result.success(if (doc.exists()) doc.data else null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener rutinas públicas de un amigo
    suspend fun obtenerRutinasDeAmigo(nombreUsuario: String): Result<List<Map<String, Any>>> {
        return try {
            val docs = db.collection("rutinas")
                .whereEqualTo("creador", nombreUsuario)
                .get().await()
            Result.success(docs.documents.mapNotNull { it.data })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

