package uz.ttpu.movieshelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import uz.ttpu.movieshelf.presentation.movies.MovieListScreen
import uz.ttpu.movieshelf.presentation.movies.MovieListViewModel
import uz.ttpu.movieshelf.presentation.movies.MovieListViewModelFactory
import uz.ttpu.movieshelf.ui.theme.MovieShelfTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as MovieShelfApp
        val container = app.container

        setContent {
            MovieShelfTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val factory = remember {
                        MovieListViewModelFactory(
                            container.getMoviesUseCase,
                            container.getFavoriteMoviesUseCase,
                            container.toggleFavoriteUseCase
                        )
                    }
                    val viewModel: MovieListViewModel = viewModel(factory = factory)
                    val state by viewModel.uiState.collectAsStateWithLifecycle()

                    var simulateFail by remember { mutableStateOf(container.fakeRemote.shouldFail) }

                    Box(modifier = Modifier.fillMaxSize()) {
                        MovieListScreen(
                            state = state,
                            onFavoriteClick = viewModel::onFavoriteClick,
                            onRefresh = viewModel::loadMovies,
                            onFavoritesOnlyChange = viewModel::onFavoritesOnlyChange
                        )

                        // Debug switch for fake network failure
                        FloatingActionButton(
                            onClick = {
                                simulateFail = !simulateFail
                                container.fakeRemote.shouldFail = simulateFail
                                viewModel.loadMovies()
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        ) {
                            Text(text = if (simulateFail) "Network: OFF" else "Network: ON")
                        }
                    }
                }
            }
        }
    }
}
