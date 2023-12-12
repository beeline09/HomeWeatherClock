package ru.weatherclock.adg.app.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import ru.weatherclock.adg.app.presentation.components.text.AutoSizeText
import ru.weatherclock.adg.app.presentation.screens.home.components.calendar

@Composable
fun HomeScreen(screenModel: HomeScreenViewModel = koinInject()) {
    val forecast by screenModel.forecast.collectAsState()
    LaunchedEffect(Unit) { screenModel.onLaunch() }
    val dot by screenModel.dot.collectAsState()
    val time by screenModel.time.collectAsState()
    val date by screenModel.date.collectAsState()

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
            AutoSizeText(
                text = "${time.first}${dot}${time.second}",
                maxLines = 1,
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.CenterVertically)
                    .background(Color.Red),
                minTextSize = 100.sp,
                maxTextSize = 500.sp,
                alignment = Alignment.Center,
                style = MaterialTheme.typography.bodyLarge,
            )
            Column(modifier = Modifier.weight(0.2f)) {
                calendar(
                    modifier = Modifier.weight(0.5f),
                    dayOfMonth = date.first,
                    month = date.second,
                    year = date.third,
                    dayName = "Воскресенье"
                )
                Row(modifier = Modifier.fillMaxSize().weight(0.5f).background(Color.White)) {
//                    BasisEpicCalendar(
//                        modifier = Modifier.fillMaxSize().background(Color.Gray),
//                        state = rememberBasisEpicCalendarState(
//                            currentMonth = EpicMonth.now(TimeZone.currentSystemDefault()),
//                            config = rememberBasisEpicCalendarConfig(
//                                rowsSpacerHeight = 1.dp,
//                                dayOfWeekViewHeight = 20.dp,
//                                dayOfMonthViewHeight = 20.dp,
//                                columnWidth = 20.dp,
//                                dayOfWeekViewShape = RoundedCornerShape(6.dp),
//                                dayOfMonthViewShape = RoundedCornerShape(6.dp),
//                                contentPadding = PaddingValues(0.dp),
//                                contentColor = Color.Unspecified,
//                                displayDaysOfAdjacentMonths = false,
//                                displayDaysOfWeek = false
//                            )
//                        )
//                    )
                }
            }
        }
        val colors = listOf(
            Color.Yellow,
            Color.Green,
            Color.Black,
            Color.Magenta,
            Color.Cyan
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier.fillMaxSize().weight(0.3f)
        ) {
            itemsIndexed(colors) { index, item ->
                Box(
                    modifier = Modifier.aspectRatio(1f).background(item)
                ) {
                    Row(Modifier.fillMaxHeight().fillMaxWidth().background(item)) {

                        Column(
                            Modifier.weight(1f).fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Number",
                                modifier = Modifier.fillMaxSize()
                            )
                            Text(text = "  $item")
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