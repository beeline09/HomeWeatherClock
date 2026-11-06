import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.time.LocalDateTime
import java.util.Properties

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.multiplatform.library)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.sqlDelight)
}

private val appName = libs.versions.app.name.get()
private val appPackageName = libs.versions.app.packageName.get()
private val appVersionName = libs.versions.app.version.name.get()
private val appVersionCode = libs.versions.app.version.code.get().toInt()

// Секреты читаются из local.properties (не коммитится, см. local.properties.example)
val localProperties = Properties().apply {
    val localPropsFile = rootProject.file("local.properties")
    if (localPropsFile.exists()) {
        localPropsFile.inputStream().use { stream -> load(stream) }
    }
}
val weatherApiKey: String = localProperties.getProperty("weatherApiKey").orEmpty()

kotlin {

    // Android-таргет — KMP-библиотека (AGP 9): приложение в отдельном модуле :androidApp
    android {
        namespace = "${appPackageName}.library"
        compileSdk = 37
        // minSdk 24 (Android 7): минимум для firebase-analytics 24 и coil 3.6+
        minSdk = 24

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_19)
        }

        // Compose-ресурсы commonMain упаковываются в AAR
        androidResources {
            enable = true
        }
    }

    jvm()

    listOf(
        // iosX64 убран: экосистема (Compose 1.12+, coil 3.6+, androidx) больше
        // не публикует варианты под Intel-симулятор iOS
        iosArm64(), iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {

        all {
            languageSettings.optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
        }

        val commonMain = getByName("commonMain")
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.material3)
            api(compose.material)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(libs.kermit)
            implementation(libs.bundles.voyager.common)
            implementation(libs.composeImageLoader)
            implementation(libs.napier)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.bundles.ktor.common)
            implementation(libs.composeIcons.featherIcons)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose.mp)
            implementation(libs.sqlDelight.coroutines.extensions)
            implementation(libs.atomicfu)
            implementation(libs.kstore)
            implementation(libs.kstore.file)
            implementation(libs.stately.common)
            implementation(libs.androidx.annotation)
//                implementation(libs.androidx.lifecycle.runtime)
            implementation(kotlin("stdlib-common"))
            implementation(kotlin("reflect"))
            implementation(libs.bundles.coil)
        }

        val androidMain = getByName("androidMain")
        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.activityCompose)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.annotation.jvm)
            implementation(libs.compose.uitooling)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.android)
//                implementation(libs.koin.compose)
//                implementation(libs.koin.android)
            implementation(libs.sqlDelight.driver.android)
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.kstore.file)
        }

        val jvmMain = getByName("jvmMain")
        jvmMain.dependencies {
            implementation(compose.desktop.common)
            implementation(compose.desktop.currentOs)
            implementation(libs.ktor.client.apache)
            implementation(libs.ktor.java)
//                implementation(libs.koin.compose)
            implementation(libs.sqlDelight.driver.sqlite)
            implementation(libs.vlcj)
            implementation(libs.jlayer)
            implementation(libs.kstore.file)
            implementation(libs.java.appdirs)
            implementation(libs.jSystemThemeDetector)
//                implementation("media.kamel:kamel-fetcher-resources-jvm:1.0.3")
        }

        val iosArm64Main = getByName("iosArm64Main")
        val iosSimulatorArm64Main = getByName("iosSimulatorArm64Main")
        val iosMain = create("iosMain") {
            dependsOn(commonMain)
        }
        iosArm64Main.dependsOn(iosMain)
        iosSimulatorArm64Main.dependsOn(iosMain)
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqlDelight.driver.native)
            implementation(libs.kstore.file)
        }
    }

    compilerOptions {
        freeCompilerArgs.addAll(
            listOf(
                "-Xallow-kotlin-package",
                "-Xexpect-actual-classes",
                "-opt-in=kotlin.ExperimentalMultiplatform",
                "-opt-in=kotlin.contracts.ExperimentalContracts",
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlin.ExperimentalUnsignedTypes",
                "-opt-in=kotlin.ExperimentalStdlibApi",
                "-opt-in=kotlinx.cinterop.BetaInteropApi",
                "-opt-in=kotlinx.cinterop.ExperimentalForeignApi",
            )
        )
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
//        jvmArgs += "-XX:+PrintCompilation"
        jvmArgs += "-XX:CompileThreshold=1"
        jvmArgs += "-Dhttps.protocols=TLSv1.1,TLSv1.2"
        jvmArgs += "-Xdock:name=$appName"

        nativeDistributions {
            val iconsRoot = project.file("src/commonMain/resources/drawables")
            targetFormats(
                TargetFormat.Dmg,
                TargetFormat.Msi,
                TargetFormat.Exe,
                TargetFormat.Deb,
                TargetFormat.Rpm,
            )
            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))

            packageName = appPackageName
            packageVersion = appVersionName

            version = appVersionCode

//            packageName = "HomeWeatherClock"
//            packageVersion = "1.0.0"
            modules += listOf(
                "java.compiler", "java.instrument", "jdk.unsupported", "java.naming", "java.sql"
            )
            copyright = "© ${LocalDateTime.now().year} Rasul Ismailov. All rights reserved."

            linux {
                iconFile.set(iconsRoot.resolve("launcher_icons/linux.png"))
            }

            windows {
                iconFile.set(iconsRoot.resolve("launcher_icons/windows.ico"))
                shortcut = true
                menu = true
            }

            macOS {
                dockName = appName
                packageName = appName
                bundleID = appPackageName
                iconFile.set(iconsRoot.resolve("launcher_icons/macos.icns"))
//                runtimeEntitlementsFile.set(project.file("runtime-entitlements.plist"))
            }
        }
    }
}

buildConfig {
    buildConfigField(
        "String", "APP_NAME", "\"${appName}\""
    )
    buildConfigField(
        "String", "APP_PACKAGE_NAME", "\"${appPackageName}\""
    )
    buildConfigField("String", "APP_VERSION", provider { "\"${appVersionName}\"" })
    buildConfigField(
        "String", "APP_AUTHOR", "\"beeline09\""
    )
    buildConfigField(
        "long", "BUILD_TIME", "${System.currentTimeMillis()}L"
    )
    // Ключ погодного API из local.properties. Прокидывается во все source set'ы,
    // но androidMain ниже переопределяет его пустой строкой: Android-сборки
    // (в т.ч. публичные release) ключа не содержат — он вводится в настройках приложения.
    buildConfigField("String", "WEATHER_API_KEY", provider { "\"$weatherApiKey\"" })
    sourceSets.named("androidMain") {
        buildConfigField("String", "WEATHER_API_KEY", "\"\"")
    }
}

sqldelight {
    val packName = "${appPackageName}.db"
    databases {
        create("ProdCalendarDb") {
            packageName.set(packName)
            srcDirs("src/commonMain/sqldelight/prodCalendar")
        }
        create("AccuweatherDb") {
            packageName.set(packName)
            srcDirs("src/commonMain/sqldelight/accuweather")
        }
        create("OpenWeatherMapDb") {
            packageName.set(packName)
            srcDirs("src/commonMain/sqldelight/openweathermap")
        }
    }
}
