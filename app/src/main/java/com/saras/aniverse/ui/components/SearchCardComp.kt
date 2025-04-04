package com.saras.aniverse.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.saras.aniverse.imageRequest
import com.saras.network.Anime

@Composable
fun SearchCardComp(anime: Anime, onAnimeClicked: (String) -> Unit) {
    val context = LocalContext.current

    OutlinedCard(
        onClick = { onAnimeClicked(anime.id) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).wrapContentHeight()) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .padding(8.dp).fillMaxWidth()
        ) {
            Row {
                Card(
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.width(75.dp).height(100.dp)
                ) {
                    val painter = rememberAsyncImagePainter(imageRequest(context, anime.image))
                    val painterState by painter.state.collectAsState()

                    // Checking Painter State to Show Image or Error or Loading
                    when (painterState) {
                        is AsyncImagePainter.State.Success -> {
                            Image(
                                painter = painter,
                                contentDescription = anime.title,
                                contentScale = ContentScale.FillBounds,
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
                }
                Spacer(Modifier.width(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(anime.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedCard { Text(anime.type ?: "", modifier = Modifier.padding(8.dp)) }
                        OutlinedCard { Text(anime.duration ?: "", modifier = Modifier.padding(8.dp)) }
                    }
                }
            }
        }
    }
}