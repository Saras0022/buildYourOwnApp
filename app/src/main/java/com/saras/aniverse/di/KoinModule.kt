package com.saras.aniverse.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.saras.aniverse.repository.AnimeImplRepository
import com.saras.network.KtorClient
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.saras.aniverse")
class KoinModule {

    // Singleton KtorClient
    @Single
    fun provideKtorClient(): KtorClient = KtorClient()

    @Single
    fun provideAnimeImplRepository(client: KtorClient): AnimeImplRepository = AnimeImplRepository(client)

    // Singleton ExoPlayer
    @OptIn(UnstableApi::class)
    @Single
    fun provideExoPlayer(context: Context): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setSeekBackIncrementMs(10000L)
            .setSeekForwardIncrementMs(10000L)
            .build()
            .also {
                // Listener to listen for Player Error
                // Retrying Again on Player Error
                it.addListener(
                    object : Player.Listener {
                        override fun onPlayerError(error: PlaybackException) {
                            super.onPlayerError(error)
                            it.prepare()
                        }
                    }
                )
            }
    }
}