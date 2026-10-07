package uz.ttpu.movieshelf.data.datasource

import uz.ttpu.movieshelf.data.model.MovieDto

class InMemoryMovieLocalDataSource : MovieLocalDataSource {
    private val favorites = mutableSetOf<Int>()
    private val cache = mutableListOf<MovieDto>()

    override suspend fun getFavorites(): Set<Int> = favorites.toSet()

    override suspend fun setFavorite(id: Int, isFavorite: Boolean) {
        if (isFavorite) {
            favorites.add(id)
        } else {
            favorites.remove(id)
        }
    }

    override suspend fun getCachedMovies(): List<MovieDto> = cache.toList()

    override suspend fun saveMovies(movies: List<MovieDto>) {
        cache.clear()
        cache.addAll(movies)
    }
}
