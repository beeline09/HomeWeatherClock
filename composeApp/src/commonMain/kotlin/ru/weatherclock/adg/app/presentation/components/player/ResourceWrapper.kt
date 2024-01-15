package ru.weatherclock.adg.app.presentation.components.player

import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Resource
import org.jetbrains.compose.resources.resource

@ExperimentalResourceApi
class ResourceWrapper(val path: String) {

    val resource: Resource
        get() = resource(path)
}