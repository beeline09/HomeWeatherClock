package ru.weatherclock.adg.app.presentation.components.player

expect class AudioPlayer(playerState: PlayerState) {

    fun play()
    fun pause()
    fun next()
    fun prev()
    fun play(songIndex: Int)
    fun seekTo(time: Double)
    fun addSongsUrls(songsUrl: List<String>)
    fun cleanUp()

    fun play(resource: ResourceWrapper)
}