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
import ru.weatherclock.adg.app.presentation.components.calendar.Calendar
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.DateInput
import ru.weatherclock.adg.app.presentation.components.player.AudioPlayer
import ru.weatherclock.adg.app.presentation.components.player.rememberPlayerState
import ru.weatherclock.adg.app.presentation.components.text.AutoSizeText
import ru.weatherclock.adg.app.presentation.screens.home.components.TextCalendar
import ru.weatherclock.adg.app.presentation.tabs.SettingsTab
import ru.weatherclock.adg.platformSpecific.byteArrayFromResources
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@OptIn(
    ExperimentalResourceApi::class,
    ExperimentalCoroutinesApi::class
)
@Composable
fun HomeScreen(screenModel: HomeScreenViewModel = koinInject()) {
    val forecast by screenModel.forecast.collectAsState()
    //Текущая дата для календаря
    val dateTime by screenModel.calendarDay.collectAsState()
    val prodCalendarDays by screenModel.prodCalendarDays.collectAsState(emptyList())
    val currentProdCalendarDay by screenModel.currentProdDay.collectAsState(null)

    val playerState = rememberPlayerState()
    LaunchedEffect(Unit) {
        val player = AudioPlayer(playerState)
        MR.files.casiohour.byteArrayFromResources(player::play)
        screenModel.onLaunch()
    }
    val dot by screenModel.dot.collectAsState()
    val time by screenModel.time.collectAsState()
    val date by screenModel.date.collectAsState()

    val navigator = LocalNavigator.currentOrThrow

    val colorPalette = LocalCustomColorsPalette.current

    if (currentProdCalendarDay.isNullOrBlank()) {

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
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                AutoSizeText(
                    color = colorPalette.clockText,
                    text = "${time.first}${dot}${time.second}",
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    minTextSize = 5.sp,
                    maxTextSize = 800.sp,
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
            Column(modifier = Modifier.aspectRatio(0.5f).align(Alignment.CenterVertically)) {
                TextCalendar(
                    modifier = Modifier.weight(0.5f).wrapContentWidth().fillMaxHeight(),
                    dayOfMonth = date.first,
                    month = date.second,
                    year = date.third,
                )
                Spacer(modifier = Modifier.height(5.dp))
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentWidth()
                        .weight(0.5f)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Calendar(
                        dateTime = dateTime,
                        dateHolder = DateInput.SingleDate(),
                        onDateSelected = {},
                        prodCalendarDays = prodCalendarDays
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
        Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(colorPalette.divider))
        Row(modifier = Modifier.fillMaxSize().weight(0.3f)) {
            colors.forEachIndexed { index, color ->
                Box(
                    modifier = Modifier.fillMaxSize().weight(1f)
                ) {
                    Row(Modifier.fillMaxSize()) {

                        Column(
                            Modifier.weight(1f).fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Number",
                                modifier = Modifier.wrapContentHeight().fillMaxWidth()
                            )
                            Text(text = "Color:  $color")
                        }

                        //Vertical divider avoiding the last cell in each row
                        if ((index + 1) % 5 != 0) {
                            Divider(
                                color = colorPalette.divider,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(1.dp)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(colorPalette.divider))
    }
}