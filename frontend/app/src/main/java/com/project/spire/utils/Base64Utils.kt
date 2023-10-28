package com.project.spire.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream


object Base64Utils {

    /**
     * Base64 String을 Bitmap으로 변환 */
    fun Base64toBitmap(encodedString: String?): Bitmap? {
        return try {
            val encodeByte: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            e.message
            null
        }
    }

    /**
     * Bitmap을 Base64 String형으로 변환 */
    fun BitmaptoBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val bytes = baos.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }
}