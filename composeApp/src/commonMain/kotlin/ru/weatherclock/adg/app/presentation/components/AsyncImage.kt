package ru.weatherclock.adg.app.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.rememberImageSuccessPainter
import com.seiko.imageloader.ui.AutoSizeBox

@Composable
fun AsyncImage(
    url: String,
    contentScale: ContentScale = ContentScale.Crop,
    modifier: Modifier
) {
    AutoSizeBox(url) { action ->
        when (action) {
            is ImageAction.Success -> {
                Image(
                    rememberImageSuccessPainter(action),
                    contentDescription = "image",
                    contentScale = contentScale
                )
            }

            is ImageAction.Loading -> {
                Box(
                    modifier = modifier,
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ImageAction.Failure -> {
                Text(action.error.message ?: "Error")
            }
        }
    }
}
