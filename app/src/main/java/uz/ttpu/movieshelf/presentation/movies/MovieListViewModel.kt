package uz.ttpu.movieshelf.presentation.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.ToggleFavoriteUseCase

class MovieListViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieListUiState>(MovieListUiState.Loading)
    val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies() {
        _uiState.value = MovieListUiState.Loading
        viewModelScope.launch {
            try {
                val result = getMoviesUseCase()
                _uiState.value = MovieListUiState.Success(
                    movies = result.movies,
                    isFromCache = result.isFromCache
                )
            } catch (e: Exception) {
                _uiState.value = MovieListUiState.Error(e.message ?: "Failed to load movies")
            }
        }
    }

    fun onFavoriteClick(movieId: Int) {
        viewModelScope.launch {
            try {
                val newState = toggleFavoriteUseCase(movieId)
                val current = _uiState.value
                if (current is MovieListUiState.Success) {
                    val updated = current.movies.map {
                        if (it.id == movieId) it.copy(isFavorite = newState) else it
                    }
                    _uiState.value = current.copy(movies = updated)
                }
            } catch (e: Exception) {
                // Ignore or handle toggle error
            }
        }
    }
}
