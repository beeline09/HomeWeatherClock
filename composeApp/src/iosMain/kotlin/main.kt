import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import ru.weatherclock.adg.App

// Иконки погоды грузятся coil3 (KMP): глобальная инициализация
// image-loader'а не нужна — прежний qdsfdhvh-лоадер никто не читал
fun MainViewController(): UIViewController = ComposeUIViewController {
    App(isDarkThemeSupported = true)
}
