package ru.weatherclock.adg.app.presentation.components.weather

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import ru.weatherclock.adg.app.domain.model.forecast.Detail
import ru.weatherclock.adg.app.domain.model.forecast.flatSvgIconUrl

@Composable
fun ColumnScope.WeatherIcon(
    detail: Detail,
    isPreview: Boolean = false
) {
    val m = Modifier
        .fillMaxSize()
        .padding(
            bottom = 5.dp,
            top = 5.dp
        )
    if (isPreview) {
        Icon(
            painter = rememberVectorPainter(image = Icons.Filled.Error),
            contentDescription = "Erro",
            modifier = m
        )
    } else when (val p = asyncPainterResource(data = detail.flatSvgIconUrl())) {
        is Resource.Failure -> Icon(
            painter = rememberVectorPainter(image = Icons.Filled.Error),
            contentDescription = "Erro",
            modifier = m
        )

        is Resource.Loading -> CircularProgressIndicator(
            p.progress,
            modifier = m
        )

        is Resource.Success -> Image(
            painter = p.value,
            contentDescription = "${detail.detailType}_WeatherIcon_${detail.icon}",
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