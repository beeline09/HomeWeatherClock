package ru.weatherclock.adg.app.presentation.components.player

import android.media.MediaPlayer
import kotlinx.coroutines.runBlocking
import ru.weatherclock.adg.AndroidApp
import java.io.File
import java.io.FileOutputStream

actual object AudioPlayer {

    private fun ByteArray.file(path: String): File {
        val fileName = path.substringAfterLast("/")
        val file = File("${AndroidApp.INSTANCE.cacheDir}/$fileName")
        if (file.exists() && file.length() < 100) {
            file.delete()
        }
        if (!file.exists()) {
            val fos = FileOutputStream(file)
            fos.write(this)
            fos.close()
        }
        return file
    }

    actual fun play(resource: ResourceWrapper) {
        runBlocking {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(resource.toByteArray().file(resource.path).path)
            mediaPlayer.prepare()
            mediaPlayer.start()
        }
    }

}