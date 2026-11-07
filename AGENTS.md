# AGENTS.md — HomeWeatherClock (Compose Multiplatform)

Инструкция для AI-агентов и разработчиков. Проект: настенные часы с погодой
и производственным календарём. KMP: Android (`:androidApp`) + Desktop (jvm)
+ iOS, общий код в `:composeApp`.

## Стек и структура

- Gradle 9.9, Kotlin 2.4.21, AGP 9.5.1, Compose Multiplatform 1.12.2,
  koin 4.2 (bom), ktor 3.6.1, SQLDelight 2.4.1, coil 3.6.3, firebase-bom 35
- **Структура по AGP 9**: `:androidApp` (com.android.application) — манифест,
  res, подпись, firebase-плагины, APK-нейминг; `:composeApp` — KMP-библиотека
  через `com.android.kotlin.multiplatform.library` (блок `kotlin.android{}`)
- Android-КОТЛИН-классы (`AndroidApp`, `AppActivity`, `BootUpReceiver`,
  все actual'ы PlatformHelper) живут в `composeApp/src/androidMain` —
  манифест androidApp ссылается на них по имени пакета
- Таргеты: jvm, iosArm64, iosSimulatorArm64. **iosX64 удалён** — экосистема
  (Compose/coil/androidx) перестала публиковать варианты под Intel-симулятор
- minSdk 24 (coil 3.6 + firebase-analytics 24), compileSdk 37,
  targetSdk 36 (осознанно не поднимали)
- Константы приложения (имя/пакет/версия) и все версии — в
  `gradle/libs.versions.toml` (`app-*` в `[versions]`)

## Сборка

- **JDK 21 обязателен**:
  `export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home`.
  JDK 25 ломает Kotlin daemon (IllegalArgumentException на версии)
- APK: `androidApp/build/outputs/apk/{debug,release}/HomeWeatherClock*_1.1.0(N).apk`
  (нейминг — через `androidComponents.onVariants`, НЕ applicationVariants)
- macOS `unzip -p | strings` не читает dex у AGP 9 APK — аудит содержимого
  APK делать через python `zipfile`
- Если «Unresolved reference» на только что добавленную строку ресурса —
  гонка инкрементальной генерации:
  `rm -rf composeApp/build/generated/compose/resourceGenerator`

## Секреты и подпись (RuStore)

- Все секреты — в `local.properties` (не коммитится; шаблон —
  `local.properties.example`): `weatherApiKey` (попадает ТОЛЬКО в
  desktop/iOS через BuildConfig; androidMain переопределяет пустой строкой —
  в Android-сборках ключа нет, вводится в настройках приложения),
  `signing.storeFile/storePassword/keyAlias/keyPassword`
- Release без заполненного `signing.*` подписывается debug-ключом —
  годится только для локальной проверки, в стор не заливать
- **RuStore-версия подписана СТАРЫМ ключом** (SHA-256 9e261c6e…, alias
  `adiga` — он же утёкший в публичную историю), пока ТП RuStore не сменит
  подпись приложения; `prod.jks` (105c6745…, alias `clock`) — новый ключ,
  до ротации в сторе нельзя
- В истории обоих репозиториев есть утечки (ключи погоды, токен
  production-calendar, копия keystore) — запланирована чистка
  git-filter-repo + force-push; в копии коммитов не появляться

## Производственный календарь (xmlcalendar.ru)

- Источник — **calendar.xml**, НЕ calendar.json: только XML несёт легенду
  с названиями праздников (`<holiday id title>`, дни ссылаются `h="id"`)
- Даты в атрибутах `d`/`f` — формат **ММ.ДД** (месяц.день!):
  `d="01.09"` = 9 января, `f="01.03"` = перенесён с 3 января
- Ответ приходит с `Content-Type: text/xml` — ContentNegotiation ktor-xml
  матчится только с application/xml, поэтому тело берётся текстом
  (`bodyAsText`) и декодируется XML-форматом xmlutil напрямую
- Неизвестные атрибуты XML собираются `@XmlOtherAttributes`
  (Map<String,String>; c ключом QName IR-компилятор падает)
- Кеш БД инвалидируется константой `CALENDAR_DB_REGION` в CalendarUseCase —
  бампать при любом изменении семантики данных (note/typeText)
- `note` в данных — только СЫРЫЕ значения (дата переноса «ММ.ДД», название
  праздника); локализованные фразы собираются в UI
  (`localizedDescription`, шаблоны `prod_calendar_*` в strings.xml)
- Ошибки сети/разбора календаря — тихий фолбэк на кеш БД (без снекбаров)

## Иконки погоды

- 232 bundled SVG: `composeResources/files/weather_icons/<set>/{owm-*,accu-*}`,
  4 набора (meteocons-fill, meteocons, twemoji, wi)
- Регенерация — `scripts/download_weather_icons.py`. **Не брать meteocons
  из Iconify API**: их тела содержат `<symbol>`+`<use>`+`<animateTransform>`,
  которые SkSVG (skiko/coil, все платформы) парсит, но НЕ рендерит —
  «пустые квадраты». Брать production-файлы репо basmilius/meteocons
  (тег v2.0.0, raw.githubusercontent)
- Верификация рендера иконок — jvmTest `SvgRasterTest`: растеризует все
  наборы в `build/icon-raster/*.png` с ассертом непустых пикселей.
  **UI верифицировать по пикселям PNG, не по отсутствию ошибок в логах**
  (painter Success ≠ видимые пиксели)

## Настройки и миграции

- Формат настроек (`AppSettings`) меняется ТОЛЬКО добавлением полей
  с дефолтами (kotlinx ignoreUnknownKeys); существующие @SerialName не
  переименовывать
- iOS `storeOf` должен иметь тот же явный Json-конфиг
  (ignoreUnknownKeys/encodeDefaults/prettyPrint), что android/jvm
- kstore 0.7.1 зафиксирован (0.9.1 крашится на Android 7 при сохранении);
  на desktop (`enableCache=false`) поток `updates` ненадёжен без начального
  значения — в `allConfigFlow` используется `onStart { emit(get()) }`
- Кеш прогноза: сеть дёргается только при пустой БД / смене сервера /
  изменении WeatherConfig / по часовому тику — **повторные запуски desktop
  без изменения настроек бесплатны** (квоты API)

## Стиль работы

- Файлы проекта править инструментами редактирования (Edit/Write),
  НЕ python-скриптами (replace молча не срабатывает на несовпадении якоря)
- Логика UI верифицируется тестами (jvmTest) по результату — рендер в PNG,
  парсинг на фикстурах; фикстуры должны использовать реальные имена enum
  из сериализации

## Скиллы агентов

`.claude/skills/` — установлены: `android-*` (36, Google android/skills:
agp-9-upgrade, r8-analyzer, edge-to-edge, compose-performance…),
`kotlin-tooling-*` (10, JetBrains: agp9-migration, kotlin-toolchain…),
`kmp-compose-multiplatform` (felipechaux), `compose-multiplatform-patterns`
(everything-claude-code). При задачах по теме — читать соответствующий
SKILL.md. Скиллы навязывают чужие стеки (Room/DataStore/BuildKonfig) —
конвенции этого проекта (SQLDelight/kstore/структура) приоритетны.
