package com.saras.aniverse.ui.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saras.aniverse.repository.AnimeImplRepository
import com.saras.network.AnimeData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SearchScreenViewModel(
    private val animeImplRepository: AnimeImplRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<SearchScreenUiState>(SearchScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    var query by mutableStateOf("")
        private set

    init {
        getSearchResult()
    }

    fun changeQuery(it: String) { query = it }

    fun getSearchResult() = viewModelScope.launch {
        _uiState.update { SearchScreenUiState.Loading }
        animeImplRepository.getSearchResult(query).onSuccess { animeData ->
            _uiState.update { SearchScreenUiState.Success(animeData) }
        }.onFailure { exception ->
            _uiState.update { SearchScreenUiState.Error(exception.message ?: "Unknown error occurred") }
        }
    }
}

sealed interface SearchScreenUiState {
    data class Success(val data: AnimeData): SearchScreenUiState
    data class Error(val message: String): SearchScreenUiState
    object Loading: SearchScreenUiState
}