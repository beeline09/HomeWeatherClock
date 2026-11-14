import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.time.LocalDateTime
import java.util.Properties

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.application)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
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

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_19)
        }
    }

    jvm()

    listOf(
        iosX64(), iosArm64(), iosSimulatorArm64()
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
            // kotlin.time.Instant/epochSeconds (kotlinx-datetime 0.7 делегирует
            // в stdlib, где API ещё experimental в Kotlin 2.2)
            languageSettings.optIn("kotlin.time.ExperimentalTime")
        }

        val commonMain = getByName("commonMain") {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.material3)
                api(compose.material)
                implementation(compose.materialIconsExtended)
                implementation(compose.components.resources)
                implementation(libs.kermit)
                // Навигация: JB-порт androidx navigation-compose (type-safe
                // версия 2.9.x — последняя для Compose 1.9.x)
                implementation(libs.jb.navigation.compose)
                implementation(libs.jb.lifecycle.viewmodel.compose)
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
                implementation(libs.bundles.coil)
            }
        }

        // Пустой source set: AGP 9 задача Compose Preview (IDE-синк)
        // требует unit-test компонент у КАЖДОГО варианта, включая release;
        // без androidUnitTest KGP его не создаёт
        val androidUnitTest = getByName("androidUnitTest")

        val androidMain = getByName("androidMain") {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.activityCompose)
                implementation(libs.compose.uitooling)
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.ktor.android)
//                implementation(libs.koin.compose)
//                implementation(libs.koin.android)
                implementation(libs.sqlDelight.driver.android)
                implementation(libs.kstore.file)
            }
        }

        val jvmMain = getByName("jvmMain") {
            dependsOn(commonMain)
            dependencies {
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
        }

        // Растеризационные тесты иконок/парсера календаря (SvgRasterTest и др.)
        val jvmTest = getByName("jvmTest") {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val iosX64Main = getByName("iosX64Main")
        val iosArm64Main = getByName("iosArm64Main")
        val iosSimulatorArm64Main = getByName("iosSimulatorArm64Main")
        val iosMain = create("iosMain") {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(libs.ktor.client.darwin)
                implementation(libs.sqlDelight.driver.native)
                implementation(libs.kstore.file)
            }
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

// Новый DSL-интерфейс (accessor android{} типизирован устаревшим
// BaseAppModuleExtension — работает только в legacy-режиме newDsl=false)
extensions.configure<com.android.build.api.dsl.ApplicationExtension> {
    namespace = appPackageName
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        targetSdk = 36

        applicationId = appPackageName
        versionCode = appVersionCode
        versionName = appVersionName
        // multiDexEnabled убран: native multidex для minSdk 21+ включён
        // всегда (legacy multidex_DEPRECATED в AGP 9.4)
    }
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        // setSrcDirs: на legacy-DSL (android.newDsl=false, нужен для KGP
        // 2.2 × AGP 9) другого API нет — «directories set» только в new DSL
        res.setSrcDirs(
            listOf(
                "src/androidMain/resources",
                "src/androidMain/res",
                "src/commonMain/resources"
            )
        )
    }

    buildFeatures {
        compose = true
    }

    // AGP 9: задача Compose Preview (generateComposePreviewRunfiles) для
    // IDE-синка требует android-ресурсы в unit tests — без флага sync
    // в Android Studio падает на warm-up task model
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    compileOptions {
        //Нужно для работы kotlinx.datetime на версии Android ниже 7
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }

    // Подпись release-сборки. Параметры берутся из local.properties (см. local.properties.example):
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
            isMinifyEnabled = false
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }
        }
        getByName("release") {
            // R8: минификация + шринк ресурсов. Старые устройства (планшет
            // Android 5.1): меньше дексов = быстрее старт и меньше RAM.
            // Правила для рефлексии (сериализация, Firebase) — в
            // proguard-rules.pro; mapping Crashlytics заберёт сам
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                // путь от корня модуля (AGP 9 строго проверяет существование:
                // раньше несуществующий composeApp/proguard-rules.pro
                // молча игнорировался)
                "src/androidMain/proguard-rules.pro"
            )
            signingConfig = if (hasReleaseSigning) signingConfigs.getByName("release")
            else signingConfigs.getByName("debug")
        }
    }

    packaging {
        resources {
            excludes += "META-INF/versions/**"
            excludes += "META-INF/*"
        }
    }

    // Именование APK: современный API (applicationVariants устарел в AGP 9)
    androidComponents {
        // AGP 9: generateComposePreviewRunfiles (Compose Preview, IDE-синк)
        // создаётся для КАЖДОГО варианта и требует unit-test компонент —
        // KGP гасит его (enableUnitTest=false), возвращаем. Каст к
        // интерфейсу — generic-типизация KMP-accessor'а не даёт свойств
        beforeVariants { builder ->
            (builder as? com.android.build.api.variant.HasUnitTestBuilder)
                ?.enableUnitTest = true
        }
        onVariants { variant ->
            variant.outputs.forEach { output ->
                output.outputFileName.set(
                    "HomeWeatherClock${variant.name.replaceFirstChar { it.uppercase() }}" +
                        "_${appVersionName}(${appVersionCode}).apk"
                )
            }
        }
    }

    dependencies {
        //Нужно для работы kotlinx.datetime на версии Android ниже 7
        coreLibraryDesugaring(libs.desugar.jdk.libs)
        implementation(platform(libs.firebase.bom))
        implementation(libs.firebase.analytics)
        implementation(libs.firebase.crashlytics)
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

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.annotation.jvm)
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
    // Ключ погодного API НЕ генерируется здесь: под плагином 5.7.1 корневые
    // поля попадают в AGP main и утекали в Android-APK. Дефолт ключа теперь
    // приходит рантаймом через PlatformHelper.defaultWeatherApiKey
    // (jvm читает local.properties, android/ios — пусто, ввод в настройках).
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
