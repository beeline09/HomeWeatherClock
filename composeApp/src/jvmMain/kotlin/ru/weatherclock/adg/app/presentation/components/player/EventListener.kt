package ru.weatherclock.adg.app.presentation.components.player

import uk.co.caprica.vlcj.media.MediaRef
import uk.co.caprica.vlcj.media.TrackType
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventListener

class EventListener(private val playerState: PlayerState): MediaPlayerEventListener {

    override fun mediaChanged(
        mediaPlayer: MediaPlayer?,
        media: MediaRef?
    ) {
        playerState.isBuffering = true
    }

    override fun opening(mediaPlayer: MediaPlayer?) {
        playerState.isBuffering = true
    }

    override fun buffering(
        mediaPlayer: MediaPlayer?,
        newCache: Float
    ) {
        playerState.isBuffering = true
    }

    override fun playing(mediaPlayer: MediaPlayer?) {
        playerState.isBuffering = false
        playerState.isPlaying = true
    }

    override fun paused(mediaPlayer: MediaPlayer?) {
        playerState.isBuffering = false
        playerState.isPlaying = false
    }

    override fun stopped(mediaPlayer: MediaPlayer?) {
        playerState.isBuffering = false
        playerState.isPlaying = false
    }

    override fun forward(mediaPlayer: MediaPlayer?) {
        playerState.canNext = true
    }

    override fun backward(mediaPlayer: MediaPlayer?) {
        playerState.canPrev = true
    }

    override fun finished(mediaPlayer: MediaPlayer?) {

    }

    override fun timeChanged(
        mediaPlayer: MediaPlayer?,
        newTime: Long
    ) {
        playerState.currentTime = newTime
    }

    override fun positionChanged(
        mediaPlayer: MediaPlayer?,
        newPosition: Float
    ) {
        playerState.duration = newPosition.toLong()
    }

    override fun seekableChanged(
        mediaPlayer: MediaPlayer?,
        newSeekable: Int
    ) {

    }

    override fun pausableChanged(
        mediaPlayer: MediaPlayer?,
        newPausable: Int
    ) {

    }

    override fun titleChanged(
        mediaPlayer: MediaPlayer?,
        newTitle: Int
    ) {

    }

    override fun snapshotTaken(
        mediaPlayer: MediaPlayer?,
        filename: String?
    ) {

    }

    override fun lengthChanged(
        mediaPlayer: MediaPlayer?,
        newLength: Long
    ) {

    }

    override fun videoOutput(
        mediaPlayer: MediaPlayer?,
        newCount: Int
    ) {

    }

    override fun scrambledChanged(
        mediaPlayer: MediaPlayer?,
        newScrambled: Int
    ) {

    }

    override fun elementaryStreamAdded(
        mediaPlayer: MediaPlayer?,
        type: TrackType?,
        id: Int
    ) {
    }

    override fun elementaryStreamDeleted(
        mediaPlayer: MediaPlayer?,
        type: TrackType?,
        id: Int
    ) {
    }

    override fun elementaryStreamSelected(
        mediaPlayer: MediaPlayer?,
        type: TrackType?,
        id: Int
    ) {

    }

    override fun corked(
        mediaPlayer: MediaPlayer?,
        corked: Boolean
    ) {

    }

    override fun muted(
        mediaPlayer: MediaPlayer?,
        muted: Boolean
    ) {

    }

    override fun volumeChanged(
        mediaPlayer: MediaPlayer?,
        volume: Float
    ) {

    }

    override fun audioDeviceChanged(
        mediaPlayer: MediaPlayer?,
        audioDevice: String?
    ) {

    }

    override fun chapterChanged(
        mediaPlayer: MediaPlayer?,
        newChapter: Int
    ) {

    }

    override fun error(mediaPlayer: MediaPlayer?) {
        playerState.isPlaying = false
    }

    override fun mediaPlayerReady(mediaPlayer: MediaPlayer?) {

    }
}