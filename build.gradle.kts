plugins {
    alias(libs.plugins.multiplatform).apply(false)
    alias(libs.plugins.compose).apply(false)
    alias(libs.plugins.android.application).apply(false)
//    alias(libs.plugins.libres).apply(false)
    alias(libs.plugins.buildConfig).apply(false)
    alias(libs.plugins.kotlinx.serialization).apply(false)
    alias(libs.plugins.sqlDelight).apply(false)
    alias(libs.plugins.mokoResources).apply(false)
//    id("dev.icerock.mobile.multiplatform-resources").apply(false)
//    id("org.jetbrains.kotlin.android") version "1.9.21" apply false
}
