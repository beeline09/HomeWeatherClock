package ru.weatherclock.adg.app.presentation.components.player

import homeweatherclock.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

@ExperimentalResourceApi
class ResourceWrapper(val path: String) {

    suspend fun toByteArray(): ByteArray = Res.readBytes(path)
}