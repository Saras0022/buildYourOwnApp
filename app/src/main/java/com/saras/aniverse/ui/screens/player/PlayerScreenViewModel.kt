package com.saras.aniverse.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.saras.aniverse.repository.AnimeImplRepository
import com.saras.network.EpisodeData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PlayerScreenViewModel(
    private val animeImplRepository: AnimeImplRepository,
    private val exoPlayer: ExoPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Loading)
    val uiState = _uiState.asStateFlow()
    val mExoPlayer = exoPlayer

    fun getStreamingLink(episodeId: String) = viewModelScope.launch {
        _uiState.update { PlayerUiState.Loading }
        animeImplRepository.getEpisodeData(episodeId).onSuccess { episodeData ->
            _uiState.update { PlayerUiState.Success(episodeData) }
        }.onFailure { exception ->
            _uiState.update { PlayerUiState.Error(exception.message ?: "Unknown error occurred") }
        }
    }

    // Build MediaItem using streamingLink and subtitles
    fun getMediaItem(streamingLink: String, subtitles: List<MediaItem.SubtitleConfiguration>): MediaItem {
        return MediaItem.Builder()
            .setUri(streamingLink)
            .setSubtitleConfigurations(subtitles)
            .build()
    }

    // SetMediaItem and play it when ready
    fun setMediaItem(mediaItem: MediaItem) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    // Stop the player when player screen viewmodel gets cleared
    // ClearMediaItems and prepare it for next MediaItems
    override fun onCleared() {
        super.onCleared()
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
    }
}

sealed interface PlayerUiState {
    data class Success(val data: EpisodeData): PlayerUiState
    data class Error(val message: String): PlayerUiState
    object Loading: PlayerUiState
}