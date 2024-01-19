package ru.weatherclock.adg.app.presentation.screens.home

import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.compose.koinInject
import ru.weatherclock.adg.MR
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay
import ru.weatherclock.adg.app.domain.model.calendar.stringForCalendar
import ru.weatherclock.adg.app.presentation.components.calendar.Calendar
import ru.weatherclock.adg.app.presentation.components.calendar.CalendarCallbackData
import ru.weatherclock.adg.app.presentation.components.calendar.color
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.DateInput
import ru.weatherclock.adg.app.presentation.components.calendar.stringForToast
import ru.weatherclock.adg.app.presentation.components.calendar.toMessageDateString
import ru.weatherclock.adg.app.presentation.components.player.AudioPlayer
import ru.weatherclock.adg.app.presentation.components.player.rememberPlayerState
import ru.weatherclock.adg.app.presentation.components.text.AutoSizeText
import ru.weatherclock.adg.app.presentation.components.util.getColor
import ru.weatherclock.adg.app.presentation.components.weather.WeatherCell
import ru.weatherclock.adg.app.presentation.screens.home.components.TextCalendar
import ru.weatherclock.adg.app.presentation.tabs.SettingsTab
import ru.weatherclock.adg.platformSpecific.fileName
import ru.weatherclock.adg.platformSpecific.rawResource
import ru.weatherclock.adg.showToast
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@OptIn(
    ExperimentalResourceApi::class,
    ExperimentalCoroutinesApi::class
)
@Composable
fun HomeScreen(screenModel: HomeScreenViewModel = koinInject()) {
    val state by screenModel.state.collectAsState()
    val forecast5Days = state.forecast5Days
    val headline = state.headline
    //Текущая дата для календаря
    val dateTime = state.dateTime
    val time = state.time
    val date = dateTime.date
    val prodCalendarDays: List<ProdCalendarDay> = state.prodCalendarDaysForCurrentMonth
    val currentProdCalendarDay = state.currentProdDay
    val currentProdCalendarDayStr = state.currentProdDay?.stringForCalendar()

    val playerState = rememberPlayerState()
    val player = AudioPlayer(playerState)
    LaunchedEffect(Unit) {
        MR.files.casiohour.fileName().rawResource().also {
            player.play(it)
        }
        screenModel.onLaunch()
    }

    var dateSelected by remember {
        mutableStateOf(
            false to CalendarCallbackData()
        )
    }

    val navigator = LocalNavigator.currentOrThrow

    val colorPalette = LocalCustomColorsPalette.current

    if (dateSelected.first) {
        val data = dateSelected.second
        val dateStr = data.selectedDate.toMessageDateString()
        val prodCalendarDay = data.prodCalendarDay
        val toastStr = buildString {
            append(dateStr)
            append(". ")
            append(prodCalendarDay.stringForToast(data.selectedDate.date))
        }
        showToast(toastStr)
        dateSelected = false to CalendarCallbackData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(colorPalette.background),
        verticalArrangement = Arrangement.Center
    ) {

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize().weight(1f)
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize().weight(1f)
                ) {
                    AutoSizeText(
                        color = colorPalette.clockText,
                        text = time,
                        maxLines = 1,
                        modifier = Modifier.fillMaxSize(),
                        minTextSize = 5.sp,
                        maxTextSize = 1000.sp,
                        alignment = Alignment.Center,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    val vis = false
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
                                onClick = {
                                    navigator.push(SettingsTab)
                                },
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
                if (!headline.isNullOrBlank()) {
                    AutoSizeText(
                        color = state.headlineSeverity.getColor(),
                        text = headline,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 5.dp)
                            .wrapContentHeight(align = Alignment.CenterVertically),
                        minTextSize = 5.sp,
                        maxTextSize = 25.sp,
                        stepGranularityTextSize = 1.sp,
                        alignment = Alignment.BottomStart,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            Column(modifier = Modifier.aspectRatio(0.4f).align(Alignment.CenterVertically)) {
                TextCalendar(
                    modifier = Modifier
                        .weight(0.5f)
                        .wrapContentWidth()
                        .fillMaxHeight()
                        .padding(horizontal = 5.dp),
                    dayOfMonth = date.dayOfMonth,
                    month = date.monthNumber,
                    year = date.year,
                    currentProdCalendarDay
                )
                if (!currentProdCalendarDayStr.isNullOrBlank()) {
                    AutoSizeText(
                        text = currentProdCalendarDayStr,
                        maxLines = 1,
                        maxTextSize = 22.sp,
                        minTextSize = 6.sp,
                        alignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(horizontal = 5.dp),
                        color = currentProdCalendarDay?.color() ?: colorPalette.dateDay
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                } else {
                    Spacer(modifier = Modifier.height(5.dp))
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentWidth()
                        .weight(0.3f)
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 5.dp),
                ) {
                    Calendar(
                        dateTime = dateTime,
                        dateHolder = DateInput.SingleDate(),
                        onDateSelected = { s, p ->
                            dateSelected = true to CalendarCallbackData(
                                selectedDate = s,
                                prodCalendarDay = p
                            )
                        },
                        prodCalendarDays = prodCalendarDays
                    )
                }
//                Spacer(Modifier.height(5.dp))
            }
        }

        if (forecast5Days.isNotEmpty()) {
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(colorPalette.divider))
            Row(modifier = Modifier.fillMaxSize().weight(0.3f)) {
                forecast5Days.forEachIndexed { index, dailyForecast ->
                    Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            WeatherCell(dailyForecast)
                        }
                    }
                    //Vertical divider avoiding the last cell in each row
                    if ((index + 1) % forecast5Days.size != 0) {
                        Divider(
                            color = colorPalette.divider,
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(colorPalette.divider))
        }

    }
}