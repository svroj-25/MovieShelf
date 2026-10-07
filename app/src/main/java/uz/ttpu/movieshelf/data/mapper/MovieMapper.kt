package uz.ttpu.movieshelf.data.mapper

import uz.ttpu.movieshelf.data.model.MovieDto
import uz.ttpu.movieshelf.domain.model.Movie

fun MovieDto.toDomain(isFavorite: Boolean = false): Movie = Movie(
    id = id,
    title = title,
    year = year,
    rating = rating,
    isFavorite = isFavorite
)

fun Movie.toDto(): MovieDto = MovieDto(
    id = id,
    title = title,
    year = year,
    rating = rating
)
