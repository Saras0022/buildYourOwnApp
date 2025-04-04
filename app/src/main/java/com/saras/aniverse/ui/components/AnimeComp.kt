package com.saras.aniverse.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.saras.aniverse.imageRequest
import com.saras.network.Anime

@Composable
fun AnimeComp(type: String, animeList: List<Anime>, onClicked: (String) -> Unit) {
    val context = LocalContext.current

    Text(text = type, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(horizontal = 8.dp))
    LazyRow(
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 24.dp)
    ) {
        items(animeList) { anime ->
            Card(
                onClick = { onClicked(anime.id) },
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .width(150.dp)
                    .height(200.dp)
            ) {
                Box(contentAlignment = Alignment.BottomStart) {

                    // Checking painterState and Showing Screen based on painterState (Error, Loading, Success)
                    val painter = rememberAsyncImagePainter(imageRequest(context, anime.image))
                    val painterState by painter.state.collectAsState()

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
                    Text(anime.title, fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0, 0, 0, 0xBA))
                            .padding(horizontal = 2.dp)
                    )
                }

            }
        }
    }
}