package ru.weatherclock.adg.app.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import homeweatherclock.composeapp.generated.resources.Res
import homeweatherclock.composeapp.generated.resources.weather_no_api_key_exception
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.weatherclock.adg.app.data.dto.isHourInRangeForHide
import ru.weatherclock.adg.app.data.error.NoWeatherApiKeyException
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay
import ru.weatherclock.adg.app.domain.model.calendar.stringForCalendar
import ru.weatherclock.adg.app.presentation.components.calendar.Calendar
import ru.weatherclock.adg.app.presentation.components.calendar.CalendarCallbackData
import ru.weatherclock.adg.app.presentation.components.calendar.color
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.DateInput
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.now
import ru.weatherclock.adg.app.presentation.components.calendar.stringForToast
import ru.weatherclock.adg.app.presentation.components.calendar.toMessageDateString
import ru.weatherclock.adg.app.presentation.components.player.AudioPlayer
import ru.weatherclock.adg.app.presentation.components.player.ResourceWrapper
import ru.weatherclock.adg.app.presentation.components.text.AutoSizeText
import ru.weatherclock.adg.app.presentation.components.util.getColor
import ru.weatherclock.adg.app.presentation.components.util.padStart
import ru.weatherclock.adg.app.presentation.components.weather.WeatherCell
import ru.weatherclock.adg.app.presentation.screens.home.components.TextCalendar
import ru.weatherclock.adg.app.presentation.tabs.SettingsTab
import ru.weatherclock.adg.showToast
import ru.weatherclock.adg.theme.LocalCustomColorsPalette


