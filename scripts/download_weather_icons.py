#!/usr/bin/env python3
# Скачивание SVG-наборов погодных иконок с Iconify (api.iconify.design)
# в composeResources/files/weather_icons/<set>/{owm-<код>.svg, accu-<код>.svg}
#
# Маппинги здесь и в Kotlin (WeatherIcons.kt) должны оставаться синхронными:
# количество файлов = 4 набора × (18 OWM + 44 AccuWeather) = 248.
#
# Использует батч-API Iconify (один запрос на набор) — не ловит rate-limit.
# Использование: ./download_weather_icons.py [--dry]
import json
import sys
import urllib.request
from pathlib import Path

DEST = Path(__file__).resolve().parent.parent / \
    "composeApp/src/commonMain/composeResources/files/weather_icons"
BASE = "https://api.iconify.design"

# ---------- Коды OpenWeatherMap -> семантика ----------
OWM = {
    "01d": "CLEAR_DAY", "01n": "CLEAR_NIGHT",
    "02d": "PARTLY_CLOUDY_DAY", "02n": "PARTLY_CLOUDY_NIGHT",
    "03d": "CLOUDY", "03n": "CLOUDY",
    "04d": "OVERCAST", "04n": "OVERCAST",
    "09d": "SHOWERS", "09n": "SHOWERS",
    "10d": "RAIN_DAY", "10n": "RAIN_NIGHT",
    "11d": "THUNDER_DAY", "11n": "THUNDER_NIGHT",
    "13d": "SNOW", "13n": "SNOW",
    "50d": "FOG_DAY", "50n": "FOG_NIGHT",
}

# ---------- Коды AccuWeather -> семантика ----------
# Официальная таблица: developer.accuweather.com/documentation/weather-icons
# 9, 10, 27, 28 не используются.
ACCU = {
    1: "CLEAR_DAY", 2: "CLEAR_DAY", 3: "PARTLY_CLOUDY_DAY", 4: "CLOUDY",
    5: "HAZE_DAY", 6: "CLOUDY", 7: "CLOUDY", 8: "OVERCAST", 11: "FOG_DAY",
    12: "SHOWERS", 13: "OVERCAST_RAIN", 14: "RAIN_DAY", 15: "THUNDER_DAY",
    16: "THUNDER_DAY", 17: "THUNDER_DAY", 18: "RAIN_DAY", 19: "SNOW",
    20: "SNOW", 21: "SNOW", 22: "SNOW", 23: "OVERCAST_SNOW", 24: "SLEET",
    25: "SLEET", 26: "SLEET", 29: "SLEET", 30: "HOT", 31: "COLD", 32: "WIND",
    33: "CLEAR_NIGHT", 34: "CLEAR_NIGHT", 35: "PARTLY_CLOUDY_NIGHT",
    36: "CLOUDY", 37: "HAZE_NIGHT", 38: "CLOUDY", 39: "RAIN_NIGHT",
    40: "OVERCAST_RAIN", 41: "THUNDER_NIGHT", 42: "THUNDER_NIGHT",
    43: "SNOW", 44: "OVERCAST_SNOW",
}

# ---------- Семантика -> имя иконки в наборе ----------
MC = {  # базовое имя meteocons (fill добавит суффикс -fill)
    "CLEAR_DAY": "clear-day", "CLEAR_NIGHT": "clear-night",
    "PARTLY_CLOUDY_DAY": "partly-cloudy-day", "PARTLY_CLOUDY_NIGHT": "partly-cloudy-night",
    "CLOUDY": "cloudy", "OVERCAST": "overcast",
    "FOG_DAY": "fog-day", "FOG_NIGHT": "fog-night",
    "SHOWERS": "rain",
    "RAIN_DAY": "partly-cloudy-day-rain", "RAIN_NIGHT": "partly-cloudy-night-rain",
    "OVERCAST_RAIN": "overcast-rain",
    "THUNDER_DAY": "thunderstorms-day", "THUNDER_NIGHT": "thunderstorms-night",
    "SNOW": "snow", "OVERCAST_SNOW": "overcast-snow",
    "SLEET": "sleet",
    "HAZE_DAY": "partly-cloudy-day-haze", "HAZE_NIGHT": "partly-cloudy-night-haze",
    "WIND": "wind", "HOT": "clear-day", "COLD": "snowflake",
}

TW = {
    "CLEAR_DAY": "2600", "CLEAR_NIGHT": "1f319",
    "PARTLY_CLOUDY_DAY": "26c5", "PARTLY_CLOUDY_NIGHT": "1f319",
    "CLOUDY": "2601", "OVERCAST": "1f325",
    "FOG_DAY": "1f32b", "FOG_NIGHT": "1f32b",
    "SHOWERS": "1f327",
    "RAIN_DAY": "1f326", "RAIN_NIGHT": "1f327",
    "OVERCAST_RAIN": "1f327",
    "THUNDER_DAY": "26c8", "THUNDER_NIGHT": "26c8",
    "SNOW": "1f328", "OVERCAST_SNOW": "1f328",
    "SLEET": "1f328",
    "HAZE_DAY": "1f32b", "HAZE_NIGHT": "1f32b",
    "WIND": "1f4a8", "HOT": "1f975", "COLD": "1f976",
}

