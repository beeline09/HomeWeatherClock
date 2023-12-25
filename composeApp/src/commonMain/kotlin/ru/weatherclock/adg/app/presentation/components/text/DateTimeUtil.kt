package ru.weatherclock.adg.app.presentation.components.text

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import ru.homeweatherclock.adg.MR


@Composable
fun Int.toMonthName(): String = when (this) {
    1 -> stringResource(MR.strings.january_f)
    2 -> stringResource(MR.strings.february_f)
    3 -> stringResource(MR.strings.march_f)
    4 -> stringResource(MR.strings.april_f)
    5 -> stringResource(MR.strings.may_f)
    6 -> stringResource(MR.strings.june_f)
    7 -> stringResource(MR.strings.jule_f)
    8 -> stringResource(MR.strings.august_f)
    9 -> stringResource(MR.strings.september_f)
    10 -> stringResource(MR.strings.october_f)
    11 -> stringResource(MR.strings.november_f)
    12 -> stringResource(MR.strings.december_f)
    else -> ""
}