@Composable
fun HomeScreen(screenModel: HomeScreenViewModel = koinInject()) {
    val navigator = LocalNavigator.currentOrThrow
    val colorPalette = LocalCustomColorsPalette.current
    val state by screenModel.state.collectAsState()
    val settings = state.appSettings
    val timeConfig = settings.timeConfig
    val uiConfig = settings.uiConfig
    val weatherConfig = settings.weatherConfig
    val calendarConfig = settings.calendarConfig
    val prodCalendarConfig = calendarConfig.prodCalendarConfig
    val forecast5Days = state.forecast5Days
    val headline = state.headline
    val dateTime = state.dateTime
    val date = dateTime.date
    val prodCalendarDays: List<ProdCalendarDay> = state.prodCalendarDaysForCurrentMonth
    val currentProdCalendarDay = state.currentProdDay
    val currentProdCalendarDayStr = state.currentProdDay?.stringForCalendar()
    val dotsColor = when {
        timeConfig.dotsFlashEnabled && timeConfig.dotsFlashAnimated -> {
            val dotsColorAnimated: Color by animateColorAsState(
                if (state.dotsShowed) colorPalette.clockText else colorPalette.background,
                animationSpec = tween(
                    400, easing = LinearEasing
                )
            )
            dotsColorAnimated
        }

        timeConfig.dotsFlashEnabled -> {
            if (state.dotsShowed) colorPalette.clockText else colorPalette.background
        }

        else -> {
            colorPalette.clockText
        }
    }
    val isHourInRangeForHide = uiConfig.isHourInRangeForHide(LocalDateTime.now().hour)
    val weatherVisible =
        (!isHourInRangeForHide || !uiConfig.isWeatherHidden) && weatherConfig.weatherEnabled
    val textCalendarVisible =
        (!isHourInRangeForHide || !uiConfig.isTextCalendarHidden) && calendarConfig.textCalendarEnabled
    val gridCalendarVisible =
        (!isHourInRangeForHide || !uiConfig.isGridCalendarHidden) && calendarConfig.gridCalendarEnabled


    LaunchedEffect(state.hourlyBeepIncrement) {
        if (state.hourlyBeepIncrement > 0) {
            AudioPlayer.play(ResourceWrapper("files/casiohour.mp3"))
        }
    }

    val noWeatherApiKeysError = stringResource(Res.string.weather_no_api_key_exception)
    LaunchedEffect(Unit) {
        screenModel.onLaunch()
        screenModel.catch {
            if (it is NoWeatherApiKeyException) {
                showToast(text = noWeatherApiKeysError)
            } else {
                showToast(text = it.message.orEmpty())
            }
        }
    }

    var dateSelected by remember {
        mutableStateOf(
            false to CalendarCallbackData()
        )
    }



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
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)
            .background(colorPalette.background), verticalArrangement = Arrangement.Center
    ) {

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize().weight(1f)
        ) {
            //Часы с кнопокой настроек
            Column(
                modifier = Modifier.weight(1f).fillMaxSize()
            ) {
                val columnScope = this
                Box(modifier = Modifier.fillMaxSize().weight(1f).clickable {
                    if (state.settingsButtonShowed) {
                        screenModel.intent(HomeScreenIntent.Settings.Hide)
                    } else {
                        screenModel.intent(HomeScreenIntent.Settings.Show)
                    }
                }) {
                    AutoSizeText(
                        color = colorPalette.clockText,
                        text = buildAnnotatedString {
                            if (timeConfig.hourWithLeadingZero) {
                                append(state.hour.padStart(2))
                            } else {
                                append(state.hour.toString())
                            }
                            withStyle(SpanStyle(color = dotsColor)) {
                                append(":")
                            }
                            append(state.minute.padStart(2))
                        },
                        maxLines = 1,
                        modifier = Modifier.fillMaxSize(),
                        minTextSize = 5.sp,
                        maxTextSize = 1000.sp,
                        alignment = Alignment.Center,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    columnScope.AnimatedVisibility(
                        visible = state.settingsButtonShowed,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut(),
                        label = "",
                        modifier = Modifier,
                    ) {
                        Box(modifier = Modifier.fillMaxSize().alpha(0.5f).background(Color.Black)) {
                            IconButton(
                                onClick = {
                                    screenModel.intent(HomeScreenIntent.Settings.Hide)
                                    navigator.push(SettingsTab)
                                }, modifier = Modifier.align(Alignment.Center)
                            ) {
                                Icon(
                                    Icons.Filled.Settings,
                                    contentDescription = "Settings",
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(150.dp)
                                )
                            }
                        }
                    }
                }
                if (!headline.isNullOrBlank() && weatherVisible) {
                    AutoSizeText(
                        color = state.headlineSeverity.getColor(),
                        text = headline,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth().padding(all = 5.dp)
                            .wrapContentHeight(align = Alignment.CenterVertically),
                        minTextSize = 5.sp,
                        maxTextSize = 25.sp,
                        stepGranularityTextSize = 1.sp,
                        alignment = Alignment.BottomStart,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            if (textCalendarVisible || gridCalendarVisible) {
                Column(modifier = Modifier.aspectRatio(0.4f).align(Alignment.CenterVertically)) {
                    if (textCalendarVisible) {
                        TextCalendar(
                            modifier = Modifier.weight(0.5f).wrapContentWidth().fillMaxHeight()
                                .padding(horizontal = 5.dp),
                            dayOfMonth = date.dayOfMonth,
                            month = date.monthNumber,
                            year = date.year,
                            currentProdCalendarDay
                        )
                    }
                    if ((textCalendarVisible || gridCalendarVisible) && prodCalendarConfig.dayDescriptionEnabled) {
                        if (!currentProdCalendarDayStr.isNullOrBlank()) {
                            AutoSizeText(
                                text = currentProdCalendarDayStr,
                                maxLines = 1,
                                maxTextSize = 22.sp,
                                minTextSize = 6.sp,
                                alignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                                    .padding(horizontal = 5.dp),
                                color = currentProdCalendarDay?.color() ?: colorPalette.dateDay
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        } else {
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                    if (gridCalendarVisible) {
                        Row(
                            modifier = Modifier.fillMaxHeight().wrapContentWidth().weight(0.3f)
                                .align(Alignment.CenterHorizontally).padding(horizontal = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Calendar(
                                dateTime = dateTime,
                                dateHolder = DateInput.SingleDate(),
                                onDateSelected = { s, p ->
                                    dateSelected = true to CalendarCallbackData(
                                        selectedDate = s, prodCalendarDay = p
                                    )
                                },
                                prodCalendarDays = prodCalendarDays
                            )
                        }
                    }
//                Spacer(Modifier.height(5.dp))
                }
            }
        }

        if (forecast5Days.isNotEmpty() && weatherVisible) {
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(colorPalette.divider))
            Row(modifier = Modifier.fillMaxSize().weight(0.3f)) {
                forecast5Days.forEachIndexed { index, dailyForecast ->
                    Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            WeatherCell(
                                forecast = dailyForecast, weatherConfigData = weatherConfig
                            )
                        }
                    }
                    //Vertical divider avoiding the last cell in each row
                    if ((index + 1) % forecast5Days.size != 0) {
                        Divider(
                            color = colorPalette.divider,
                            modifier = Modifier.fillMaxHeight().width(1.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(colorPalette.divider))
        }

    }
}