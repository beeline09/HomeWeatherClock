@file:OptIn(
    ExperimentalResourceApi::class,
    ExperimentalResourceApi::class,
    ExperimentalResourceApi::class
)

package ru.weatherclock.adg.app.presentation.components.player

import java.io.BufferedInputStream
import kotlinx.coroutines.runBlocking
import javazoom.jl.player.Player
import org.jetbrains.compose.resources.ExperimentalResourceApi
import uk.co.caprica.vlcj.player.base.MediaPlayer

@OptIn(ExperimentalResourceApi::class)
actual class AudioPlayer actual constructor(
    private val playerState: PlayerState
) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentItemIndex = -1
    private lateinit var timeObserver: EventListener
    private val playerItems = mutableListOf<String>()

    init {
        /*        NativeDiscovery().discover()

                mediaPlayer =
                        // see https://github.com/caprica/vlcj/issues/887#issuecomment-503288294 for why we're using CallbackMediaPlayerComponent for macOS.
                    (if (RuntimeUtil.isMac()) {
                        CallbackMediaPlayerComponent()
                    } else {
                        EmbeddedMediaPlayerComponent()
                    }).mediaPlayerFactory().mediaPlayers().newMediaPlayer()*/

        playerState.isPlaying = mediaPlayer?.status()?.isPlaying == true
//        javazoom.jl.player.jlp.main(arrayOf("-url https://cdn.pixabay.com/download/audio/2022/12/28/audio_e232e79ed8.mp3"))

//        val audioFilePath = "AudioFileWithMp3Format.mp3"
//        val player: SoundPlayerUsingJavaZoom = SoundPlayerUsingJavaZoom()
    }

    actual fun play() {
        if (currentItemIndex == -1) {
            play(0)
        } else {
            mediaPlayer?.controls()?.play()
            playerState.isPlaying = true
        }
    }

    actual fun pause() {
        mediaPlayer?.controls()?.pause()
    }

    actual fun next() {
        playerState.canNext = (currentItemIndex + 1) < playerItems.size
        if (playerState.canNext) {
            currentItemIndex += 1
            playWithIndex(currentItemIndex)
        }
    }

    actual fun prev() {
        when {
            playerState.currentTime > 3 -> {
                seekTo(0.0)
            }

            else -> {
                playerState.canPrev = (currentItemIndex - 1) >= 0
                if (playerState.canPrev) {
                    currentItemIndex -= 1
                    playWithIndex(currentItemIndex)
                }
            }
        }
    }

    actual fun play(songIndex: Int) {
        playerState.isBuffering = true
        if (songIndex < playerItems.size) {
            currentItemIndex = songIndex
            playWithIndex(currentItemIndex)
        }
    }

    actual fun seekTo(time: Double) {
        mediaPlayer?.controls()?.setPosition(time.toFloat())
        playerState.isBuffering = true
    }

    actual fun addSongsUrls(songsUrl: List<String>) {
        playerItems.addAll(songsUrl)
    }

    actual fun cleanUp() {
        stop()
    }

    private val eventListener = EventListener(playerState)

    private fun startTimeObserver() {
        timeObserver = eventListener
        mediaPlayer?.events()?.addMediaPlayerEventListener(timeObserver)
    }

    private fun stop() {
        if (::timeObserver.isInitialized) {
            mediaPlayer?.events()?.removeMediaPlayerEventListener(timeObserver)
        }
        mediaPlayer?.controls()?.pause()
        mediaPlayer?.controls()?.setPosition(0f)
    }

    private fun playWithIndex(currentItemIndex: Int) {
        stop()
        startTimeObserver()
        playerState.isBuffering = true
        playerState.currentItemIndex = currentItemIndex
        val playItem = playerItems[currentItemIndex]
        mediaPlayer?.media()?.play(playItem)
    }

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
            play(resource.resource.readBytes())
        }
    }
}