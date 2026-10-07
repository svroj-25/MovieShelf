package uz.ttpu.movieshelf.di

import android.content.Context
import uz.ttpu.movieshelf.data.datasource.FakeMovieRemoteDataSource
import uz.ttpu.movieshelf.data.datasource.MovieLocalDataSource
import uz.ttpu.movieshelf.data.datasource.SharedPrefsMovieLocalDataSource
import uz.ttpu.movieshelf.data.repository.MovieRepositoryImpl
import uz.ttpu.movieshelf.domain.repository.MovieRepository
import uz.ttpu.movieshelf.domain.usecase.GetFavoriteMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.ToggleFavoriteUseCase

class AppContainer(context: Context) {
    val fakeRemote = FakeMovieRemoteDataSource()
    private val local: MovieLocalDataSource = SharedPrefsMovieLocalDataSource(context)

    val repository: MovieRepository = MovieRepositoryImpl(
        remote = fakeRemote,
        local = local
    )

    val getMoviesUseCase = GetMoviesUseCase(repository)
    val getFavoriteMoviesUseCase = GetFavoriteMoviesUseCase(getMoviesUseCase)
    val toggleFavoriteUseCase = ToggleFavoriteUseCase(repository)
}
