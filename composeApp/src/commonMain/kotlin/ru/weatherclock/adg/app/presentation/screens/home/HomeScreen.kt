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
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.TextAutoSizeDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import homeweatherclock.composeapp.generated.resources.Res
import homeweatherclock.composeapp.generated.resources.weather_no_api_key_exception
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.getKoin
import ru.weatherclock.adg.app.data.dto.isHourInRangeForHide
import ru.weatherclock.adg.app.data.error.NoWeatherApiKeyException
import ru.weatherclock.adg.app.data.util.atStartOfDay
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay
import ru.weatherclock.adg.app.domain.model.forecast.Severity
import ru.weatherclock.adg.app.presentation.components.calendar.Calendar
import ru.weatherclock.adg.app.presentation.components.calendar.CalendarCallbackData
import ru.weatherclock.adg.app.presentation.components.calendar.color
import ru.weatherclock.adg.app.presentation.components.calendar.localizedDescription
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.DateInput
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.now
import ru.weatherclock.adg.app.presentation.components.calendar.stringForToast
import ru.weatherclock.adg.app.presentation.components.calendar.toMessageDateString
import ru.weatherclock.adg.app.presentation.components.player.AudioPlayer
import ru.weatherclock.adg.app.presentation.components.player.ResourceWrapper
import ru.weatherclock.adg.app.presentation.components.util.getColor
import ru.weatherclock.adg.app.presentation.components.util.padStart
import ru.weatherclock.adg.app.presentation.components.weather.WeatherCell
import ru.weatherclock.adg.app.presentation.screens.home.components.ClockText
import ru.weatherclock.adg.app.presentation.screens.home.components.TextCalendar
import ru.weatherclock.adg.showToast
import ru.weatherclock.adg.theme.LocalCustomColorsPalette


@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit,
) {
    // VM в скоупе NavBackStackEntry: смерть экрана → onCleared →
    // отмена всех корутин вьюмодели
    val koin = getKoin()
    val screenModel: HomeScreenViewModel = viewModel { koin.get() }
    val colorPalette = LocalCustomColorsPalette.current
    val state by screenModel.state.collectAsState()
    val settings = state.appSettings
    val timeConfig = settings.timeConfig
    val uiConfig = settings.uiConfig
    val weatherConfig = settings.weatherConfig
    val calendarConfig = settings.calendarConfig
    val prodCalendarConfig = calendarConfig.prodCalendarConfig
    // Погода — из самообновляющегося источника: рекомпозиция блока погоды
    // только при реальном изменении данных
    val forecast by screenModel.forecast.collectAsState()
    val forecast5Days = forecast?.dailyForecast.orEmpty()
    val headline = forecast?.headline
    val date = state.date
    val prodCalendarDays: List<ProdCalendarDay> = state.prodCalendarDaysForCurrentMonth
    val currentProdCalendarDay = state.currentProdDay
    val currentProdCalendarDayStr = state.currentProdDay?.localizedDescription()
    val isHourInRangeForHide = uiConfig.isHourInRangeForHide(LocalDateTime.now().hour)
    val weatherVisible =
        (!isHourInRangeForHide || !uiConfig.isWeatherHidden) && weatherConfig.weatherEnabled
    val textCalendarVisible =
        (!isHourInRangeForHide || !uiConfig.isTextCalendarHidden) && calendarConfig.textCalendarEnabled
    val gridCalendarVisible =
        (!isHourInRangeForHide || !uiConfig.isGridCalendarHidden) && calendarConfig.gridCalendarEnabled


    // Ежечасный бип — по событию
    LaunchedEffect(Unit) {
        screenModel.hourlyBeep.collect {
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
    // События стейт-машины погоды (нет ключа — тост раз в час при
    // включённой погоде; сетевые ошибки туда не попадают)
    LaunchedEffect(Unit) {
        screenModel.forecastErrors.collect {
            showToast(
                text = if (it is NoWeatherApiKeyException) noWeatherApiKeysError
                else it.message.orEmpty()
            )
        }
    }

    var dateSelected by remember {
        mutableStateOf(
            false to CalendarCallbackData()
        )
    }

    // Сайд-эффект (тост) — в LaunchedEffect, не в теле композиции:
    // раньше повторная рекомпозиция могла показать тост ещё раз.
    // Строка собирается в композиции (toMessageDateString/stringForToast
    // — @Composable), показ — только в эффекте
    val pendingToastText = dateSelected.second.takeIf { dateSelected.first }?.let { data ->
        buildString {
            append(data.selectedDate.toMessageDateString())
            append(". ")
            append(data.prodCalendarDay.stringForToast(data.selectedDate.date))
        }
    }
    LaunchedEffect(pendingToastText) {
        if (pendingToastText != null) {
            showToast(pendingToastText)
            dateSelected = false to CalendarCallbackData()
        }
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
                    ClockText(
                        hourMinute = screenModel.hourMinute,
                        dotsOn = screenModel.dotsOn,
                        hourWithLeadingZero = timeConfig.hourWithLeadingZero,
                        dotsFlashEnabled = timeConfig.dotsFlashEnabled,
                        dotsFlashAnimated = timeConfig.dotsFlashAnimated,
                        clockTextColor = colorPalette.clockText,
                        dotsOffColor = colorPalette.background,
                        modifier = Modifier.fillMaxSize().align(Alignment.Center),
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
                                    onOpenSettings()
                                }, modifier = Modifier.align(Alignment.Center).size(150.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Settings,
                                    contentDescription = "Settings",
                                    tint = Color.LightGray,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
                if (!headline.isNullOrBlank() && weatherVisible) {
                    BasicText(
                        text = headline,
                        autoSize = TextAutoSize.StepBased(
                            minFontSize = 5.sp, maxFontSize = 25.sp, stepSize = 0.25.sp
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = (forecast?.severity ?: Severity.UNKNOWN).getColor(),
                            textAlign = TextAlign.Start
                        ),
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth().padding(all = 5.dp)
                            .wrapContentHeight(align = Alignment.CenterVertically),
                    )
                }
            }

            if (textCalendarVisible || gridCalendarVisible) {
                Column(modifier = Modifier.aspectRatio(0.4f).align(Alignment.CenterVertically)) {
                    if (textCalendarVisible) {
                        TextCalendar(
                            modifier = Modifier.weight(0.5f).wrapContentWidth().fillMaxHeight()
                                .padding(horizontal = 5.dp),
                            dayOfMonth = date.day,
                            month = date.month.number,
                            year = date.year,
                            currentProdCalendarDay
                        )
                    }
                    if ((textCalendarVisible || gridCalendarVisible) && prodCalendarConfig.dayDescriptionEnabled) {
                        if (!currentProdCalendarDayStr.isNullOrBlank()) {
                            BasicText(
                                text = currentProdCalendarDayStr,
                                autoSize = TextAutoSize.StepBased(
                                    minFontSize = 6.sp, maxFontSize = 22.sp, stepSize = 0.25.sp
                                ),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = currentProdCalendarDay?.color() ?: colorPalette.dateDay,
                                    textAlign = TextAlign.Center
                                ),
                                maxLines = 1,
                                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                                    .padding(horizontal = 5.dp),
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
                                // Время обнуляем: в пределах дня аргумент равен
                                // сам себе — ежесекундный тик не рекомпозирует
                                // сетку (смена дня = новый аргумент)
                                dateTime = date.atStartOfDay(),
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
                        Spacer(
                            modifier = Modifier.fillMaxHeight().width(1.dp)
                                .background(color = colorPalette.divider),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(colorPalette.divider))
        }

    }
}