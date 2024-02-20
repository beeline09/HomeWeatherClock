package ru.weatherclock.adg.platformSpecific

import org.jetbrains.compose.resources.ExperimentalResourceApi
import ru.weatherclock.adg.app.presentation.components.player.ResourceWrapper

@OptIn(ExperimentalResourceApi::class)
expect fun String.rawResource(): ResourceWrapper