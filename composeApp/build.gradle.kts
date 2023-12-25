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

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
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
            export(libs.moko.resources)
//            export(libs.moko.graphics)
        }
    }

//    cocoapods {
//        version = "1.0.0"
//        summary = "Some description for the Shared Module"
//        homepage = "Link to the Shared Module homepage"
////        ios.deploymentTarget = "14.0"
//        podfile = project.file("../iosApp/Podfile")
//        framework {
//            baseName = "ComposeApp"
//            isStatic = true
//        }
//        extraSpecAttributes["resources"] =
//            "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
//        extraSpecAttributes["exclude_files"] = "['src/commonMain/resources/MR/**']"
//    }

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
//            implementation(libs.composeCalendar)
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
            }
        }

    }
}

multiplatformResources {
    disableStaticFrameworkWarning = true
    multiplatformResourcesPackage = "ru.homeweatherclock.adg" // required
//    multiplatformResourcesClassName = "Res" // optional, default MR
//    multiplatformResourcesVisibility = MRVisibility.Internal // optional, default Public
//    iosBaseLocalizationRegion = "ru" // optional, default "en"
//    multiplatformResourcesSourceSet = "commonClientMain"  // optional, default "commonMain"
}

android {
    namespace = "ru.weatherclock.adg"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 34

        applicationId = "ru.weatherclock.adg.androidApp"
        versionCode = 1
        versionName = "1.0.0"
    }
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/resources")
        res.srcDirs("src/androidMain/res", "src/commonMain/resources")
        resources.exclude("src/commonMain/resources/MR")
    }
    compileOptions {
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
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        jvmArgs += "-XX:+PrintCompilation"
        jvmArgs += "-XX:CompileThreshold=1"

        nativeDistributions {
            val iconsRoot = project.file("src/commonMain/resources/drawables")
            targetFormats(
                TargetFormat.Dmg,
                TargetFormat.Msi,
                TargetFormat.Exe,
                TargetFormat.Deb
            )
            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))
            packageName = "HomeWeatherClock"
            packageVersion = "1.0.0"
            modules += listOf(
                "java.compiler",
                "java.instrument",
                "jdk.unsupported",
                "java.naming",
                "java.sql"
            )

            linux {
                iconFile.set(iconsRoot.resolve("launcher_icons/linux.png"))
            }

            windows {
                iconFile.set(iconsRoot.resolve("launcher_icons/windows.ico"))
                shortcut = true
                menu = true
            }

            macOS {
                packageName = "HomeWeatherClock"
                bundleID = "ru.homeweatherclock.adg"
                iconFile.set(iconsRoot.resolve("launcher_icons/macos.icns"))
//                runtimeEntitlementsFile.set(project.file("runtime-entitlements.plist"))
            }
        }
    }
}

/*libres {
    // https://github.com/Skeptick/libres#setup
//    generatedClassName = "MainRes" // "Res" by default
    generateNamedArguments = true // false by default
    baseLocaleLanguageCode = "ru" // "en" by default
    camelCaseNamesForAppleFramework = false // false by default
}*/
//tasks.getByPath("jvmProcessResources").dependsOn("libresGenerateResources")
//tasks.getByPath("jvmSourcesJar").dependsOn("libresGenerateResources")
dependencies {
    implementation(libs.androidx.core.ktx)
}

buildConfig {
    // BuildConfig configuration here.
    // https://github.com/gmazzo/gradle-buildconfig-plugin#usage-in-kts
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

/*// workaround https://github.com/icerockdev/moko-resources/issues/421
tasks.matching { it.name == "desktopProcessResources" }.configureEach {
    dependsOn(tasks.matching { it.name == "generateMRdesktopMain" })
}
tasks.matching { it.name == "iosSimulatorArm64ProcessResources" }.configureEach {
    dependsOn(tasks.matching { it.name == "generateMRiosSimulatorArm64Main" })
}
tasks.matching { it.name == "metadataIosMainProcessResources" }.configureEach {
    dependsOn(tasks.matching { it.name == "generateMRcommonMain" })
}
tasks.matching { it.name == "metadataCommonMainProcessResources" }.configureEach {
    dependsOn(tasks.matching { it.name == "generateMRcommonMain" })
}
tasks.matching { it.name == "iosX64ProcessResources" }.configureEach {
    dependsOn(tasks.matching { it.name == "generateMRiosX64Main" })
}
tasks.matching { it.name == "iosArm64ProcessResources" }.configureEach {
    dependsOn(tasks.matching { it.name == "generateMRiosArm64Main" })
}*/
