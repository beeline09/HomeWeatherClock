
import HomeWeatherClock.composeApp.BuildConfig
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.icerock.moko.resources.compose.stringResource
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.batikSvgDecoder
import io.kamel.image.config.resourcesFetcher
import net.harawata.appdirs.AppDirsFactory
import ru.weatherclock.adg.App
import ru.weatherclock.adg.MR
import ru.weatherclock.adg.app.domain.di.initKoin
import ru.weatherclock.adg.platformSpecific.appStorage
import java.awt.Dimension

fun main() = application {
    Window(
        title = stringResource(MR.strings.app_window_name),
        state = rememberWindowState(
            width = 900.dp,
            height = 700.dp,
            position = WindowPosition(
                Alignment.Center
            ),
//            placement = WindowPlacement.Maximized
        ),
        onCloseRequest = ::exitApplication,
        undecorated = false,
    ) {
        appStorage = AppDirsFactory.getInstance()
            .getUserDataDir(
                BuildConfig.APP_NAME,
                BuildConfig.APP_PACKAGE_NAME,
                BuildConfig.APP_AUTHOR
            )
        initKoin()
        window.minimumSize = Dimension(
            350,
            600
        )
        val kamelConfig = remember {
            KamelConfig {
                takeFrom(KamelConfig.Default)
                resourcesFetcher()
                batikSvgDecoder()
            }
        }
        App(isDarkThemeSupported = true, kamelConfig = kamelConfig)
    }
}