
import java.awt.Dimension
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import HomeWeatherClock.composeApp.BuildConfig
import net.harawata.appdirs.AppDirsFactory
import ru.weatherclock.adg.App
import ru.weatherclock.adg.app.domain.di.initKoin
import ru.weatherclock.adg.platformSpecific.appStorage

fun main() = application {
    Window(
        title = "HomeWeatherClock",
        state = rememberWindowState(
            width = 800.dp,
            height = 600.dp,
            position = WindowPosition(
                Alignment.Center
            ),
            placement = WindowPlacement.Maximized
        ),
        onCloseRequest = ::exitApplication,
    ) {
        appStorage = AppDirsFactory.getInstance()
            .getUserDataDir(
                BuildConfig.APP_NAME,
                BuildConfig.APP_PACKAGE_NAME,
                BuildConfig.APP_AUTHOR
            )
        initKoin()
        /*        MenuBar {
                    Menu("MyMenu") {
                        Item(
                            "Preferences...",
                            shortcut = KeyShortcut(
                                Key.Comma,
                                meta = true
                            ),
                            onClick = {
                                println("Preferences menu action")
        //                    showPreferences = true
                            })
                    }
                }*/
        window.minimumSize = Dimension(
            350,
            600
        )
        App()
    }
}