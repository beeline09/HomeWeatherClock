package ru.weatherclock.adg.app.presentation.components.player

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.runBlocking
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioPlayerDelegateProtocol
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.AVAudioSessionCategoryOptionMixWithOthers
import platform.AVFAudio.AVAudioSessionModeDefault
import platform.AVFAudio.setActive
import platform.darwin.NSObject
import platform.Foundation.NSData
import platform.Foundation.create

actual object AudioPlayer {

    // Плеер обязан жить всю длительность воспроизведения: локальная
    // переменная деаллоцируется Kotlin/Native сразу после выхода из
    // play() — звук обрывался после первых мс или не начинался вовсе.
    private var currentPlayer: AVAudioPlayer? = null

    // delegate у AVAudioPlayer weak — объект должен жить сам
    private val finishedDelegate = object : NSObject(), AVAudioPlayerDelegateProtocol {
        override fun audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully: Boolean) {
            if (currentPlayer === player) currentPlayer = null
        }
    }

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
                // MixWithOthers: бип как на Android — поверх музыки,
                // не прерывая аудио других приложений
                AVAudioSession.sharedInstance().setCategory(
                    category = AVAudioSessionCategoryPlayback,
                    mode = AVAudioSessionModeDefault,
                    options = AVAudioSessionCategoryOptionMixWithOthers,
                    error = null
                )
                AVAudioSession.sharedInstance().setActive(
                    active = true, error = null
                )
                currentPlayer?.stop()
                val player = AVAudioPlayer(
                    data = resource.toByteArray().toNsData(), error = null
                )
                player.delegate = finishedDelegate
                // Прогрев декодера — старт вывода без задержки
                player.prepareToPlay()
                player.play()
                currentPlayer = player
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
