package com.igdtuw.projectmatch.presentation.imageutils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object ImageUtils {
    fun bitmapToBase64(bitmap:Bitmap): String{
        val byteArrayOutputStream= ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray=byteArrayOutputStream.toByteArray()
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
    }

    fun base64ToBitmap(base64String: String): Bitmap?{
        return try {
            val decodedByte = Base64.decode(base64String,android.util.Base64.DEFAULT)
            val inputStream: InputStream = ByteArrayInputStream(decodedByte)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException){
            null
        }
    }
}