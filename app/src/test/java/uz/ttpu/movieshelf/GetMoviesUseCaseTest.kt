package uz.ttpu.movieshelf

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.domain.model.MoviesResult
import uz.ttpu.movieshelf.domain.repository.MovieRepository
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase

class FakeMovieRepository(
    private val result: MoviesResult
) : MovieRepository {
    override suspend fun getMovies(): MoviesResult = result
    override suspend fun toggleFavorite(movieId: Int): Boolean = true
}

class GetMoviesUseCaseTest {

    @Test
    fun movies_are_sorted_by_rating_desc_then_title() = runBlocking<Unit> {
        // Arrange: B (8.0), A (8.0), C (9.1)
        val movies = listOf(
            Movie(1, "B", 2020, 8.0),
            Movie(2, "A", 2020, 8.0),
            Movie(3, "C", 2021, 9.1)
        )
        val fakeRepo = FakeMovieRepository(MoviesResult(movies, isFromCache = false))
        val useCase = GetMoviesUseCase(fakeRepo)

        // Act
        val result = useCase()

        // Assert: the titles come out as C, A, B
        val titles = result.movies.map { it.title }
        assertEquals(listOf("C", "A", "B"), titles)
    }

    @Test
    fun is_from_cache_flag_is_passed_through() = runBlocking<Unit> {
        val fakeRepo = FakeMovieRepository(MoviesResult(emptyList(), isFromCache = true))
        val useCase = GetMoviesUseCase(fakeRepo)

        val result = useCase()
        assertTrue(result.isFromCache)
    }
}
