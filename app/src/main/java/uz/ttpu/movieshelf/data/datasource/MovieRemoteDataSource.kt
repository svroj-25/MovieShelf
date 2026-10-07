package uz.ttpu.movieshelf.data.datasource

import uz.ttpu.movieshelf.data.model.MovieDto

interface MovieRemoteDataSource {
    suspend fun fetchMovies(): List<MovieDto>
}
