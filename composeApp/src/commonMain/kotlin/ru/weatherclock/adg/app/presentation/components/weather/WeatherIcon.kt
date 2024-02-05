package ru.weatherclock.adg.app.presentation.components.weather

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.kamel.core.Resource
import io.kamel.image.asyncPainterResource
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
    } else when (val p =
        asyncPainterResource(data = detail.iconUrl(weatherConfig = weatherConfigData))) {
        is Resource.Failure -> {
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

        is Resource.Loading -> CircularProgressIndicator(
            p.progress,
            modifier = Modifier
                .aspectRatio(
                    ratio = 1f
                )
                .padding(5.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
        )

        is Resource.Success -> Image(
            painter = p.value,
            contentDescription = "${detail}_WeatherIcon_${detail.icon}",
            modifier = Modifier
                .aspectRatio(
                    ratio = p.value.intrinsicSize.height /
                            p.value.intrinsicSize.width
                )
                .padding(5.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            contentScale = ContentScale.Fit
        )
    }
}