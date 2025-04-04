package com.saras.aniverse.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.saras.network.AnimeEpisode
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImagePainter
import com.saras.aniverse.imageRequest

@Composable
fun EpisodeComp(episodeData: AnimeEpisode, image: String, onClicked: (String) -> Unit) {
    val context = LocalContext.current

    Card(
        onClick = {
            onClicked(episodeData.episodeId.substringAfter('?'))
        },
        shape = RoundedCornerShape(2.dp),
        modifier = Modifier
            .width(150.dp)
            .height(120.dp)
    ) {
        Box(
            contentAlignment = Alignment.BottomStart
        ) {

            // Checking painterState
            val painter = rememberAsyncImagePainter(imageRequest(context, image))
            val painterState by painter.state.collectAsState()

            when (painterState) {
                is AsyncImagePainter.State.Success -> {
                    Image(
                        painter = painter,
                        contentDescription = episodeData.title,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is AsyncImagePainter.State.Error -> {
                    ImageErrorComp()
                }
                else -> {
                    Box(Modifier.fillMaxSize().shimmerEffect())
                }
            }
            Text("${episodeData.number}" + "." + " ${episodeData.title}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.background(Color(0, 0, 0, 0xBA)).fillMaxWidth().padding(horizontal = 2.dp))
        }
    }
}