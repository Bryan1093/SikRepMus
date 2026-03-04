package com.example.sikrepmus

import android.app.Application
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Inicializar NewPipe
        NewPipe.init(SimpleDownloader())
    }
}

// Downloader simple para NewPipe
class SimpleDownloader : Downloader() {
    private val connectTimeout = TimeUnit.SECONDS.toMillis(30).toInt()
    private val readTimeout = TimeUnit.SECONDS.toMillis(30).toInt()

    @Throws(IOException::class, ReCaptchaException::class)
    override fun execute(request: Request): Response {
        val url = URL(request.url())
        val connection = url.openConnection() as HttpURLConnection

        connection.requestMethod = request.httpMethod()
        connection.connectTimeout = connectTimeout
        connection.readTimeout = readTimeout

        // Headers
        request.headers().forEach { (key, values) ->
            values.forEach { value ->
                connection.addRequestProperty(key, value)
            }
        }

        // Body (para POST)
        request.dataToSend()?.let { data ->
            connection.doOutput = true
            connection.outputStream.use { it.write(data) }
        }

        val responseCode = connection.responseCode
        val responseMessage = connection.responseMessage
        val responseHeaders = connection.headerFields

        val responseBody = try {
            connection.inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
        }

        return Response(
            responseCode,
            responseMessage,
            responseHeaders,
            responseBody,
            request.url()
        )
    }
}