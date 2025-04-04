package com.saras.aniverse.ui.screens.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.saras.aniverse.imageRequest
import com.saras.aniverse.ui.components.EpisodeComp
import com.saras.aniverse.ui.components.ImageErrorComp
import com.saras.aniverse.ui.components.shimmerEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailScreenViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState,
    animeId: String, onEpisodeClicked: (String) -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Api Call to get AnimeInfo using animeId on Screen Navigation
    LaunchedEffect(Unit) { viewModel.getAnimeInfo(animeId) }

    when (val state = uiState) {
        // When Screen is Loading
        DetailScreenUiState.Loading -> {
            Column(modifier.fillMaxSize()) {
                Box(modifier = Modifier
                    .height(250.dp)
                    .fillMaxWidth()
                    .shimmerEffect()
                )
            }
        }
        is DetailScreenUiState.Success -> {
            val anime = state.data

            LazyColumn(modifier.fillMaxSize()) {
                item {
                    Box(
                        modifier = Modifier.requiredHeight(250.dp)
                    ) {

                        // Image Loading, Success and Error States
                        val painter = rememberAsyncImagePainter(imageRequest(context, anime.image))
                        val painterState by painter.state.collectAsState()

                        when (painterState) {
                            is AsyncImagePainter.State.Success -> {
                                Image(
                                    painter = painter,
                                    contentDescription = anime.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().shimmerEffect()
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

                    // Content about Anime Information, Title, Description and Episodes
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxSize().padding(8.dp)
                    ) {
                        // Title
                        Text(
                            text = anime.title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                        )

                        // Genres
                        LazyRow(modifier = Modifier.fillMaxWidth()) {
                            item {
                                anime.genres?.forEach {
                                    OutlinedCard(
                                        shape = RectangleShape,
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) { Text(it, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) }
                                }
                            }
                        }

                        // Description
                        Text(
                            text = anime.description ?: "",
                            fontSize = 16.sp,
                            color = Color.LightGray
                        )

                        // Episodes
                        Text(
                            text = "Episodes",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (anime.episodes?.isNotEmpty() == true) {
                                items(anime.episodes!!) { episode ->
                                    EpisodeComp(episode, anime.image, onEpisodeClicked)
                                }
                            }
                        }
                    }
                }
            }
        }

        is DetailScreenUiState.Error -> {
            LaunchedEffect(Unit) { snackbarHostState.showSnackbar(state.message) }
        }
    }
}