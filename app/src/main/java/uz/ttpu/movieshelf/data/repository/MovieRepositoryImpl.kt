package uz.ttpu.movieshelf.data.repository

import uz.ttpu.movieshelf.data.datasource.MovieLocalDataSource
import uz.ttpu.movieshelf.data.datasource.MovieRemoteDataSource
import uz.ttpu.movieshelf.data.mapper.toDomain
import uz.ttpu.movieshelf.domain.model.MoviesResult
import java.io.IOException

class MovieRepositoryImpl(
    private val remote: MovieRemoteDataSource,
    private val local: MovieLocalDataSource
) {
    suspend fun getMovies(): MoviesResult {
        return try {
            val dtos = remote.fetchMovies()
            local.saveMovies(dtos)
            val favorites = local.getFavorites()
            val movies = dtos.map { it.toDomain(isFavorite = it.id in favorites) }
            MoviesResult(movies = movies, isFromCache = false)
        } catch (e: IOException) {
            val cachedDtos = local.getCachedMovies()
            if (cachedDtos.isEmpty()) {
                throw e
            }
            val favorites = local.getFavorites()
            val movies = cachedDtos.map { it.toDomain(isFavorite = it.id in favorites) }
            MoviesResult(movies = movies, isFromCache = true)
        }
    }

    suspend fun toggleFavorite(movieId: Int): Boolean {
        val favorites = local.getFavorites()
        val newState = movieId !in favorites
        local.setFavorite(movieId, newState)
        return newState
    }
}
