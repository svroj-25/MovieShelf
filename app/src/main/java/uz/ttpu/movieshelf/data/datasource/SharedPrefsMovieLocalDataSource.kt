package uz.ttpu.movieshelf.data.datasource

import android.content.Context
import uz.ttpu.movieshelf.data.model.MovieDto

class SharedPrefsMovieLocalDataSource(context: Context) : MovieLocalDataSource {

    private val prefs = context.getSharedPreferences("movieshelf", Context.MODE_PRIVATE)
    private val memoryCache = mutableListOf<MovieDto>()

    override suspend fun getFavorites(): Set<Int> {
        val stringSet = prefs.getStringSet("favorites", emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    override suspend fun setFavorite(id: Int, isFavorite: Boolean) {
        val currentSet = prefs.getStringSet("favorites", emptySet())?.toMutableSet() ?: mutableSetOf()
        if (isFavorite) {
            currentSet.add(id.toString())
        } else {
            currentSet.remove(id.toString())
        }
        prefs.edit().putStringSet("favorites", currentSet).apply()
    }

    override suspend fun getCachedMovies(): List<MovieDto> {
        return memoryCache.toList()
    }

    override suspend fun saveMovies(movies: List<MovieDto>) {
        memoryCache.clear()
        memoryCache.addAll(movies)
    }
}