WI = {
    "CLEAR_DAY": "day-sunny", "CLEAR_NIGHT": "night-clear",
    "PARTLY_CLOUDY_DAY": "day-cloudy", "PARTLY_CLOUDY_NIGHT": "night-alt-cloudy",
    "CLOUDY": "cloudy", "OVERCAST": "cloudy",
    "FOG_DAY": "day-fog", "FOG_NIGHT": "fog",
    "SHOWERS": "showers",
    "RAIN_DAY": "day-rain", "RAIN_NIGHT": "night-alt-rain",
    "OVERCAST_RAIN": "rain",
    "THUNDER_DAY": "day-thunderstorm", "THUNDER_NIGHT": "night-alt-thunderstorm",
    "SNOW": "snow", "OVERCAST_SNOW": "snow",
    "SLEET": "sleet",
    "HAZE_DAY": "day-haze", "HAZE_NIGHT": "dust",
    "WIND": "strong-wind", "HOT": "hot", "COLD": "snowflake-cold",
}


def icons_for_set(set_name: str) -> dict:
    """semantic -> имя иконки в iconify-наборе."""
    if set_name == "meteocons-fill":
        return {k: v + "-fill" for k, v in MC.items()}
    if set_name == "meteocons":
        return dict(MC)
    if set_name == "twemoji":
        return dict(TW)
    if set_name == "wi":
        return dict(WI)
    raise ValueError(set_name)


def fetch_batch(prefix: str, names: list) -> tuple:
    """Батч-запрос. Возвращает ({имя: {body,width,height}}, дефолтные_размеры_набора),
    разрешая алиасы (например, twemoji '2600' -> 'sun')."""
    url = f"{BASE}/{prefix}.json?icons={','.join(names)}"
    req = urllib.request.Request(url, headers={
        "User-Agent": "HomeWeatherClock-assets/1.0 (build script)",
        "Accept": "application/json",
    })
    with urllib.request.urlopen(req, timeout=30) as r:
        data = json.load(r)
    icons = data.get("icons", {})
    aliases = data.get("aliases", {})
    set_defaults = (data.get("width", 16), data.get("height", 16))

    def resolve(name: str, depth: int = 0):
        if name in icons:
            return icons[name]
        parent = aliases.get(name, {}).get("parent")
        if parent and depth < 5:
            return resolve(parent, depth + 1)
        return None

    return {n: resolve(n) for n in names}, set_defaults


def build_svg(icon: dict, set_defaults: tuple) -> str:
    """SVG с явными width/height (для декодера) и корректным viewBox
    из размеров иконки (или дефолтов набора)."""
    w = icon.get("width", set_defaults[0])
    h = icon.get("height", set_defaults[1])
    return (f'<svg xmlns="http://www.w3.org/2000/svg" '
            f'width="256" height="256" viewBox="0 0 {w} {h}">'
            f'{icon["body"]}</svg>')


def main() -> int:
    dry = "--dry" in sys.argv
    ok = fails = 0
    for set_name in ["meteocons-fill", "meteocons", "twemoji", "wi"]:
        prefix = "meteocons" if set_name.startswith("meteocons") else set_name
        sem_to_name = icons_for_set(set_name)
        names = sorted(set(sem_to_name.values()))
        try:
            icons, set_defaults = fetch_batch(prefix, names)
        except Exception as e:
            print(f"FAIL batch {prefix}: {e}")
            fails += len(OWM) + len(ACCU)
            continue
        set_dir = DEST / set_name
        if not dry:
            set_dir.mkdir(parents=True, exist_ok=True)
        for provider_map, tag in ((OWM, "owm"), (ACCU, "accu")):
            for code, sem in provider_map.items():
                name = sem_to_name[sem]
                icon = icons.get(name)
                if icon is None:
                    print(f"FAIL {set_name}/{tag}-{code}: нет '{name}' в ответе")
                    fails += 1
                    continue
                if dry:
                    ok += 1
                    continue
                svg = build_svg(icon, set_defaults)
                if len(svg) < 100:
                    print(f"FAIL {set_name}/{tag}-{code}: подозрительно короткий svg")
                    fails += 1
                    continue
                (set_dir / f"{tag}-{code}.svg").write_text(svg, encoding="utf-8")
                ok += 1
    print(f"OK={ok} FAILS={fails}")
    return 1 if fails else 0


if __name__ == "__main__":
    sys.exit(main())
