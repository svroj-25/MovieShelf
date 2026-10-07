package uz.ttpu.movieshelf.domain.usecase

import uz.ttpu.movieshelf.data.repository.MovieRepositoryImpl
import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.domain.model.MoviesResult

/**
 * Arxitektura nuqsoni izohi:
 * Ushbu GetMoviesUseCase klassi domen qatlamida joylashgan bo'lsa-da,
 * u to'g'ridan-to'g'ri data qatlamiga tegishli bo'lgan MovieRepositoryImpl aniq klassiga bog'langan.
 * Bu Clean Architecture'ning "Dependency Rule" qoidasini buzadi (ichki domen qatlami tashqi ma'lumotlar qatlamini bilmasligi kerak).
 */
class GetMoviesUseCase(
    private val repository: MovieRepositoryImpl
) {
    suspend operator fun invoke(): MoviesResult {
        val result = repository.getMovies()
        val sorted = result.movies.sortedWith(
            compareByDescending<Movie> { it.rating }.thenBy { it.title }
        )
        return result.copy(movies = sorted)
    }
}
