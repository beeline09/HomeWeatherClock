package ru.weatherclock.adg.app.domain.model

object RussiaRegions {}

val russiaRegions: Map<String, RegionSet>
    get() = mapOf(
        "Вся Россия" to 0,
        "Республика Адыгея" to 1,
        "Республика Башкортостан" to regionSetOf(
            2,
            102,
            702
        ),
        "Республика Бурятия" to regionSetOf(
            3,
            103
        ),
        "Республика Алтай" to 4,
        "Республика Дагестан" to 5,
        "Республика Ингушетия" to 6,
        "Кабардино-Балкарская Республика" to 7,
        "Республика Калмыкия" to 8,
        "Республика Карачаево-Черкесия" to 9,
        "Республика Карелия" to 10,
        "Республика Коми" to regionSetOf(
            11,
            111
        ),
        "Республика Марий Эл" to 12,
        "Республика Мордовия" to regionSetOf(
            13,
            113
        ),
        "Республика Саха (Якутия)" to 14,
        "Республика Северная Осетия — Алания" to 15,
        "Республика Татарстан" to regionSetOf(
            16,
            116,
            716
        ),
        "Республика Тыва" to 17,
        "Удмуртская Республика" to regionSetOf(
            18,
            188
        ),
        "Республика Хакасия" to 19,
        "Чувашская Республика" to regionSetOf(
            21,
            121
        ),
        "Алтайский край" to regionSetOf(
            22,
            122
        ),
        "Краснодарский край" to regionSetOf(
            23,
            93,
            123,
            193
        ),
        "Красноярский край" to regionSetOf(
            24,
            84,
            88,
            124
        ),
        "Приморский край" to regionSetOf(
            25,
            125,
            725
        ),
        "Ставропольский край" to regionSetOf(
            26,
            126
        ),
        "Хабаровский край" to regionSetOf(
            27,
            127
        ),
        "Амурская область" to 28,
        "Архангельская область" to 29,
        "Астраханская область" to regionSetOf(
            30,
            130
        ),
        "Белгородская область" to 31,
        "Брянская область" to 32,
        "Владимирская область" to 33,
        "Волгоградская область" to regionSetOf(
            34,
            134
        ),
        "Вологодская область" to 35,
        "Воронежская область" to regionSetOf(
            36,
            136
        ),
        "Ивановская область" to 37,
        "Иркутская область" to regionSetOf(
            38,
            138
        ),
        "Калининградская область" to regionSetOf(
            39,
            91
        ),
        "Калужская область" to 40,
        "Камчатский край" to regionSetOf(
            41,
            82
        ),
        "Кемеровская область" to regionSetOf(
            42,
            142
        ),
        "Кировская область" to 43,
        "Костромская область" to 44,
        "Курганская область" to 45,
        "Курская область" to 46,
        "Ленинградская область" to regionSetOf(
            47,
            147
        ),
        "Липецкая область" to 48,
        "Магаданская область" to 49,
        "Московская область" to regionSetOf(
            50,
            90,
            150,
            190,
            750,
            790
        ),
        "Мурманская область" to 51,
        "Нижегородская область" to regionSetOf(
            52,
            152,
            252
        ),
        "Новгородская область" to 53,
        "Новосибирская область" to regionSetOf(
            54,
            154,
            754
        ),
        "Омская область" to regionSetOf(
            55,
            155
        ),
        "Оренбургская область" to regionSetOf(
            56,
            156
        ),
        "Орловская область" to 57,
        "Пензенская область" to regionSetOf(
            58,
            158
        ),
        "Пермский край" to regionSetOf(
            59,
            81,
            159
        ),
        "Псковская область" to 60,
        "Ростовская область" to regionSetOf(
            61,
            161,
            761
        ),
        "Рязанская область" to 62,
        "Самарская область" to regionSetOf(
            63,
            163,
            763
        ),
        "Саратовская область" to regionSetOf(
            64,
            164
        ),
        "Сахалинская область" to 65,
        "Свердловская область" to regionSetOf(
            66,
            96,
            166,
            196
        ),
        "Смоленская область" to 67,
        "Тамбовская область" to 68,
        "Тверская область" to regionSetOf(
            69,
            169
        ),
        "Томская область" to 70,
        "Тульская область" to 71,
        "Тюменская область" to regionSetOf(
            72,
            172
        ),
        "Ульяновская область" to regionSetOf(
            73,
            173
        ),
        "Челябинская область" to regionSetOf(
            74,
            174,
            774
        ),
        "Забайкальский край" to regionSetOf(
            75,
            80
        ),
        "Ярославская область" to 76,
        "Москва" to regionSetOf(
            77,
            97,
            99,
            177,
            199,
            197,
            777,
            797,
            799,
            977,
            277,
            297,
            299
        ),
        "Санкт-Петербург" to regionSetOf(
            78,
            98,
            178,
            198
        ),
        "Еврейская автономная область" to 79,
        "Донецкая Народная Республика" to regionSetOf(
            80,
            180
        ),
        "Луганская Народная Республика" to regionSetOf(
            81,
            181
        ),
        "Республика Крым" to regionSetOf(
            82,
            777
        ),
        "Ненецкий автономный округ" to 83,
        "Херсонская область" to regionSetOf(
            84,
            184
        ),
        "Запорожская область" to regionSetOf(
            85,
            185
        ),
        "Ханты-Мансийский автономный округ Югра" to regionSetOf(
            86,
            186
        ),
        "Чукотский автономный округ" to 87,
        "Ямало-Ненецкий автономный округ" to 89,
        "Севастополь" to regionSetOf(
            92,
            192
        ),
        "Байконур" to 94,
        "Чеченская республика" to 95,

        )

class RegionSet(private val regions: Set<String> = setOf()): Set<String> {

    override val size: Int
        get() = regions.size

    override fun isEmpty(): Boolean {
        return regions.isEmpty()
    }

    override fun iterator(): Iterator<String> {
        return regions.iterator()
    }

    override fun containsAll(elements: Collection<String>): Boolean {
        return regions.containsAll(elements)
    }

    override fun contains(element: String): Boolean {
        return regions.contains(element)
    }

    fun sorted(): List<String> = regions.sortedBy { it }

}

private fun regionSetOf(vararg elements: String): RegionSet = RegionSet(elements.toSet())

private fun regionSetOf(vararg elements: Int): RegionSet =
    RegionSet(elements.map(::toRegionString).toSet())

private infix fun String.to(that: Int): Pair<String, RegionSet> = this to regionSetOf(that)

private fun toRegionString(number: Int): String {
    if (number < 10) return number.toString().padStart(
        2,
        '0'
    )
    return number.toString()
}
