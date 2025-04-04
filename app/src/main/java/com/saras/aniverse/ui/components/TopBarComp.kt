package com.saras.aniverse.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.saras.aniverse.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarComp(navController: NavController, onSearchButtonClicked: () -> Unit) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val topBarState = rememberSaveable { mutableStateOf(true) }

    // Checking if Current Screen is player to hide TopBar
    when (navBackStackEntry?.destination?.route?.contains("player")) {
        true -> topBarState.value = false
        else -> topBarState.value = true
    }

    AnimatedVisibility(visible = topBarState.value) {
        TopAppBar(
            title = {
                // Checking current destination to show App Name on home screen
                if (navBackStackEntry?.destination?.route == "home") {
                    Text(stringResource(R.string.app_name))
                }
            },
            actions = {
                // Current Destination check to show Search Button
                when (navBackStackEntry?.destination?.route) {
                    "search" -> {}
                    else -> Row(horizontalArrangement = Arrangement.End) {
                        IconButton( onClick = onSearchButtonClicked) {
                            Icon(Icons.Default.Search, "Search Button")
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(255, 0, 0, 0x2F)),
            navigationIcon = {
                when(navBackStackEntry?.destination?.route != "home") {
                    true -> {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back")
                        }
                    }
                    false -> {}
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}