package com.saras.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class KtorClient {

    val httpsProtocol = URLProtocol.HTTPS
    val animeHost = "consumet-api-lovat-zeta.vercel.app/anime/zoro"

    // Dispatcher.IO for network calling
    private val defaultDispatcher = Dispatchers.IO

    // Ktor Client using CIO as Engine
    private val client = HttpClient(OkHttp) {

        install(Logging)

        // ContentNegotiation for Serializing and Deserializing Json from Api Result
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
                explicitNulls = false
            })
        }

        // Timeout for Request, Socket, Connect
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            socketTimeoutMillis = 30000
            connectTimeoutMillis = 2000
        }
    }

    // In memory Cache using map
    private val animeCache = mutableMapOf<String, AnimeData>()
    private val animeInfoCache = mutableMapOf<String, AnimeInfo>()

    // Api Call to getTopAiring
    suspend fun getTopAiring(): ApiOperation<AnimeData> {
        animeCache["top-airing"]?.let { return ApiOperation.Success(it) }
        return safeApiCall {
            withContext(defaultDispatcher) {
                client.get {
                    url {
                        protocol = httpsProtocol
                        host = animeHost
                        path("top-airing")
                    }
                }
                    .body<AnimeData>()
                    .also { animeCache["top-airing"] = it }
            }
        }
    }

    // Api Call to getMostPopular
    suspend fun getMostPopular(): ApiOperation<AnimeData> {
        animeCache["most-popular"]?.let { return ApiOperation.Success(it) }
        return safeApiCall {
            withContext(defaultDispatcher) {
                client.get {
                    url {
                        protocol = httpsProtocol
                        host = animeHost
                        path("most-popular")
                    }
                }
                    .body<AnimeData>()
                    .also { animeCache["most-popular"] = it }
            }
        }
    }

    // Api Call to getRecentAdded
    suspend fun getRecentAdded(): ApiOperation<AnimeData> {
        animeCache["recent-added"]?.let { return ApiOperation.Success(it) }
        return safeApiCall {
            withContext(defaultDispatcher) {
                client.get {
                    url {
                        protocol = httpsProtocol
                        host = animeHost
                        path("recent-added")
                    }
                }
                    .body<AnimeData>()
                    .also { animeCache["recent-added"] = it }
            }
        }
    }

    // Api Call to getRecentEpisodes
    suspend fun getRecentEpisodes(): ApiOperation<AnimeData> {
        animeCache["recent-episodes"]?.let { return ApiOperation.Success(it) }
        return safeApiCall {
            withContext(defaultDispatcher) {
                client.get {
                    url {
                        protocol = httpsProtocol
                        host = animeHost
                        path("recent-episodes")
                    }
                }
                    .body<AnimeData>()
                    .also { animeCache["recent-episodes"] = it }
            }
        }
    }

    // Api Call to getMovies
    suspend fun getMovies(): ApiOperation<AnimeData> {
        animeCache["movies"]?.let { return ApiOperation.Success(it) }
        return safeApiCall {
            withContext(defaultDispatcher) {
                client.get {
                    url {
                        protocol = httpsProtocol
                        host = animeHost
                        path("movies")
                    }
                }
                    .body<AnimeData>()
                    .also { animeCache["movies"] = it }
            }
        }
    }

    // Api Call to getLatestCompleted
    suspend fun getLatestCompleted(): ApiOperation<AnimeData> {
        animeCache["latest-completed"]?.let { return ApiOperation.Success(it) }
        return safeApiCall {
            withContext(defaultDispatcher) {
                client.get {
                    url {
                        protocol = httpsProtocol
                        host = animeHost
                        path("latest-completed")
                    }
                }
                    .body<AnimeData>()
                    .also { animeCache["latest-completed"] = it }
            }
        }
    }

    // Api Call to getAnimeInfo
    suspend fun getAnimeInfo(animeId: String): ApiOperation<AnimeInfo> {
        animeInfoCache[animeId]?.let { return ApiOperation.Success(it) }
        return safeApiCall {
            withContext(defaultDispatcher) {
                client.get {
                    url {
                        protocol = httpsProtocol
                        host = animeHost
                        path("info")
                        parameter("id", animeId)
                    }
                }
                    .body<AnimeInfo>()
                    .also { animeInfoCache[animeId] = it }
            }
        }
    }

    // Api Call to getEpisodeData
    suspend fun getEpisodeData(episodeId: String, dub: Boolean = false): ApiOperation<EpisodeData> {
        return safeApiCall {
            withContext(defaultDispatcher) {
                client.get {
                    url {
                        protocol = httpsProtocol
                        host = animeHost
                        path("watch", episodeId)
                        parameters.append("dub", dub.toString())
                    }
                }.body<EpisodeData>()
            }
        }
    }

    // Api Call to searchAnime
    // Default search result being topAiring
    suspend fun getSearchResult(query: String): ApiOperation<AnimeData> {
        if (query.isBlank()) animeCache["top-airing"]?.let { return ApiOperation.Success(it) }
        return safeApiCall {
            withContext(defaultDispatcher) {
                client.get {
                    url {
                        protocol = httpsProtocol
                        host = animeHost
                        path(query)
                    }
                }.body<AnimeData>()
            }
        }
    }

    private inline fun <T> safeApiCall(apiCall: () -> T): ApiOperation<T> {
        return try {
            ApiOperation.Success(apiCall())
        } catch (e: Exception) {
            ApiOperation.Failure(e)
        }
    }
}

sealed interface ApiOperation<T> {
    data class Success<T>(val data: T): ApiOperation<T>
    data class Failure<T>(val exception: Exception): ApiOperation<T>

    fun onSuccess(block: (T) -> Unit): ApiOperation<T> {
        if (this is Success) block(data)
        return this
    }

    fun onFailure(block: (Exception) -> Unit): ApiOperation<T> {
        if (this is Failure) block(exception)
        return this
    }
}

@Serializable
data class AnimeData(
    val results: List<Anime>
)

@Serializable
data class AnimeHome(
    val topAiring: AnimeData,
    val mostPopular: AnimeData,
    val recentAdded: AnimeData,
    val recentEpisodes: AnimeData,
    val movies: AnimeData,
    val latestCompleted: AnimeData
)

@Serializable
data class Anime(
    val id: String,
    val title: String,
    val image: String,
    val duration: String? = null,
    val type: String? = null
)

@Serializable
data class AnimeInfo(
    val id: String,
    val title: String,
    val image: String,
    val description: String?,
    val genres: List<String>?,
    val episodes: List<AnimeEpisode>?,
    val recommendations: List<Anime>?,
    val relatedAnime: List<Anime>?
)

@Serializable
data class AnimeEpisode(
    @SerialName("id") val episodeId: String,
    val number: Int,
    val title: String?,
    val isFiller: Boolean
)

@Serializable
data class EpisodeData(
    val sources: List<EpisodeLink>?,
    val subtitles: List<Subtitle>?
)

@Serializable
data class EpisodeLink(
    val url: String
)

@Serializable
data class Subtitle(
    val url: String,
    val lang: String?
)