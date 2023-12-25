package ru.weatherclock.adg.app.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.datetime.LocalDateTime
import org.koin.compose.koinInject
import ru.weatherclock.adg.app.presentation.components.calendar.Calendar
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.DateInput
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.now
import ru.weatherclock.adg.app.presentation.components.calendar.styles.DateInputDefaults
import ru.weatherclock.adg.app.presentation.components.text.AutoSizeText
import ru.weatherclock.adg.app.presentation.screens.home.components.TextCalendar

@Composable
fun HomeScreen(screenModel: HomeScreenViewModel = koinInject()) {
    val forecast by screenModel.forecast.collectAsState()
    LaunchedEffect(Unit) { screenModel.onLaunch() }
    val dot by screenModel.dot.collectAsState()
    val time by screenModel.time.collectAsState()
    val date by screenModel.date.collectAsState()

    val navigator = LocalNavigator.currentOrThrow

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(Color.LightGray),
        verticalArrangement = Arrangement.Center
    ) {

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(Color.Green).fillMaxSize().weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(Color.Red)
            ) {
                AutoSizeText(
                    text = "${time.first}${dot}${time.second}",
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(Color.Red),
                    minTextSize = 5.sp,
                    maxTextSize = 800.sp,
                    alignment = Alignment.Center,
                    style = MaterialTheme.typography.bodyLarge,
                )
                val vis = true
                this@Row.AnimatedVisibility(
                    visible = vis,
                    enter = slideInHorizontally()
                            + expandHorizontally(expandFrom = Alignment.End)
                            + fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
                            + shrinkHorizontally()
                            + fadeOut(),
                ) {
                    Box(modifier = Modifier.fillMaxSize().alpha(0.5f).background(Color.Black)) {
                        IconButton(
                            onClick = { /* do something */ },
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Icon(
                                Icons.Filled.Settings,
                                contentDescription = "Favorite",
                                tint = Color.LightGray,
                                modifier = Modifier.size(150.dp)
                            )
                        }
                    }
                }
            }
            Column(modifier = Modifier.aspectRatio(0.5f).align(Alignment.CenterVertically)) {
                TextCalendar(
                    modifier = Modifier.weight(0.5f).wrapContentWidth().fillMaxHeight(),
                    dayOfMonth = date.first,
                    month = date.second,
                    year = date.third,
                    dayName = "Воскресенье"
                )
                Spacer(modifier = Modifier.height(5.dp))
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentWidth()
                        .weight(0.5f)
                        .background(Color.Black)
                        .align(Alignment.CenterHorizontally)
                        .background(Color.Magenta)
                ) {
                    val dateTime = mutableStateOf(LocalDateTime.now())
                    val holder = mutableStateOf<DateInput>(DateInput.SingleDate())
                    Calendar(
                        dateTime = dateTime,
                        dateHolder = holder,
                        background = Color.White,
                        errorMessage = mutableStateOf(""),
                        locale = DateInputDefaults.DateInputLocale.RU,
                        onDateSelected = {},
                    )
                }
                Spacer(Modifier.height(5.dp))
            }
        }
        val colors = listOf(
            Color.Yellow,
            Color.Green,
            Color.Black,
            Color.Magenta,
            Color.Cyan
        )
        Row(modifier = Modifier.fillMaxSize().weight(0.3f).background(Color.Blue)) {
            colors.forEachIndexed { index, color ->
                Box(
                    modifier = Modifier.fillMaxSize().background(color).weight(1f)
                ) {
                    Row(Modifier.fillMaxSize().background(color)) {

                        Column(
                            Modifier.weight(1f).fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Number",
                                modifier = Modifier.wrapContentHeight().fillMaxWidth()
                            )
                            Text(text = "Color:  $color")
                            Divider(
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth().height(1.dp)
                            ) //Horizontal divider
                        }

                        //Vertical divider avoiding the last cell in each row
                        if ((index + 1) % 5 != 0) {
                            Divider(
                                color = Color.Red,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }

}