package ru.weatherclock.adg.platformSpecific

import org.jetbrains.compose.resources.ExperimentalResourceApi
import ru.weatherclock.adg.app.presentation.components.player.ResourceWrapper

@OptIn(ExperimentalResourceApi::class)
actual fun String.rawResource(): ResourceWrapper {
    return ResourceWrapper("files/${this@rawResource}")
}