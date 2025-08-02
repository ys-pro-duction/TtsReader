package utils

import tts.TTSModel
import java.io.File
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.util.zip.ZipInputStream

class ModelDownloader(
    private val listener: ModelDownloaderListener
) {
    interface ModelDownloaderListener {
        fun onDownloadStart()
        fun onDownloadComplete()
        fun onDownloadError(errorMsg: String)
        fun onDownloadProgress(_downloadedSize: Long)
        fun onProgress(progress: Float)
        fun onModelSize(size: Long)
        fun onUnzippedFileName(name: String)
    }

    suspend fun run() {
        println("download: Start")
        listener.onDownloadStart()
        try {
            val connection = URI.create(TTSModel.url).toURL().openConnection()
            val size = connection.contentLengthLong
            listener.onModelSize(size/1024/1024)
            connection.getInputStream().use { input ->
                unzipFile(input, File(TTSModel.modelDir.absolutePath),size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            listener.onDownloadError("Download failed: ${e.message}")
            return
        }
    }

    private suspend fun unzipFile(inputStream: InputStream, targetDir: File, totalSize: Long) {
        var downloadedSize: Long = 0
        ZipInputStream(inputStream).use { zipIn ->
            var entry = zipIn.nextEntry
            while (entry != null) {
                val outFile = File(targetDir, entry.name)
                if (entry.isDirectory) {
                    outFile.mkdirs()
                } else {
                    listener.onUnzippedFileName(outFile.name)
                    outFile.outputStream().use {
                        while (zipIn.available() == 1){
                            val buffer = ByteArray(1024*1024)
                            val bytesRead = zipIn.read(buffer)
                            if (bytesRead > 0) {
                                it.write(buffer, 0, bytesRead)
                                downloadedSize += bytesRead
                                listener.onDownloadProgress(downloadedSize/1024/1024)
                                listener.onProgress("%.2f".format(downloadedSize.toFloat()/totalSize).toFloat())
                            }
                        }
                    }

                }
                zipIn.closeEntry()
                entry = zipIn.nextEntry
            }
        }
        File(targetDir,"DownloadedProof").createNewFile()
        listener.onDownloadComplete()
    }
}