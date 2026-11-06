import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import java.util.Properties

private fun String.capitalized(): String = replaceFirstChar { it.uppercaseChar() }

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

private val appPackageName = libs.versions.app.packageName.get()
private val appVersionName = libs.versions.app.version.name.get()
private val appVersionCode = libs.versions.app.version.code.get().toInt()

// Подпись читается из local.properties (не коммитится, см. local.properties.example)
val localProperties = Properties().apply {
    val localPropsFile = rootProject.file("local.properties")
    if (localPropsFile.exists()) {
        localPropsFile.inputStream().use { stream -> load(stream) }
    }
}

android {
    namespace = appPackageName
    compileSdk = 37

    defaultConfig {
        applicationId = appPackageName
        // minSdk 24 (Android 7): минимум для firebase-analytics 24 и coil 3.6+
        minSdk = 24
        // compileSdk поднят для coil 3.6+, targetSdk не менялся
        targetSdk = 36
        versionCode = appVersionCode
        versionName = appVersionName

        multiDexEnabled = true
    }

    // Подпись release-сборки. Параметры берутся из local.properties:
    // signing.storeFile / signing.storePassword / signing.keyAlias / signing.keyPassword.
    // Пока все поля не заполнены — release подписывается debug-ключом (только для локальной проверки).
    val releaseSigningStoreFile = localProperties.getProperty("signing.storeFile").orEmpty().trim()
    val releaseSigningStorePassword = localProperties.getProperty("signing.storePassword").orEmpty()
    val releaseSigningKeyAlias = localProperties.getProperty("signing.keyAlias").orEmpty()
    val releaseSigningKeyPassword = localProperties.getProperty("signing.keyPassword").orEmpty()
    val hasReleaseSigning = releaseSigningStoreFile.isNotEmpty() &&
            releaseSigningStorePassword.isNotEmpty() &&
            releaseSigningKeyAlias.isNotEmpty() &&
            releaseSigningKeyPassword.isNotEmpty()

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = rootProject.file(releaseSigningStoreFile)
                storePassword = releaseSigningStorePassword
                keyAlias = releaseSigningKeyAlias
                keyPassword = releaseSigningKeyPassword
            }
        }
    }

    buildTypes {
        getByName("debug") {
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = if (hasReleaseSigning) signingConfigs.getByName("release")
            else signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        //Нужно для работы kotlinx.datetime на версии Android ниже 7
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }

    packaging {
        resources {
            excludes += "META-INF/versions/**"
            excludes += "META-INF/*"
        }
    }
}

// Имя выходного APK (новый Variant API вместо устаревшего applicationVariants)
androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            output.outputFileName.set(
                "HomeWeatherClock${variant.name.capitalized()}_${appVersionName}(${appVersionCode}).apk"
            )
        }
    }
}

dependencies {
    implementation(project(":composeApp"))

    // Тема манифеста (Theme.AppCompat.DayNight) берётся из appcompat
    implementation(libs.androidx.appcompat)

    //Нужно для работы kotlinx.datetime на версии Android ниже 7
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
}
