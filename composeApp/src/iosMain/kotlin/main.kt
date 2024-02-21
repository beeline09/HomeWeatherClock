import kotlinx.cinterop.ExperimentalForeignApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpFetcher
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import okio.Path.Companion.toPath
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIViewController
import ru.weatherclock.adg.App
import ru.weatherclock.adg.app.domain.util.commonConfig
import ru.weatherclock.adg.platformSpecific.appStorage
import ru.weatherclock.adg.platformSpecific.defaultHttpClientEngine

fun MainViewController(): UIViewController = ComposeUIViewController {
    CompositionLocalProvider(
        LocalImageLoader provides remember { generateImageLoader() },
    ) {
        appStorage = NSHomeDirectory()
        val kamelConfig = KamelConfig {
            takeFrom(KamelConfig.Default)
            httpFetcher(defaultHttpClientEngine)
        }
        App(isDarkThemeSupported = true, kamelConfig = kamelConfig)
    }
}

/**
 * Cache for image loader
 */
private fun generateImageLoader(): ImageLoader {
    return ImageLoader {
        commonConfig()
        components { setupDefaultComponents() }
        interceptor {
            memoryCacheConfig {
                // Set the max size to 25% of the app's available memory.
                maxSizePercent(0.25)
            }
            diskCacheConfig {
                directory(getCacheDir().toPath().resolve("image_cache"))
                maxSizeBytes(512L * 1024 * 1024) // 512MB
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun getCacheDir(): String {
    return NSFileManager.defaultManager
        .URLForDirectory(
            NSCachesDirectory,
            NSUserDomainMask,
            null,
            true,
            null,
        )!!
        .path
        .orEmpty()
}