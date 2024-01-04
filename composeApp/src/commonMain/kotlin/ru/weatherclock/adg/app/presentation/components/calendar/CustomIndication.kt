package ru.weatherclock.adg.app.presentation.components.calendar

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope

internal class CustomIndication(
    val pressColor: Color = Color.Red,
    val alpha: Float = 0.5f,
): Indication {

    private inner class DefaultIndicationInstance(
        private val isPressed: State<Boolean>,
    ): IndicationInstance {

        override fun ContentDrawScope.drawIndication() {

            drawContent()
            when {
                isPressed.value -> {
                    drawCircle(
                        radius = size.width,
                        color = pressColor.copy(
                            alpha = alpha
                        )
                    )
                }
            }
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val isPressed = interactionSource.collectIsPressedAsState()
        return remember(interactionSource) {
            DefaultIndicationInstance(isPressed)
        }
    }
}