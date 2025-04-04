package com.saras.aniverse.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saras.aniverse.repository.AnimeImplRepository
import com.saras.network.AnimeInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class DetailScreenViewModel(
    private val animeImplRepository: AnimeImplRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<DetailScreenUiState>(DetailScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun getAnimeInfo(animeId: String) = viewModelScope.launch {
        _uiState.update { DetailScreenUiState.Loading }
        animeImplRepository.getAnimeInfo(animeId).onSuccess { animeInfo ->
            _uiState.update { DetailScreenUiState.Success(animeInfo) }
        }.onFailure { exception ->
            _uiState.update { DetailScreenUiState.Error(exception.message ?: "Unknown error occurred") }
        }
    }
}

sealed interface DetailScreenUiState {
    data class Success(val data: AnimeInfo): DetailScreenUiState
    data class Error(val message: String): DetailScreenUiState
    object Loading: DetailScreenUiState
}