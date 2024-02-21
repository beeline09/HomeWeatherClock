
import java.awt.Dimension
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import HomeWeatherClock.composeApp.BuildConfig
import com.jthemedetecor.OsThemeDetector
import homeweatherclock.composeapp.generated.resources.Res
import homeweatherclock.composeapp.generated.resources.app_window_name
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpFetcher
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.batikSvgDecoder
import io.kamel.image.config.resourcesFetcher
import net.harawata.appdirs.AppDirsFactory
import org.jetbrains.compose.resources.stringResource
import ru.weatherclock.adg.App
import ru.weatherclock.adg.app.domain.di.initKoin
import ru.weatherclock.adg.platformSpecific.appStorage
import ru.weatherclock.adg.platformSpecific.defaultHttpClientEngine

fun main() = application {
    appStorage = AppDirsFactory.getInstance().getUserDataDir(
        BuildConfig.APP_NAME,
        BuildConfig.APP_PACKAGE_NAME,
        BuildConfig.APP_AUTHOR
    )
    initKoin()
    Window(
        title = stringResource(Res.string.app_window_name),
        state = rememberWindowState(
            width = 1000.dp,
            height = 700.dp,
            position = WindowPosition(
                Alignment.Center
            ),
//            placement = WindowPlacement.Maximized
        ),
        onCloseRequest = ::exitApplication,
        undecorated = false,
    ) {
        window.minimumSize = Dimension(
            350,
            600
        )
        val kamelConfig = remember {
            KamelConfig {
                takeFrom(KamelConfig.Default)
                resourcesFetcher()
                batikSvgDecoder()
                httpFetcher(defaultHttpClientEngine)
            }
        }

        val isDark = remember { mutableStateOf(false) }
        val dark = isSystemInDarkTheme()
        LaunchedEffect(Unit) {
            isDark.value = dark
        }
        val detector = OsThemeDetector.getDetector()
        detector.registerListener {
            isDark.value = it
        }

        App(
            isDarkThemeSupported = true,
            kamelConfig = kamelConfig,
            systemIsDark = isDark.value
        )
    }
}