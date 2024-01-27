package ru.weatherclock.adg.app.data.util

infix fun Int.isInHours(that: Pair<Int, Int>): Boolean {
    val start = that.first
    val end = that.second
    return if (start < end) {
        this in start..end
    } else {
        val part1 = 0..end
        val part2 = start..<0
        this in part1 || this in part2
    }

}