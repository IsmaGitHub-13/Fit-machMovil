package com.fic.mobile_app_base_compose.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

fun generarQR(contenido: String, tamano: Int = 512): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(contenido, BarcodeFormat.QR_CODE, tamano, tamano)
    val bitmap = Bitmap.createBitmap(tamano, tamano, Bitmap.Config.RGB_565)
    for (x in 0 until tamano) {
        for (y in 0 until tamano) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
        }
    }
    return bitmap
}

// El contenido del QR será: fitmatch://rutina/{creador}/{rutinaId}
fun generarContenidoQR(nombreUsuario: String, rutinaId: Int): String {
    return "fitmatch://rutina/$nombreUsuario/$rutinaId"
}

// Parsear el QR escaneado
fun parsearQR(contenido: String): Pair<String, Int>? {
    return try {
        if (!contenido.startsWith("fitmatch://rutina/")) return null
        val partes = contenido.removePrefix("fitmatch://rutina/").split("/")
        if (partes.size != 2) return null
        val creador = partes[0]
        val rutinaId = partes[1].toInt()
        Pair(creador, rutinaId)
    } catch (e: Exception) {
        null
    }
}