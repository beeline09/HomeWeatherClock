# Погодные иконки (bundled SVG)

232 SVG-иконки, скачиваются скриптом `scripts/download_weather_icons.py`:

- `meteocons-fill/` и `meteocons/` — production-файлы
  [Meteocons](https://github.com/basmilius/meteocons) v2.0.0
  (raw.githubusercontent, тег `fdd19749b9`). Для OpenWeatherMap —
  официальная папка `openweathermap/` с кодами `01d..50n`, для
  AccuWeather — `all/` по семантике условий.
- `twemoji/` и `wi/` — батч-API [Iconify](https://api.iconify.design)
  с разрешением алиасов.

Имена файлов: `<set>/{owm-<код>.svg, accu-<код>.svg}` — коды
OpenWeatherMap (`10d`) и AccuWeather (`12`). Маппинги в скрипте и в Kotlin
(`domain/model/forecast/WeatherIcons.kt`) должны оставаться синхронными:
4 набора × (18 + 40) = 232 файла.

## Почему нельзя брать meteocons из Iconify API

Тела иконок из Iconify содержат `<symbol>` + `<use>` и `<animateTransform>`.
SkSVG (декодер skiko/coil на всех платформах) парсит такие документы,
но НЕ отрисовывает — получаются «пустые квадраты». Production-файлы из
репозитория Meteocons standalone (без symbol/use) и рендерятся корректно
(проверено тестом SvgRasterTest: растеризация в build/icon-raster/*.png).

## Лицензии и атрибуция

| Набор | Каталог | Лицензия | Источник |
|---|---|---|---|
| Meteocons (fill + line) | `meteocons-fill/`, `meteocons/` | MIT | basmilius/meteocons v2.0.0 |
| Twemoji | `twemoji/` | CC-BY 4.0 | Twitter/twemoji через Iconify |
| Weather Icons | `wi/` | SIL OFL 1.1 | erikflowers/weather-icons через Iconify |

Twemoji распространяется по CC-BY 4.0 — атрибуция указана в `policy.html`
(раздел «Атрибуция сторонних ресурсов»).
