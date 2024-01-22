import java.time.LocalDateTime
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
//    alias(libs.plugins.libres)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.mokoResources)
//    kotlin("native.cocoapods")
}

apply(from = "${rootProject.projectDir}/composeApp/constants.gradle.kts")

private val appName = extra["appName"].toString()
private val appPackageName = extra["appPackageName"].toString()
private val appVersionName = extra["appVersionName"].toString()
private val appVersionCode = extra["appVersionCode"] as Int

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
//                freeCompilerArgs += "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi::class"
            }
        }
    }

    @Suppress("OPT_IN_USAGE")
    //https://youtrack.jetbrains.com/issue/KT-61573
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    jvm()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
//            export(libs.moko.resources)
            // Make AppleSettings visible from Swift
//            export(libs.multiplatform.settings)
//            export(libs.moko.graphics)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.material3)
                api(compose.material)
                implementation(compose.materialIconsExtended)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
//                api(libs.libres)
                implementation(libs.kermit)
                implementation(libs.bundles.voyager.common)
                implementation(libs.composeImageLoader)
                implementation(libs.napier)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.bundles.ktor.common)
                implementation(libs.composeIcons.featherIcons)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.koin.core)
                implementation(libs.koin.compose.mp)
                implementation(libs.sqlDelight.coroutines.extensions)
                api(libs.moko.resources.compose)
                api(libs.moko.resources)
                implementation(libs.atomicfu)
                implementation(libs.kstore)
                implementation(libs.stately.common)
                implementation(libs.kamelImageLoader)
                implementation(libs.androidx.annotation)
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.activityCompose)
                implementation(libs.compose.uitooling)
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.ktor.android)
                implementation(libs.koin.compose)
                implementation(libs.sqlDelight.driver.android)
                implementation(libs.androidx.media3.exoplayer)
                implementation(libs.kstore.file)
//                implementation (libs.desugar.jdk.libs)
//                coreLibraryDesugaring (libs.desugar.jdk.libs)
//                implementation(libs.kotlinx.datetime)
//                implementation(libs.androidx.preferences)
            }
        }

        val jvmMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)
                implementation(libs.ktor.client.apache)
                implementation(libs.ktor.java)
                implementation(libs.koin.compose)
                implementation(libs.sqlDelight.driver.sqlite)
                implementation(libs.vlcj)
                implementation(libs.jlayer)
                implementation(libs.kstore.file)
                implementation(libs.java.appdirs)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
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
}

multiplatformResources {
    disableStaticFrameworkWarning = true
    multiplatformResourcesPackage = appPackageName // required
//    multiplatformResourcesClassName = "Res" // optional, default MR
//    multiplatformResourcesVisibility = MRVisibility.Internal // optional, default Public
//    iosBaseLocalizationRegion = "ru" // optional, default "en"
//    multiplatformResourcesSourceSet = "commonClientMain"  // optional, default "commonMain"
}

android {
    namespace = appPackageName
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 34

        applicationId = appPackageName
        versionCode = appVersionCode
        versionName = appVersionName

        multiDexEnabled = true
    }
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/resources")
        res.srcDirs(
            "src/androidMain/res",
            "src/commonMain/resources"
        )
        resources.exclude("src/commonMain/resources/MR")
    }
    compileOptions {
        //Нужно для работы kotlinx.datetime на версии Android ниже 7
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    packaging {
        resources {
            excludes += "META-INF/versions/**"
            excludes += "META-INF/*"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            multiDexEnabled = true
        }
        getByName("debug") {
            multiDexEnabled = true
        }
    }

    dependencies {
        //Нужно для работы kotlinx.datetime на версии Android ниже 7
        coreLibraryDesugaring(libs.desugar.jdk.libs)
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
                TargetFormat.Deb
            )
            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))

            packageName = appName
            packageVersion = appVersionName

            version = appVersionCode

//            packageName = "HomeWeatherClock"
//            packageVersion = "1.0.0"
            modules += listOf(
                "java.compiler",
                "java.instrument",
                "jdk.unsupported",
                "java.naming",
                "java.sql"
            )
            copyright = "© ${LocalDateTime.now()} Rasul Ismailov. All rights reserved."

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
        "String",
        "APP_NAME",
        "\"${appName}\""
    )
    buildConfigField(
        "String",
        "APP_PACKAGE_NAME",
        "\"${appPackageName}\""
    )
    buildConfigField(
        "String",
        "APP_VERSION",
        provider { "\"${appVersionName}\"" })
    buildConfigField(
        "String",
        "APP_AUTHOR",
        "\"beeline09\""
    )
    buildConfigField(
        "long",
        "BUILD_TIME",
        "${System.currentTimeMillis()}L"
    )
}

sqldelight {
    databases {
        create("Database") {
            // Database configuration here.
            // https://cashapp.github.io/sqldelight
            packageName.set("ru.weatherclock.adg.db")
        }
    }
}
