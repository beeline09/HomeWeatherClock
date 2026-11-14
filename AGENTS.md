# AGENTS.md — HomeWeatherClock (Compose Multiplatform)

Инструкция для AI-агентов и разработчиков. Проект: настенные часы с погодой
и производственным календарём. KMP: Android + Desktop (jvm) + iOS, всё в
одном модуле `:composeApp` (com.android.application + androidTarget()).

## Стек и структура (ОСНОВНОЙ — поддержка Android 5.1)

- **Стек зафиксирован ради minSdk 21** (планшет на Android 5.1):
  Gradle 8.14, Kotlin 2.2.21, AGP 8.13.0, Compose 1.9.2, coil 3.3.0,
  koin 4.1.1, ktor 3.3.1, firebase-bom 33.1.0, SQLDelight 2.2.1
  (последняя с minSdk 21; 2.4.1 требует 23), навигация — JB-порт
  androidx navigation-compose 2.9.2 (последняя линия для Compose 1.9.x;
  2.10 требует CMP 1.10; Nav3 — только minSdk 23+), lifecycle-viewmodel
  2.9.0
- Почему зафиксирован: compose ≥1.10 требует minSdk 23, а его же
  плагин ≤1.9 несовместим с AGP 9 (пустое окно совместимости);
  coil ≥3.6 и appcompat ≥1.8 / activity ≥1.12 / koin ≥4.2 — тоже minSdk 23.
  Откат тулчейна — осознанное решение (2026-11-07), НЕ «обновлять обратно»
  без пересмотра поддержки 5.1
- Voyager удалён (2026-11-14): навигация — NavHost/composable, VM —
  androidx ViewModel (KMP) в скоупе NavBackStackEntry; koin-viewmodel-
  модули недоступны (koin 4.2+ = minSdk 23) — VM как factory + koin.get()
- Release собирается с R8 (minify+shrink, ~4.7 МБ): правила для
  kotlinx-serialization/xmlutil в proguard-rules.pro; любые изменения
  DI-графа проверяются jvmTest KoinGraphTest (резолвит зависимости
  реально), а не живым крашем на устройстве
- Таргеты: jvm, iosX64, iosArm64, iosSimulatorArm64
- minSdk 21, compileSdk 36, targetSdk 36
- Константы приложения (имя/пакет/версия) и все версии — в
  `gradle/libs.versions.toml` (`app-*` в `[versions]`)
- Исторически: 2026-11-07 была миграция на AGP 9 + `:androidApp` —
  свёрнута обратно ради minSdk 21 (git-история хранит обе структуры)

## Сборка

- **JDK 21 обязателен**:
  `export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home`.
  JDK 25 ломает Kotlin daemon (IllegalArgumentException на версии).
  Запуск из Xcode экспорта терминала НЕ наследует — лечится
  `~/.gradle/gradle.properties` (машинно-локально, не в репо):
  `org.gradle.java.home=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home`.
  Симптом без него: iOS-сборка падает в фазе Run Script с
  «What went wrong: 25.0.1»
- APK: `composeApp/build/outputs/apk/{debug,release}/HomeWeatherClock*_1.1.0(N).apk`
- Аудит содержимого APK — только через python `zipfile`
  (macOS `unzip -p | strings` на dex даёт мусор/пустоту — ВСЕ ранние
  «нулевые» аудиты через unzip были недостоверны)
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
- История обоих репозиториев **уже вычищена** (filter-repo, force-push
  2026-11-07): секреты заменены на REMOVED-*, keystore-копия удалена.
  НОВЫХ секретов в коммиты не добавлять
- **Ключ погоды НЕ генерируется в BuildConfig** (под плагином 5.7.1
  корневые поля попадают в AGP main → утечка в APK). Дефолт приходит
  рантаймом: `PlatformHelper.defaultWeatherApiKey` (jvm читает
  local.properties при запуске; android/ios — «», ввод в настройках)

## Производственный календарь (xmlcalendar.ru)

- **TLS на кастомных ROM — ИСТОРИЯ**: CA-стор на планшете обновлён
  13.11.2026, бандл корней из приложения удалён (движок снова Android/
  системный траст). Если на каком-то устройстве вернётся «Trust anchor
  not found» — рецепт в git-истории: коммит 0e6394a (OkHttp-движок +
  X509TrustManager «система → бандленные res/raw корни», NSC-якоря как
  второй слой); корни сверять по SHA-256 отпечатку с ≥2 независимых
  источников (chain сервера, SystemRootCertificates, сайт CA). Платформа
  ROM может игнорировать NSC — надёжен только траст-менеджер в коде
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
