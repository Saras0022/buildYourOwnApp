package com.saras.aniverse.ui.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.saras.aniverse.ui.components.AnimeComp
import com.saras.aniverse.ui.components.TopAiringComp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState,
    onAnimeClicked: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        HomeScreenUiState.Loading -> {
            HomeScreenLoading()
        }
        is HomeScreenUiState.Success -> {

            val topAiring = state.data["top-airing"] ?: mutableListOf()
            val mostPopular = state.data["most-popular"] ?: mutableListOf()
            val recentAdded = state.data["recent-added"] ?: mutableListOf()
            val latestCompleted = state.data["latest-completed"] ?: mutableListOf()
            val movies = state.data["movies"] ?: mutableListOf()
            val recentEpisodes = state.data["recent-episodes"] ?: mutableListOf()


            LazyColumn(
                modifier = modifier.fillMaxSize(),
            ) {
                item {
                    TopAiringComp(topAiring, onAnimeClicked)

                    AnimeComp(type = "Most Popular", mostPopular, onAnimeClicked)

                    AnimeComp(type = "Latest Episodes", recentEpisodes, onAnimeClicked)

                    AnimeComp(type = "Latest Completed", latestCompleted, onAnimeClicked)

                    AnimeComp(type = "Recent Added", recentAdded, onAnimeClicked)

                    AnimeComp(type = "Movies", movies, onAnimeClicked)
                }
            }
        }

        is HomeScreenUiState.Error -> {
            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar(state.message, duration = SnackbarDuration.Indefinite, actionLabel = "Retry"
                ).run {
                    when (this) {
                        SnackbarResult.Dismissed -> {  }
                        SnackbarResult.ActionPerformed -> {
                            viewModel.retry()
                        }
                    }
                }
            }
        }
    }
}