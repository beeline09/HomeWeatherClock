package ru.weatherclock.adg.app.presentation.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

/**
 * Bottom sheet в стилистике приложения — для длинных списков выбора
 * (на маленьких экранах AlertDialog не помещается).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockBottomSheetDialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val colorsPalette = LocalCustomColorsPalette.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colorsPalette.alertDialogBackground,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        dragHandle = null,
    ) {
        //Сразу на всю высоту: контент с изменяемой высотой (списки) не «прыгает»
        Column(
            modifier = Modifier.fillMaxHeight().fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = colorsPalette.clockText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}
