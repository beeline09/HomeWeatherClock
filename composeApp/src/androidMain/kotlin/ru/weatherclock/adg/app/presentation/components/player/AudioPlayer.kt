package ru.weatherclock.adg.app.presentation.components.player

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import android.annotation.TargetApi
import android.media.MediaDataSource
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import ru.weatherclock.adg.AndroidApp

actual class AudioPlayer actual constructor(
    private val playerState: PlayerState
): Runnable {

    private val handler = Handler(Looper.getMainLooper())

    private val mediaPlayer = ExoPlayer.Builder(AndroidApp.INSTANCE).build()
    private val mediaItems = mutableListOf<MediaItem>()
    private var currentItemIndex = -1
    private val listener = object: Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE -> {
                }

                Player.STATE_BUFFERING -> {
                    playerState.isBuffering = true
                }

                Player.STATE_ENDED -> {
                    if (playerState.isPlaying) {
                        next()
                    }
                }

                Player.STATE_READY -> {
                    playerState.isBuffering = false
                    playerState.duration = mediaPlayer.duration / 1000
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            playerState.isPlaying = isPlaying
            if (isPlaying) {
                scheduleUpdate()
            } else {
                stopUpdate()
            }
        }
    }

    private fun stopUpdate() {
        handler.removeCallbacks(this)
    }

    private fun scheduleUpdate() {
        stopUpdate()
        handler.postDelayed(
            this,
            100
        )
    }

    actual fun play() {
        if (currentItemIndex == -1) {
            play(0)
        } else {
            mediaPlayer.play()
        }
    }

    actual fun pause() {
        mediaPlayer.pause()
    }

    actual fun addSongsUrls(songsUrl: List<String>) {
        mediaItems += songsUrl.map { MediaItem.fromUri(it) }
        mediaPlayer.addListener(listener)
        mediaPlayer.prepare()
    }

    actual fun next() {
        playerState.canNext = (currentItemIndex + 1) < mediaItems.size
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
        if (songIndex < mediaItems.size) {
            currentItemIndex = songIndex
            playWithIndex(currentItemIndex)
        }
    }

    actual fun seekTo(time: Double) {
        val seekTime = time * 1000
        mediaPlayer.seekTo(seekTime.toLong())
    }

    private fun playWithIndex(index: Int) {
        playerState.currentItemIndex = index
        val playItem = mediaItems[index]
        mediaPlayer.setMediaItem(playItem)
        mediaPlayer.play()
    }

    override fun run() {
        playerState.currentTime = mediaPlayer.currentPosition / 1000
        handler.postDelayed(
            this,
            1000
        )
    }

    actual fun cleanUp() {
        mediaPlayer.release()
        mediaPlayer.removeListener(listener)
    }

    actual fun play(byteArray: ByteArray) {
        try {
            val path = File(AndroidApp.INSTANCE.cacheDir.path + "/musicfile.mp3")
            val fos = FileOutputStream(path)
            fos.write(byteArray)
            fos.close()

            val mediaPlayer = MediaPlayer()

            val fis = FileInputStream(path)
            mediaPlayer.setDataSource(AndroidApp.INSTANCE.cacheDir.path + "/musicfile.mp3")

            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (ex: IOException) {
            val s = ex.toString()
            ex.printStackTrace()
        }
    }

}

@TargetApi(Build.VERSION_CODES.M)
class ByteArrayMediaDataSource(private val data: ByteArray): MediaDataSource() {

    @Throws(IOException::class)
    override fun readAt(
        position: Long,
        buffer: ByteArray,
        offset: Int,
        size: Int
    ): Int {
        System.arraycopy(
            data,
            position.toInt(),
            buffer,
            offset,
            size
        )
        return size
    }

    @Throws(IOException::class)
    override fun getSize(): Long {
        return data.size.toLong()
    }

    @Throws(IOException::class)
    override fun close() {
        // Nothing to do here
    }
}