package ru.weatherclock.adg.app.presentation.components.weather

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import ru.weatherclock.adg.app.data.dto.WeatherConfig
import ru.weatherclock.adg.app.domain.model.forecast.DayDetail
import ru.weatherclock.adg.app.domain.model.forecast.iconUrl

@Composable
fun ColumnScope.WeatherIcon(
    detail: DayDetail,
    weatherConfigData: WeatherConfig,
    isPreview: Boolean = false
) {
    if (isPreview) {
        val p = rememberVectorPainter(image = Icons.Filled.Error)
        Icon(
            painter = rememberVectorPainter(image = Icons.Filled.Error),
            contentDescription = "Erro",
            modifier = Modifier
                .aspectRatio(
                    ratio = p.intrinsicSize.height /
                            p.intrinsicSize.width
                )
                .padding(5.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
        )
    } else {
        val model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(data = detail.iconUrl(weatherConfig = weatherConfigData))
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()
        val painter = rememberAsyncImagePainter(model = model)
        val state by painter.state.collectAsState()
        when (val currentState = state) {
            is AsyncImagePainter.State.Error -> {
                println("WeatherIcon load failed: ${detail.iconUrl(weatherConfig = weatherConfigData)}")
                currentState.result.throwable.printStackTrace()
                val p1 = rememberVectorPainter(image = Icons.Filled.Error)
                Icon(
                    painter = rememberVectorPainter(image = Icons.Filled.Error),
                    contentDescription = "Erro",
                    modifier = Modifier
                        .aspectRatio(
                            ratio = p1.intrinsicSize.height /
                                    p1.intrinsicSize.width
                        )
                        .padding(5.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                )
            }

            is AsyncImagePainter.State.Empty,
            is AsyncImagePainter.State.Loading -> CircularProgressIndicator(
                modifier = Modifier
                    .aspectRatio(
                        ratio = 1f
                    )
                    .padding(5.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
            )

            is AsyncImagePainter.State.Success -> Image(
                painter = painter,
                contentDescription = "${detail}_WeatherIcon_${detail.icon}",
                modifier = Modifier.run {
                    val r = painter.intrinsicSize.height / painter.intrinsicSize.width
                    if (r > 0) {
                        aspectRatio(r)
                    } else this
                }
                    .padding(5.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                // Без colorFilter.tint: иконки OpenWeatherMap изначально
                // цветные (жёлтое солнце, серые облака, синий дождь)
                contentScale = ContentScale.Fit
            )

            AsyncImagePainter.State.Empty -> {}
        }
    }
}