package com.miraitowa.shiguang.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageStorage {
    private const val TAG = "ShiguangImage"

    fun importFromUri(context: Context, uri: Uri): String? {
        val bitmap = runCatching {
            decodeWithImageDecoder(context, uri)
        }.onFailure {
            Log.w(TAG, "ImageDecoder failed, falling back to BitmapFactory", it)
        }.getOrNull() ?: runCatching {
            decodeWithBitmapFactory(context, uri)
        }.onFailure {
            Log.w(TAG, "Unable to decode selected image", it)
        }.getOrNull() ?: return null

        return runCatching {
            val directory = File(context.filesDir, "item_images").apply { mkdirs() }
            val file = File(directory, "img_${UUID.randomUUID().toString().take(12)}.jpg")
            FileOutputStream(file).use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, output)
            }
            if (!bitmap.isRecycled) bitmap.recycle()
            file.absolutePath
        }.onFailure {
            Log.w(TAG, "Unable to save imported image", it)
        }.getOrNull()
    }

    private fun decodeWithImageDecoder(context: Context, uri: Uri): Bitmap {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        return ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            val maxSide = 1200
            val largest = maxOf(info.size.width, info.size.height)
            if (largest > maxSide) {
                val scale = maxSide.toFloat() / largest
                decoder.setTargetSize(
                    (info.size.width * scale).toInt().coerceAtLeast(1),
                    (info.size.height * scale).toInt().coerceAtLeast(1),
                )
            }
        }
    }

    private fun decodeWithBitmapFactory(context: Context, uri: Uri): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, bounds)
        } ?: return null
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        var sample = 1
        while ((bounds.outWidth / sample) > 2400 || (bounds.outHeight / sample) > 2400) {
            sample *= 2
        }
        val options = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        return context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }
    }

    fun decodeSampled(path: String, maxSide: Int = 640): Bitmap? {
        if (!File(path).exists()) return null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
        var sample = 1
        while ((bounds.outWidth / sample) > maxSide * 2 || (bounds.outHeight / sample) > maxSide * 2) {
            sample *= 2
        }
        val options = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        return runCatching { BitmapFactory.decodeFile(path, options) }.getOrNull()
    }
}
