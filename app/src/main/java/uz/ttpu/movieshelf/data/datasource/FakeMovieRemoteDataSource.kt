package uz.ttpu.movieshelf.data.datasource

import kotlinx.coroutines.delay
import uz.ttpu.movieshelf.data.model.MovieDto
import java.io.IOException

class FakeMovieRemoteDataSource(
    var shouldFail: Boolean = false
) : MovieRemoteDataSource {

    private val sampleMovies = listOf(
        MovieDto(1, "The Shawshank Redemption", 1994, 9.3),
        MovieDto(2, "The Godfather", 1972, 9.2),
        MovieDto(3, "The Dark Knight", 2008, 9.0),
        MovieDto(4, "Pulp Fiction", 1994, 8.9),
        MovieDto(5, "Inception", 2010, 8.8),
        MovieDto(6, "Fight Club", 1999, 8.8),
        MovieDto(7, "Interstellar", 2014, 8.7),
        MovieDto(8, "The Matrix", 1999, 8.7)
    )

    override suspend fun fetchMovies(): List<MovieDto> {
        delay(800)
        if (shouldFail) {
            throw IOException("Network error: offline mode")
        }
        return sampleMovies
    }
}
