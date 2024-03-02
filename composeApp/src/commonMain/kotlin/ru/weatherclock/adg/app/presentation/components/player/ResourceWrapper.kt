package ru.weatherclock.adg.app.presentation.components.player

import homeweatherclock.composeapp.generated.resources.Res

class ResourceWrapper(val path: String) {

    suspend fun toByteArray(): ByteArray = Res.readBytes(path)
}