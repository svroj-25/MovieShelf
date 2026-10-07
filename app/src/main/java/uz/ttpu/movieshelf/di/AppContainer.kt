package uz.ttpu.movieshelf.di

import uz.ttpu.movieshelf.data.datasource.FakeMovieRemoteDataSource
import uz.ttpu.movieshelf.data.datasource.InMemoryMovieLocalDataSource
import uz.ttpu.movieshelf.data.datasource.MovieLocalDataSource
import uz.ttpu.movieshelf.data.repository.MovieRepositoryImpl
import uz.ttpu.movieshelf.domain.repository.MovieRepository
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.ToggleFavoriteUseCase

class AppContainer {
    val fakeRemote = FakeMovieRemoteDataSource()
    private val local: MovieLocalDataSource = InMemoryMovieLocalDataSource()

    val repository: MovieRepository = MovieRepositoryImpl(
        remote = fakeRemote,
        local = local
    )

    val getMoviesUseCase = GetMoviesUseCase(repository)
    val toggleFavoriteUseCase = ToggleFavoriteUseCase(repository)
}
