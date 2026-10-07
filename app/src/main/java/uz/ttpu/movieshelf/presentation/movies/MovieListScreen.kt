package uz.ttpu.movieshelf.presentation.movies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.ui.theme.MovieShelfTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    state: MovieListUiState,
    onFavoriteClick: (Int) -> Unit,
    onRefresh: () -> Unit,
    onFavoritesOnlyChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (state) {
                is MovieListUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is MovieListUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onRefresh) {
                            Text("Retry")
                        }
                    }
                }
                is MovieListUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        if (state.isFromCache) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "Offline Mode: Showing cached data",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            FilterChip(
                                selected = state.favoritesOnly,
                                onClick = { onFavoritesOnlyChange(!state.favoritesOnly) },
                                label = { Text("Favorites only") }
                            )
                        }
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.movies, key = { it.id }) { movie ->
                                MovieItem(
                                    movie = movie,
                                    onFavoriteClick = { onFavoriteClick(movie.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingPreview() {
    MovieShelfTheme {
        MovieListScreen(state = MovieListUiState.Loading, onFavoriteClick = {}, onRefresh = {})
    }
}

@Preview(showBackground = true)
@Composable
fun SuccessFreshPreview() {
    MovieShelfTheme {
        MovieListScreen(
            state = MovieListUiState.Success(
                movies = listOf(
                    Movie(1, "The Shawshank Redemption", 1994, 9.3, true),
                    Movie(2, "The Godfather", 1972, 9.2, false)
                ),
                isFromCache = false,
                favoritesOnly = false
            ),
            onFavoriteClick = {},
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SuccessCachedPreview() {
    MovieShelfTheme {
        MovieListScreen(
            state = MovieListUiState.Success(
                movies = listOf(
                    Movie(1, "The Shawshank Redemption", 1994, 9.3, false)
                ),
                isFromCache = true,
                favoritesOnly = false
            ),
            onFavoriteClick = {},
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorPreview() {
    MovieShelfTheme {
        MovieListScreen(
            state = MovieListUiState.Error("Network error: offline mode"),
            onFavoriteClick = {},
            onRefresh = {}
        )
    }
}
