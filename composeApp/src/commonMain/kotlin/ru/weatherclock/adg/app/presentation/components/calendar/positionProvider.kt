package ru.weatherclock.adg.app.presentation.components.calendar

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider

data class DesktopDropdownMenuPositionProvider(
    val contentOffset: DpOffset = DpOffset.Zero,
    val density: Density,
    val onPositionCalculated: (IntRect, IntRect) -> Unit = { _, _ -> },
): PopupPositionProvider {

    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        // The min margin above and below the menu, relative to the screen.
        val verticalMargin = 0
        // The content offset specified using the dropdown offset parameter.
        val contentOffsetX = with(density) { contentOffset.x.roundToPx() }
        val contentOffsetY = with(density) { contentOffset.y.roundToPx() }

        // Compute horizontal position.
        val toRight = anchorBounds.left + contentOffsetX
        val toLeft = anchorBounds.right - contentOffsetX - popupContentSize.width
        val toDisplayRight = windowSize.width - popupContentSize.width
        val toDisplayLeft = 0
        val x = if (layoutDirection == LayoutDirection.Ltr) {
            sequenceOf(
                toRight,
                toLeft,
                toDisplayRight
            )
        } else {
            sequenceOf(
                toLeft,
                toRight,
                toDisplayLeft
            )
        }.firstOrNull {
            it >= 0 && it + popupContentSize.width <= windowSize.width
        } ?: toLeft

        // Compute vertical position.
        val toBottom = maxOf(
            anchorBounds.bottom + contentOffsetY,
            verticalMargin
        )
        val toTop = anchorBounds.top - contentOffsetY - popupContentSize.height
        val toCenter = anchorBounds.top - popupContentSize.height / 2
        val toDisplayBottom = windowSize.height - popupContentSize.height - verticalMargin
        var y = sequenceOf(
            toBottom,
            toTop,
            toCenter,
            toDisplayBottom
        ).firstOrNull {
            it >= verticalMargin &&
                    it + popupContentSize.height <= windowSize.height - verticalMargin
        } ?: toTop

        // Desktop specific vertical position checking
        val aboveAnchor = anchorBounds.top + contentOffsetY
        val belowAnchor = windowSize.height - anchorBounds.bottom - contentOffsetY

        if (belowAnchor >= aboveAnchor) {
            y = anchorBounds.bottom + contentOffsetY
        }

        if (y + popupContentSize.height > windowSize.height) {
            y = windowSize.height - popupContentSize.height
        }

        y = y.coerceAtLeast(0)

        onPositionCalculated(
            anchorBounds,
            IntRect(
                x,
                y,
                x + popupContentSize.width,
                y + popupContentSize.height
            )
        )
        return IntOffset(
            x,
            y
        )
    }
}