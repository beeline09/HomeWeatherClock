package ru.weatherclock.adg.app.presentation.components.player

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.runBlocking
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.AVAudioSessionModeDefault
import platform.AVFAudio.setActive
import platform.Foundation.NSData
import platform.Foundation.create

actual object AudioPlayer {

    private fun ByteArray.toNsData(): NSData {
        return this.usePinned {
            NSData.create(
                it.addressOf(0), this.size.convert()
            )
        }
    }

    actual fun play(resource: ResourceWrapper) {
        runBlocking {
            try {
                AVAudioSession.sharedInstance().setCategory(
                    category = AVAudioSessionCategoryPlayback,
                    mode = AVAudioSessionModeDefault,
                    options = 0u,
                    error = null
                )
                AVAudioSession.sharedInstance().setActive(
                    active = true, error = null
                )
                val player = AVAudioPlayer(
                    data = resource.toByteArray().toNsData(), error = null
                )
                player.play()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}