package com.project.spire.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.provider.MediaStore
import android.util.Base64
import androidx.core.content.ContextCompat
import com.example.spire.R
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


object BitmapUtils {


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
    fun BitmaptoBase64String(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val bytes = baos.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    /**
     * Bitmap을 Base64로 변환 */
    fun BitmaptoBase64ByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encode(byteArray, Base64.DEFAULT)
    }

    /**
     * Bitmap을 파일로 저장
     * Activity에서 this.contentResolver를 parameter로 전달 */
    fun saveImageOnAboveAndroidQ(bitmap: Bitmap, contentResolver: ContentResolver) {

        val fileName = System.currentTimeMillis().toString() + ".png"
        val contentValues = ContentValues()
        contentValues.apply {
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/ImageSave")
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            if (uri != null) {
                val image = contentResolver.openFileDescriptor(uri, "w", null)
                if (image != null) {
                    val fos = FileOutputStream(image.fileDescriptor)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    fos.close()

                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(uri, contentValues, null, null)
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun maskTransparentToBlack(originBitmap: Bitmap): Bitmap {
        val width = originBitmap.width
        val height = originBitmap.height
        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.BLACK)

        val paint = Paint()
        val filter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        paint.colorFilter = filter
        canvas.drawBitmap(originBitmap, 0f, 0f, paint)
        return newBitmap
    }

    fun maskBlackToTransparent(originBitmap: Bitmap, color: Int? = null): Bitmap {
        val width = originBitmap.width
        val height = originBitmap.height
        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.TRANSPARENT)

        for (x in 0 until width) {
            for (y in 0 until height) {
                if (originBitmap.getPixel(x, y) != Color.BLACK) {
                    if (color == null) {
                        newBitmap.setPixel(x, y, originBitmap.getPixel(x, y))
                    } else {
                        newBitmap.setPixel(x, y, color)
                    }
                }
            }
        }
        return newBitmap
    }
}