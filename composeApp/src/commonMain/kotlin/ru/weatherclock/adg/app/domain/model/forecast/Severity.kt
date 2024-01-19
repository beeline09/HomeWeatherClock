package ru.weatherclock.adg.app.domain.model.forecast

enum class Severity {

    /**
     * 0 = Неизвестно
     */
    UNKNOWN,

    /**
     * 1 = Значительно, Significant
     */
    SIGNIFICANT,

    /**
     * 2 = Важно, Major
     */
    MAJOR,

    /**
     * 3 = Умеренно, Moderate
     */
    MODERATE,

    /**
     * 4 = Незначительно, Minor
     */
    MINOR,

    /**
     * 5 = Минимально, Minimal
     */
    MINIMAL,

    /**
     * 6 = Незначительно, Insignificant
     */
    INSIGNIFICANT,

    /**
     *  7 = Информационно, Informational
     */
    INFORMATIONAL
}