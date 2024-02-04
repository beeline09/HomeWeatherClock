package ru.weatherclock.adg.app.presentation.components.text

import kotlin.math.abs
import kotlin.math.min
import ru.weatherclock.adg.app.data.WeatherUnits

//желательно уместиться в 5 символов вместе с точкой,
//чтобы числа во всех ячейках занимали примерно одинаковый размер
fun Double.toTemperature(unitType: WeatherUnits): String = buildString {
    if (this@toTemperature < 0.0) {
        append("-")
        val absValue = abs(this@toTemperature)
        if (absValue < 10.0) {
            append(
                absValue.limitDecimals(maxDecimals = 2)
            ) //-9.20
        } else {
            append(
                absValue.limitDecimals(maxDecimals = 1)
            ) //-12.8
        }
    } else {
        if (this@toTemperature < 10.0) {
            append(
                this@toTemperature.limitDecimals(maxDecimals = 2).padEnd(
                    4,
                    '0'
                )
            )//_9.20
        } else {
            append(
                this@toTemperature.limitDecimals(maxDecimals = 1).padEnd(
                    4,
                    '0'
                )
            ) //_12.8
        }
    }
    append(
        when (unitType) {
            WeatherUnits.Standard -> "K"
            WeatherUnits.Imperial -> "°F"
            WeatherUnits.Metric -> "°C"
        }
    )

}

fun <T> T.limitDecimals(maxDecimals: Int): String {
    val input = this
    val result = input.toString().replace(
        " ",
        ""
    ).replace(
        "'",
        ""
    ).replace(
        "`",
        ""
    )
    val lastIndex = result.length - 1
    var pos = lastIndex
    while (pos >= 0 && result[pos] != '.') {
        pos--
    }
    return if (maxDecimals < 1 && pos >= 0) {
        result.substring(
            0,
            min(
                pos,
                result.length
            )
        )
    } else if (pos >= 0) {
        result.substring(
            0,
            min(
                pos + 1 + maxDecimals,
                result.length
            )
        )
    } else {
        return result
    }
}