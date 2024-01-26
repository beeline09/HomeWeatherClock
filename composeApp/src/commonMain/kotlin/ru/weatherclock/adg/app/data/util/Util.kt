package ru.weatherclock.adg.app.data.util

infix fun Int.isIn(that: Pair<Int, Int>): Boolean {
    return this in that.first..that.second
}