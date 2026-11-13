package ru.weatherclock.adg.app.presentation.screens.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.TextAutoSizeDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.StateFlow
import ru.weatherclock.adg.app.presentation.components.util.padStart

/**
 * Часы: рекомпозируются ТОЛЬКО сами (не весь экран).
 * - час/минуты: обновление при смене минуты (StateFlow дедуплицирует пары);
 * - точки: анимация — плавный перелив цвета в пределах этого компонента,
 *   без анимации — флип раз в секунду, мигание выключено — вообще не тикают.
 */
@Composable
fun ClockText(
    hourMinute: StateFlow<Pair<Int, Int>>,
    dotsOn: StateFlow<Boolean>,
    hourWithLeadingZero: Boolean,
    dotsFlashEnabled: Boolean,
    dotsFlashAnimated: Boolean,
    clockTextColor: Color,
    dotsOffColor: Color,
    modifier: Modifier = Modifier,
) {
    val time by hourMinute.collectAsState()
    val hour = time.first
    val minute = time.second
    val dots by dotsOn.collectAsState()
    val dotsColor = when {
        dotsFlashEnabled && dotsFlashAnimated -> animateColorAsState(
            targetValue = if (dots) clockTextColor else dotsOffColor,
            animationSpec = tween(400, easing = LinearEasing),
            label = "dotsColor"
        ).value

        dotsFlashEnabled -> if (dots) clockTextColor else dotsOffColor

        else -> clockTextColor
    }
    BasicText(
        text = buildAnnotatedString {
            if (hourWithLeadingZero) {
                append(hour.padStart(2))
            } else {
                append(hour.toString())
            }
            withStyle(SpanStyle(color = dotsColor)) {
                append(":")
            }
            append(minute.padStart(2))
        },
        maxLines = 1,
        autoSize = TextAutoSize.StepBased(
            maxFontSize = 1000.sp,
            minFontSize = TextAutoSizeDefaults.MinFontSize,
            stepSize = 0.25.sp
        ),
        style = MaterialTheme.typography.bodyLarge.copy(
            color = clockTextColor, textAlign = TextAlign.Center
        ),
        modifier = modifier.fillMaxSize(),
    )
}
