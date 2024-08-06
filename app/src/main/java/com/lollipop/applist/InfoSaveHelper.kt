package com.lollipop.applist

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object InfoSaveHelper {

    private val sdf by lazy {
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault())
    }

    fun save(
        context: Context,
        name: String = "",
        infoProvider: () -> String,
        onEnd: (String) -> Unit
    ) {
        val fileName = name.ifEmpty {
            "Info-${sdf.format(Date(System.currentTimeMillis()))}"
        }
        Thread {
            saveImpl(context, infoProvider(), "$fileName.txt", onEnd)
        }.start()
    }

    private fun saveImpl(
        context: Context,
        content: String,
        fileName: String,
        onEnd: (String) -> Unit
    ) {
        var result = ""
        try {
            val file = File(context.filesDir, fileName)
            if (file.exists()) {
                file.delete()
            }
            val outputStream = FileOutputStream(file)
            try {
                outputStream.write(content.toByteArray())
                outputStream.flush()
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                outputStream.close()
            }

            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".file.save.provider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            saveToMediaStore(content, context, fileName)
            Handler(Looper.getMainLooper()).post {
                try {
                    context.startActivity(intent)
                } catch (e: Throwable) {
                    e.printStackTrace()
                    result = e.toString()
                }
                onEnd(result)
            }
            return
        } catch (e: Throwable) {
            e.printStackTrace()
            result = e.toString()
        }
        Handler(Looper.getMainLooper()).post {
            onEnd("Error: $result")
        }
    }

    private fun saveToMediaStore(content: String, context: Context, fileName: String) {
        try {
            val contentResolver = context.contentResolver
            val contentValues = android.content.ContentValues()
            contentValues.put(
                MediaStore.Images.Media.DISPLAY_NAME,
                fileName
            )
            contentValues.put(
                MediaStore.Images.Media.MIME_TYPE,
                "text/plain"
            )
            contentValues.put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS
            )
            val uri =
                contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
            val out = contentResolver.openOutputStream(uri!!)
            try {
                out!!.write(content.toByteArray())
                out.flush()
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                out?.close()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

}