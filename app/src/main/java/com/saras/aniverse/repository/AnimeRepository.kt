package com.saras.aniverse.repository

import com.saras.network.AnimeData
import com.saras.network.AnimeInfo
import com.saras.network.ApiOperation
import com.saras.network.EpisodeData
import com.saras.network.KtorClient

// Interface of Repository
interface AnimeRepository {
    suspend fun getTopAiring(): ApiOperation<AnimeData>
    suspend fun getMostPopular(): ApiOperation<AnimeData>
    suspend fun getRecentAdded(): ApiOperation<AnimeData>
    suspend fun getRecentEpisodes(): ApiOperation<AnimeData>
    suspend fun getMovies(): ApiOperation<AnimeData>
    suspend fun getLatestCompleted(): ApiOperation<AnimeData>
    suspend fun getAnimeInfo(animeId: String): ApiOperation<AnimeInfo>
    suspend fun getEpisodeData(episodeId: String): ApiOperation<EpisodeData>
    suspend fun getSearchResult(query: String): ApiOperation<AnimeData>
}

// Implementation of Repository
class AnimeImplRepository(
    private val client: KtorClient
): AnimeRepository {
    override suspend fun getTopAiring(): ApiOperation<AnimeData> = client.getTopAiring()

    override suspend fun getMostPopular(): ApiOperation<AnimeData> = client.getMostPopular()

    override suspend fun getRecentAdded(): ApiOperation<AnimeData> = client.getRecentAdded()

    override suspend fun getRecentEpisodes(): ApiOperation<AnimeData> = client.getRecentEpisodes()

    override suspend fun getMovies(): ApiOperation<AnimeData> = client.getMovies()

    override suspend fun getLatestCompleted(): ApiOperation<AnimeData> = client.getLatestCompleted()

    override suspend fun getAnimeInfo(animeId: String): ApiOperation<AnimeInfo> = client.getAnimeInfo(animeId)

    override suspend fun getEpisodeData(episodeId: String): ApiOperation<EpisodeData> = client.getEpisodeData(episodeId)

    override suspend fun getSearchResult(query: String): ApiOperation<AnimeData> = client.getSearchResult(query)
}