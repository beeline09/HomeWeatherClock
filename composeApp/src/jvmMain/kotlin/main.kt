import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension
import ru.weatherclock.adg.App
import ru.weatherclock.adg.app.domain.di.initKoin

fun main() = application {
    Window(
        title = "HomeWeatherClock",
        state = rememberWindowState(width = 800.dp, height = 600.dp),
        onCloseRequest = ::exitApplication,
    ) {
        initKoin("http://dataservice.accuweather.com")
        window.minimumSize = Dimension(350, 600)
        App()
    }
}