# Погодные иконки (bundled SVG)

232 SVG-иконки, скачиваются скриптом `scripts/download_weather_icons.py`
(батч-API [Iconify](https://api.iconify.design), алиасы разрешаются автоматически).
Имена файлов: `<set>/{owm-<код>.svg, accu-<код>.svg}` — коды OpenWeatherMap
(`10d`) и AccuWeather (`12`). Маппинги в скрипте и в Kotlin
(`domain/model/forecast/WeatherIcons.kt`) должны оставаться синхронными:
4 набора × (18 + 40) = 232 файла.

## Лицензии и атрибуция

| Набор | Каталог | Лицензия | Источник |
|---|---|---|---|
| Meteocons (fill + line) | `meteocons-fill/`, `meteocons/` | MIT | basmilius/meteocons через Iconify |
| Twemoji | `twemoji/` | CC-BY 4.0 | Twitter/twemoji через Iconify |
| Weather Icons | `wi/` | SIL OFL 1.1 | erikflowers/weather-icons через Iconify |

Twemoji распространяется по CC-BY 4.0 — атрибуция указана в `policy.html`
(раздел Credits). Метаданные иконок (viewBox и т.п.) берутся из ответа
Iconify; SVG собираются скриптом как `<svg viewBox="...">{body}</svg>`.
