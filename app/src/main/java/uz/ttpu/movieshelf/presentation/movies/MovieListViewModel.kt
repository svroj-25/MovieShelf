package uz.ttpu.movieshelf.presentation.movies

/*
========================================================================================
15-VAZIFA TAHLILI: CLEANFLOW VA DEPENDENCY RULE
========================================================================================

(1) Chaqiruvlar tartibi (Control Flow):
    MovieItem (UI yurak bosilishi)
        ↓
    MovieListViewModel.onFavoriteClick(movieId)
        ↓
    ToggleFavoriteUseCase.invoke(movieId)
        ↓
    MovieRepositoryImpl.toggleFavorite(movieId)
        ↓
    SharedPrefsMovieLocalDataSource.setFavorite(movieId, newState)

(2) Bog'liqlik o'qlari (Source Code Dependencies) va Teskari Yo'nalish:
    • Boshqaruv oqimi (Control Flow): Presentation -> Domain -> Data tomon harakatlanadi.
    • Manba-kod bog'liqliklari (Dependency Rule): Barcha o'qlar faqat ICHKARIGA, ya'ni DOMAIN qatlamiga qaraydi:
        - Presentation -> Domain (ViewModel use case'larga bog'liq)
        - Data -> Domain (MovieRepositoryImpl domenning MovieRepository interfeysini amalga oshiradi)
    • Qaysi o'q chaqiruvga teskari yo'nalgan?
      ToggleFavoriteUseCase -> MovieRepositoryImpl chaqiruvi. Chaqiruv Domain'dan Data'ga qarab ketadi,
      lekin kod bog'liqligi Data'dan Domain'ga qarab yo'nalgan (MovieRepositoryImpl -> MovieRepository).
      Bu "Dependency Inversion Principle" (DIP) orqali amalga oshirilgan: use case aniq klassga emas,
      o'zining domen interfeysiga suyanadi.

(3) Nima uchun Use Case Log.d'ni chaqira olmaydi va domendan loglashning toza usuli:
    • android.util.Log Android SDK framework'iga tegishli. Domain moduli esa sof Kotlin (JVM) moduli bo'lib,
      unda Android SDK kutubxonalari umuman mavjud emas va bo'lmasligi kerak (mustaqillik tamoyili).
    • Domendan toza log yozish usuli — yana Dependency Inversion'dan foydalanish:
      Domen modulida toza Kotlin `interface AppLogger { fun d(tag: String, msg: String) }` e'lon qilinadi.
      Tashqi presentation yoki data modulida esa ushbu interfeysni Android Log.d orqali amalga oshiruvchi
      `class AndroidAppLogger : AppLogger` klassi yoziladi va DI (AppContainer) orqali uzatiladi.
========================================================================================
*/

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.ttpu.movieshelf.domain.usecase.GetFavoriteMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.ToggleFavoriteUseCase

class MovieListViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieListUiState>(MovieListUiState.Loading)
    val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    private var isFavoritesOnly: Boolean = false

    init {
        loadMovies()
    }

    fun loadMovies() {
        _uiState.value = MovieListUiState.Loading
        viewModelScope.launch {
            try {
                val result = if (isFavoritesOnly) {
                    getFavoriteMoviesUseCase()
                } else {
                    getMoviesUseCase()
                }
                _uiState.value = MovieListUiState.Success(
                    movies = result.movies,
                    isFromCache = result.isFromCache,
                    favoritesOnly = isFavoritesOnly
                )
            } catch (e: Exception) {
                _uiState.value = MovieListUiState.Error(e.message ?: "Failed to load movies")
            }
        }
    }

    fun onFavoritesOnlyChange(enabled: Boolean) {
        isFavoritesOnly = enabled
        loadMovies()
    }

    fun onFavoriteClick(movieId: Int) {
        viewModelScope.launch {
            try {
                val newState = toggleFavoriteUseCase(movieId)
                val current = _uiState.value
                if (current is MovieListUiState.Success) {
                    if (isFavoritesOnly && !newState) {
                        val updated = current.movies.filter { it.id != movieId }
                        _uiState.value = current.copy(movies = updated)
                    } else {
                        val updated = current.movies.map {
                            if (it.id == movieId) it.copy(isFavorite = newState) else it
                        }
                        _uiState.value = current.copy(movies = updated)
                    }
                }
            } catch (e: Exception) {
                // Ignore or handle toggle error
            }
        }
    }
}
