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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
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
import homeweatherclock.composeapp.generated.resources.Res
import ru.weatherclock.adg.app.data.dto.WeatherConfig
import ru.weatherclock.adg.app.data.dto.WeatherIconSet
import ru.weatherclock.adg.app.data.dto.WeatherServer
import ru.weatherclock.adg.app.domain.model.forecast.DayDetail
import ru.weatherclock.adg.app.domain.model.forecast.WeatherIconResolver

/**
 * Данные для coil-запроса иконки погоды: байты bundled-SVG из ресурсов
 * приложения либо URL сети (незнакомый код / набор Official / CDN).
 * Пока читается локальный ресурс, возвращает null (иконка в состоянии Loading);
 * сеть используется только если bundled-чтение реально не удалось.
 */
@Composable
fun rememberWeatherIconRequestData(
    code: String,
    server: WeatherServer,
    iconSet: WeatherIconSet
): Any? {
    val bundled = remember(code, server, iconSet) {
        WeatherIconResolver.bundled(set = iconSet, server = server, code = code)
    }
    val network = remember(code, server, iconSet) {
        WeatherIconResolver.network(set = iconSet, server = server, code = code)
    }
    return produceState<Any?>(initialValue = null, bundled, network) {
        value = when (val b = bundled) {
            is WeatherIconResolver.Source.BundledSvg ->
                runCatching { Res.readBytes(b.resourcePath) }.getOrNull()
                    ?: (network as? WeatherIconResolver.Source.Network)?.url

            null -> (network as? WeatherIconResolver.Source.Network)?.url
        }
    }.value
}

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
        val context = LocalPlatformContext.current
        val data = rememberWeatherIconRequestData(
            code = detail.icon,
            server = weatherConfigData.server,
            iconSet = weatherConfigData.iconSet
        )
        // Локальный ресурс ещё читается: показываем загрузку, не создавая
        // coil-запрос с null (NullRequestDataException)
        if (data == null) {
            CircularProgressIndicator(
                modifier = Modifier
                    .aspectRatio(ratio = 1f)
                    .padding(5.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
            )
            return
        }
        val model = remember(context, data) {
            ImageRequest.Builder(context)
                .data(data = data)
                .crossfade(true)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()
        }
        val painter = rememberAsyncImagePainter(model = model)
        val state by painter.state.collectAsState()
        when (val currentState = state) {
            is AsyncImagePainter.State.Error -> {
                println(
                    "WeatherIcon load failed: code=${detail.icon}, " +
                        "set=${weatherConfigData.iconSet}, server=${weatherConfigData.server}"
                )
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
                    // Без colorFilter.tint: иконки наборов изначально цветные
                    contentScale = ContentScale.Fit
                )

            AsyncImagePainter.State.Empty -> {}
        }
    }
}
