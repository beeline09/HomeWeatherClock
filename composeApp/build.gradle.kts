import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.time.LocalDateTime

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
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

kotlin {

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
//                freeCompilerArgs += "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi::class"
            }
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
        }

        val commonMain by getting {
            dependencies {
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
                implementation(libs.stately.common)
                implementation(libs.kamelImageLoader)
                implementation(libs.androidx.annotation)
//                implementation(libs.androidx.lifecycle.runtime)
                implementation(kotlin("stdlib-common"))
                implementation(kotlin("reflect"))
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
//                implementation(libs.ktor.android)
//                implementation(libs.koin.compose)
//                implementation(libs.koin.android)
                implementation(libs.sqlDelight.driver.android)
                implementation(libs.androidx.media3.exoplayer)
                implementation(libs.kstore.file)
            }
        }

        val jvmMain by getting {
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

tasks
    .withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>()
    .configureEach {
        compilerOptions {
            languageVersion.set(
                org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0
            )
            freeCompilerArgs.addAll(
                listOf(
                    "-Xallow-kotlin-package",
                    "-Xexpect-actual-classes",
                    "-opt-in=kotlin.ExperimentalMultiplatform",
                    "-opt-in=kotlin.contracts.ExperimentalContracts",
                    "-opt-in=kotlin.RequiresOptIn",
                    "-opt-in=kotlin.ExperimentalUnsignedTypes",
                    "-opt-in=kotlin.ExperimentalStdlibApi",
                )
            )
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
        }
    }

/*tasks.withType<Jar>().configureEach {
    manifest {
        attributes += sortedMapOf(
            "Built-By" to System.getProperty("user.name"),
            "Build-Jdk" to System.getProperty("java.version"),
            "Implementation-Vendor" to "JetBrains s.r.o.",
            "Implementation-Version" to archiveVersion.get(),
            "Created-By" to GradleVersion.current()
        )
    }
}*/

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
            "src/androidMain/res", "src/commonMain/resources"
        )
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
        kotlinCompilerExtensionVersion = "1.5.11"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = true
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
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    packaging {
        resources {
            excludes += "META-INF/versions/**"
            excludes += "META-INF/*"
        }
    }

    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName = "HomeWeatherClock${
                    variant.flavorName.capitalized()
                }${variant.buildType.name.capitalized()}_${appVersionName}(${appVersionCode}).apk"
                output.outputFileName = outputFileName
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
