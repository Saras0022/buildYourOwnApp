package com.saras.aniverse.ui.screens

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.saras.aniverse.ui.components.TopBarComp
import com.saras.aniverse.ui.screens.detail.DetailScreen
import com.saras.aniverse.ui.screens.home.HomeScreen
import com.saras.aniverse.ui.screens.player.PlayerScreen
import com.saras.aniverse.ui.screens.search.SearchScreen

@Composable
fun NavScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()

    Scaffold(
        topBar = { TopBarComp(navController = navController) { navController.navigate("search") } },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(modifier = Modifier.padding(innerPadding).background(Color.Black), startDestination = "home", navController = navController) {
            composable(route = "home") {
                HomeScreen(snackbarHostState = snackbarHostState) { animeId ->
                    navController.navigate("detail/$animeId")
                }
            }

            // Getting animeId argument in Navigation
            composable(route = "detail/{animeId}") { backStackEntry ->
                val animeId = backStackEntry.arguments?.getString("animeId") ?: ""

                DetailScreen(
                    snackbarHostState = snackbarHostState,
                    animeId = animeId
                ) { episodeId ->
                    navController.navigate("player/$animeId/$episodeId")
                }
            }

            // Getting animeId and episodeId in Navigation
            composable(
                route = "player/{animeId}/{episodeId}",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }) { backStackEntry ->

                val episodeId = backStackEntry.arguments?.getString("episodeId") ?: ""
                val animeId = backStackEntry.arguments?.getString("animeId") ?: ""
                val episode = "$animeId?$episodeId"

                PlayerScreen(episodeStreamingLink = episode, snackbarHostState = snackbarHostState)
            }
            composable(route = "search") {
                SearchScreen(snackbarHostState = snackbarHostState) { animeId ->
                    navController.navigate("detail/$animeId")
                }
            }
        }
    }
}