package com.saras.aniverse.ui.screens.player

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.C.SELECTION_FLAG_DEFAULT
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.SubtitleConfiguration.*
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.SHOW_BUFFERING_ALWAYS
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    episodeStreamingLink: String,
    viewModel: PlayerScreenViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val exoPlayer = viewModel.mExoPlayer
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Official ExoPlayer PlayerView
    val playerView by remember { mutableStateOf(PlayerView(context)) }

    // Activity and window variables to change Rotation and hide System Bars
    val activity = context as Activity
    val window = activity.window
    val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

    windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    // DisposableEffect to change Rotation and System Bars and change back when navigating back
    DisposableEffect(Unit) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        onDispose {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    // Getting Episode Streaming Link
    LaunchedEffect(Unit) { viewModel.getStreamingLink(episodeStreamingLink) }

    when (val state = uiState) {
        PlayerUiState.Loading -> {
            Box(Modifier.fillMaxSize().background(Color.Black)) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        is PlayerUiState.Success -> {

            // Episode Link and Subtitle Variables
            val link = state.data.sources
            val subtitles: MutableList<MediaItem.SubtitleConfiguration> = mutableListOf()

            // Adding multiple and if any subtitles from Api Call
            state.data.subtitles?.forEach { subtitle ->

                // Skipping Subtitle having thumbnails information
                if (subtitle.lang != "thumbnails") {

                    // Selecting English as Default Subtitle
                    when (subtitle.lang) {
                        "English" -> {
                            subtitles.add(
                                Builder(subtitle.url.toUri())
                                    .setMimeType(MimeTypes.TEXT_VTT)
                                    .setLanguage(subtitle.lang)
                                    .setSelectionFlags(SELECTION_FLAG_DEFAULT)
                                    .build()
                            )
                        }
                        else -> {
                            subtitles.add(
                                Builder(subtitle.url.toUri())
                                    .setMimeType(MimeTypes.TEXT_VTT)
                                    .setLanguage(subtitle.lang)
                                    .build()
                            )
                        }
                    }
                }
            }

            // Preparing and Setting MediaItem
            val mediaItem = viewModel.getMediaItem(link?.get(0)?.url.toString(), subtitles)
            viewModel.setMediaItem(mediaItem)

            // Showing Player using AndroidView as Player is View
            Box {
                AndroidView(factory = {
                    playerView.apply {
                        // Configuring player to keepScreen, showBuffering and showSubtitleButton
                        player = exoPlayer
                        keepScreenOn = true
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        controllerAutoShow = true
                        setShowBuffering(SHOW_BUFFERING_ALWAYS)
                        setShowSubtitleButton(true)
                        exoPlayer.addListener(object : Player.Listener {
                            override fun onPlayerError(error: PlaybackException) {
                                super.onPlayerError(error)
                                coroutineScope.launch { snackbarHostState.showSnackbar(error.message ?: "Unknown Error") }
                                exoPlayer.prepare()
                            }
                        })
                    }
                }, modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0, 0, 0)))
            }
        }

        is PlayerUiState.Error -> {
            LaunchedEffect(Unit) { snackbarHostState.showSnackbar(state.message) }
        }
    }
}