package uz.ttpu.movieshelf.domain

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.domain.model.MoviesResult
import uz.ttpu.movieshelf.domain.repository.MovieRepository
import uz.ttpu.movieshelf.domain.usecase.GetFavoriteMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase

class FakeRepoForFavorites(
    private val result: MoviesResult
) : MovieRepository {
    override suspend fun getMovies(): MoviesResult = result
    override suspend fun toggleFavorite(movieId: Int): Boolean = true
}

class GetFavoriteMoviesUseCaseTest {

    @Test
    fun returns_only_favorite_movies() = runBlocking<Unit> {
        val movies = listOf(
            Movie(1, "Movie A", 2020, 8.0, isFavorite = false),
            Movie(2, "Movie B", 2021, 9.0, isFavorite = true),
            Movie(3, "Movie C", 2022, 7.5, isFavorite = true)
        )
        val fakeRepo = FakeRepoForFavorites(MoviesResult(movies, isFromCache = false))
        val getMoviesUseCase = GetMoviesUseCase(fakeRepo)
        val favoriteUseCase = GetFavoriteMoviesUseCase(getMoviesUseCase)

        val result = favoriteUseCase()

        assertEquals(2, result.movies.size)
        assertTrue(result.movies.all { it.isFavorite })
    }
}
