package com.saras.aniverse.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.saras.aniverse.repository.AnimeImplRepository
import com.saras.network.Anime
import com.saras.network.AnimeData
import com.saras.network.AnimeHome
import com.saras.network.ApiOperation
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeScreenViewModel(
    private val animeImplRepository: AnimeImplRepository,
    private val exoPlayer: ExoPlayer
): ViewModel() {

    private val _uiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    val map = mutableMapOf<String, List<Anime>>()

    init {
        fetchAllAnimeData()
    }

    fun retry() {
        fetchAllAnimeData()
    }

    private fun fetchAllAnimeData() = viewModelScope.launch {
        _uiState.update { HomeScreenUiState.Loading }

        supervisorScope { // Ensures one failure doesn't cancel all requests
            val topAiring = async { fetchData("top-airing") { animeImplRepository.getTopAiring() } }
            val mostPopular = async { fetchData("most-popular") { animeImplRepository.getMostPopular() } }
            val latestCompleted = async { fetchData("latest-completed") { animeImplRepository.getLatestCompleted() } }
            val recentEpisodes = async { fetchData("recent-episodes") { animeImplRepository.getRecentEpisodes() } }
            val recentAdded = async { fetchData("recent-added") { animeImplRepository.getRecentAdded() } }
            val movies = async { fetchData("movies") { animeImplRepository.getMovies() } }

            // Wait for all requests to complete
            listOf(topAiring, mostPopular, latestCompleted, recentEpisodes, recentAdded, movies).awaitAll()

        }
        if (map.isNotEmpty()) {
            _uiState.update { HomeScreenUiState.Success(map) }
        }
    }

    private suspend fun fetchData(key: String, request: suspend () -> ApiOperation<AnimeData>) {
        when (val result = request()) {
            is ApiOperation.Success -> { // Handle success case
                map[key] = result.data.results
            }
            is ApiOperation.Failure -> { // Handle error case
                _uiState.update { HomeScreenUiState.Error("Failed to load: ${result.exception.message ?: "Unknown Error"}") }
            }
        }
    }

    // Release ExoPlayer when home screen viewmodel is cleared
    // This is done here because activity gets destroyed when rotation is occurred and viewmodel survives screen rotation
    // Only single instance of exoplayer is created
    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }
}

sealed interface HomeScreenUiState {
    object Loading: HomeScreenUiState
    data class Error(val message: String): HomeScreenUiState
    data class Success(val data: Map<String, List<Anime>>): HomeScreenUiState

}