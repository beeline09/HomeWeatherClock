# Стековый след для Crashlytics
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,InnerClasses,EnclosingMethod,Signature,Exceptions

# --- kotlinx-serialization (настройки/DTO: рефлексивный lookup $serializer) ---
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class ru.weatherclock.adg.**$$serializer { *; }
-keepclassmembers class ru.weatherclock.adg.** { static *** Companion; }
-keepclasseswithmembers class ru.weatherclock.adg.** { kotlinx.serialization.KSerializer serializer(...); }

# --- xmlutil (парсинг calendar.xml поверх kotlinx-serialization) ---
-keep,includedescriptorclasses class io.github.pdvrieze.xmlutil.**$$serializer { *; }
-keepclasseswithmembers class io.github.pdvrieze.xmlutil.** { kotlinx.serialization.KSerializer serializer(...); }
-dontnote io.github.pdvrieze.xmlutil.**

# --- ktor/okhttp/coil/sqlidelight поставляют consumer-правила в jar; ---
# --- отдельные правила не требуются ---

# Java 8+ desugaring (datetime на minSdk 21)
-dontwarn java.lang.invoke.StringConcatFactory
