package tts

import BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket

class Server(private val baseViewModel: BaseViewModel) {

    fun startSimpleTtsServer(port: Int = 9024) {
        val server = ServerSocket(port)
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                val socket = server.accept()
                handleClient(socket)
            }
        }
    }

    fun handleClient(client: Socket) {
        client.use {
            val reader = BufferedReader(InputStreamReader(client.getInputStream()))
            val writer = BufferedWriter(OutputStreamWriter(client.getOutputStream()))

            // Read request line and headers
            val headers = mutableListOf<String>()
            var line: String?
            var contentLength = 0

            while (reader.readLine().also { line = it } != null && line!!.isNotEmpty()) {
                headers.add(line)
                if (line.startsWith("Content-Length:", ignoreCase = true)) {
                    contentLength = line.substringAfter(":").trim().toInt()
                }
            }

            // Read body (text/plain)
            val body = CharArray(contentLength)
            reader.read(body, 0, contentLength)
            val text = String(body).replace("\n\n","\n")

            println("Received text: $text")
//            baseViewModel.updateTextValue("")
            baseViewModel.updateTextValue(text)
//            baseViewModel.stopTTS()
            baseViewModel.restartWholeSpeech()
            // Respond with a basic HTTP 200 OK
            val response = """
            HTTP/1.1 200 OK
            Content-Type: text/plain
            Content-Length: 2

            OK
        """.trimIndent()

            writer.write(response)
            writer.flush()
        }
    }
}