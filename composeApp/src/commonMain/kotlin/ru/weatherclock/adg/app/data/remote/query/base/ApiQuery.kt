package ru.weatherclock.adg.app.data.remote.query.base

interface ApiQuery {

    fun buildUrl(): String
}