package com.saras.aniverse.ui.screens.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.saras.aniverse.ui.components.SearchCardComp
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchScreenViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState,
    onAnimeClicked: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize().padding(bottom = 16.dp, start = 8.dp, end = 8.dp)
    ) {
        val query = viewModel.query

        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.changeQuery(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search Anime") },
            leadingIcon = { Icon(Icons.Default.Search, "Search") },
            keyboardActions = KeyboardActions { viewModel.getSearchResult() },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search)
        )

        Spacer(Modifier.height(24.dp))

        // If uiState is Success show content else show Loading Screen
        when (val state = uiState) {
            is SearchScreenUiState.Success -> {
                val searchResult = state.data.results

                LazyColumn {
                    items(searchResult) { anime ->
                        SearchCardComp(anime, onAnimeClicked)
                    }
                }
            }
            SearchScreenUiState.Loading -> {
                Box(Modifier.fillMaxSize()) { CircularProgressIndicator(modifier = Modifier.align(Alignment.Center)) }
            }

            is SearchScreenUiState.Error -> {
                LaunchedEffect(Unit) { snackbarHostState.showSnackbar(state.message) }
            }
        }
    }
}