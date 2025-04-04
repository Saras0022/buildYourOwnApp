package com.saras.aniverse.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.delay

@Composable
fun TopAiringComp(topTenAnimes: List<Anime>, onClicked: (String) -> Unit) {

    val context = LocalContext.current

    // Required Variables to animate Pager to scroll to next page automatically
    val pagerState = rememberPagerState { topTenAnimes.size }
    val pagerIsDragged by pagerState.interactionSource.collectIsDraggedAsState()
    val pageInteractionSource = remember { MutableInteractionSource() }
    val pageIsPressed by pageInteractionSource.collectIsPressedAsState()
    val autoAdvance = !pagerIsDragged && !pageIsPressed

    // Checking if Pager is not Dragged and not Pressed
    // If autoAdvance is true, scroll to nextPage
    if (autoAdvance) {
        LaunchedEffect(pagerState, pageInteractionSource) {
            while (true) {
                delay(5000L)
                val nextPage = (pagerState.currentPage + 1) % topTenAnimes.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        snapPosition = SnapPosition.Center,
        beyondViewportPageCount = 2,
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        val anime = topTenAnimes[it]
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            ElevatedCard(
                onClick = { onClicked(anime.id) },
                elevation = CardDefaults.elevatedCardElevation(16.dp),
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .width(300.dp)
                    .height(500.dp)
            ) {
                Box(
                    contentAlignment = Alignment.BottomStart,
                    modifier = Modifier.fillMaxSize()
                ) {
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
                    Text(
                        anime.title,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0, 0, 0, 0xBA))
                            .padding(horizontal = 12.dp)
                    )
                }
            }
        }
    }
}