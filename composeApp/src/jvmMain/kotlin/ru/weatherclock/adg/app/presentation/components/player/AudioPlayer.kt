package ru.weatherclock.adg.app.presentation.components.player

import javazoom.jl.player.Player
import kotlinx.coroutines.runBlocking
import java.io.BufferedInputStream

actual object AudioPlayer {

    private fun play(byteArray: ByteArray) {
        val buffer = BufferedInputStream(
            byteArray.inputStream()
        )
        val mp3Player = Player(buffer)
        try {
            mp3Player.play()
        } catch (ex: Exception) {
            println("Error occured during playback process:" + ex.message)
        }
    }

    actual fun play(resource: ResourceWrapper) {
        runBlocking {
            play(resource.toByteArray())
        }
    }
}