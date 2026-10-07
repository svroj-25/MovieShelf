package uz.ttpu.movieshelf.data.datasource

import uz.ttpu.movieshelf.data.model.MovieDto

interface MovieLocalDataSource {
    suspend fun getFavorites(): Set<Int>
    suspend fun setFavorite(id: Int, isFavorite: Boolean)
    suspend fun getCachedMovies(): List<MovieDto>
    suspend fun saveMovies(movies: List<MovieDto>)
}
