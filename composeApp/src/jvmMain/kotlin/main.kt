import java.awt.Dimension
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ru.weatherclock.adg.App
import ru.weatherclock.adg.app.domain.di.initKoin

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
        initKoin()
        MenuBar {
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
        }
        window.minimumSize = Dimension(
            350,
            600
        )
        App()
    }
}