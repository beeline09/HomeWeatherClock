#!/usr/bin/env python3
# Скачивание наборов погодных иконок для bundled-ресурсов:
#  - meteocons-fill / meteocons: оригинальные production-SVG Meteocons v2.0.0
#    (raw.githubusercontent, SHA тега v2.0.0). Для OWM — официальная папка
#    openweathermap (коды 01d..50n), для AccuWeather — all/ по семантике.
#    ВАЖНО: тела из Iconify API НЕ годятся — там <symbol>+<use>, которые
#    SkSVG (skiko/coil) парсит, но не отрисовывает (см. SvgRasterTest).
#  - twemoji / wi: Iconify batch API (плоские path-тела, SkSVG рендерит).
#
# Результат: composeResources/files/weather_icons/<set>/{owm-<код>,accu-<код>}.svg
# Синхронно с маппингами в Kotlin (WeatherIcons.kt).
import json
import sys
from typing import Optional
import urllib.request
from pathlib import Path

DEST = Path(__file__).resolve().parent.parent / \
    "composeApp/src/commonMain/composeResources/files/weather_icons"
METEOCONS_SHA = "fdd19749b9"  # v2.0.0
RAW = f"https://raw.githubusercontent.com/basmilius/meteocons/{METEOCONS_SHA}/production"
ICONIFY = "https://api.iconify.design"

# ---------- Коды OpenWeatherMap -> семантика (для twemoji/wi и Kotlin) ----------
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

# ---------- Семантика -> имя файла в наборе ----------
MC = {  # production/all (fill и line директории, имена одинаковые)
    "CLEAR_DAY": "clear-day", "CLEAR_NIGHT": "clear-night",
    "PARTLY_CLOUDY_DAY": "partly-cloudy-day", "PARTLY_CLOUDY_NIGHT": "partly-cloudy-night",
    "CLOUDY": "cloudy", "OVERCAST": "overcast",
    "FOG_DAY": "fog-day", "FOG_NIGHT": "fog-night",
    "SHOWERS": "rain",
    "RAIN_DAY": "partly-cloudy-day-rain", "RAIN_NIGHT": "partly-cloudy-night-rain",
    "OVERCAST_RAIN": "rain",
    "THUNDER_DAY": "thunderstorms-day", "THUNDER_NIGHT": "thunderstorms-night",
    "SNOW": "snow", "OVERCAST_SNOW": "snow",
    "SLEET": "sleet",
    "HAZE_DAY": "haze-day", "HAZE_NIGHT": "haze-night",
    "WIND": "wind", "HOT": "thermometer-warmer", "COLD": "thermometer-colder",
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


def fetch_url(url: str) -> Optional[bytes]:
    req = urllib.request.Request(url, headers={
        "User-Agent": "HomeWeatherClock-assets/1.0 (build script)",
    })
    try:
        with urllib.request.urlopen(req, timeout=30) as r:
            data = r.read()
        return data if len(data) > 100 else None
    except Exception as e:
        print(f"FAIL {url}: {e}")
        return None


def iconify_batch(prefix: str, names: list) -> dict:
    """Батч: {имя: {body, width, height}} с разрешением алиасов."""
    url = f"{ICONIFY}/{prefix}.json?icons={','.join(names)}"
    req = urllib.request.Request(url, headers={
        "User-Agent": "HomeWeatherClock-assets/1.0 (build script)",
        "Accept": "application/json",
    })
    with urllib.request.urlopen(req, timeout=30) as r:
        data = json.load(r)
    icons = data.get("icons", {})
    aliases = data.get("aliases", {})
    defaults = (data.get("width", 16), data.get("height", 16))

    def resolve(name: str, depth: int = 0):
        if name in icons:
            return icons[name]
        parent = aliases.get(name, {}).get("parent")
        if parent and depth < 5:
            return resolve(parent, depth + 1)
        return None

    return {n: resolve(n) for n in names}, defaults


def wrap_svg(body: str, w: int, h: int) -> str:
    return (f'<svg xmlns="http://www.w3.org/2000/svg" '
            f'width="256" height="256" viewBox="0 0 {w} {h}">{body}</svg>')


def main() -> int:
    dry = "--dry" in sys.argv
    ok = fails = 0
    dest_all = DEST
    if not dry:
        dest_all.mkdir(parents=True, exist_ok=True)

    # --- meteocons-fill и meteocons: production v2.0.0 ---
    for set_name, sub in (("meteocons-fill", "fill"), ("meteocons", "line")):
        set_dir = dest_all / set_name
        if not dry:
            set_dir.mkdir(parents=True, exist_ok=True)
        # OWM: официальные файлы по кодам
        for code in OWM:
            url = f"{RAW}/{sub}/openweathermap/{code}.svg"
            data = fetch_url(url)
            if data is None:
                fails += 1
                continue
            if dry:
                ok += 1
                continue
            (set_dir / f"owm-{code}.svg").write_bytes(data)
            ok += 1
        # AccuWeather: семантика -> имя файла в all/
        for code, sem in ACCU.items():
            name = MC[sem]
            url = f"{RAW}/{sub}/all/{name}.svg"
            data = fetch_url(url)
            if data is None:
                print(f"FAIL {set_name}/accu-{code}: {name} не найден")
                fails += 1
                continue
            if dry:
                ok += 1
                continue
            (set_dir / f"accu-{code}.svg").write_bytes(data)
            ok += 1

    # --- twemoji и wi: Iconify batch ---
    for set_name, prefix, table in (("twemoji", "twemoji", TW), ("wi", "wi", WI)):
        names = sorted(set(table.values()))
        icons, defaults = iconify_batch(prefix, names)
        set_dir = dest_all / set_name
        if not dry:
            set_dir.mkdir(parents=True, exist_ok=True)
        for provider_map, tag in ((OWM, "owm"), (ACCU, "accu")):
            for code, sem in provider_map.items():
                icon = icons.get(table[sem])
                if icon is None:
                    print(f"FAIL {set_name}/{tag}-{code}")
                    fails += 1
                    continue
                if dry:
                    ok += 1
                    continue
                svg = wrap_svg(icon["body"],
                               icon.get("width", defaults[0]),
                               icon.get("height", defaults[1]))
                (set_dir / f"{tag}-{code}.svg").write_text(svg, encoding="utf-8")
                ok += 1

    print(f"OK={ok} FAILS={fails}")
    return 1 if fails else 0


if __name__ == "__main__":
    sys.exit(main())
