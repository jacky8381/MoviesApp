package com.example.moviesapp.moviesList.data.mappers

import com.example.moviesapp.moviesList.data.local.TvEntity
import com.example.moviesapp.moviesList.data.local.movie.FavoriteEntity
import com.example.moviesapp.moviesList.data.local.movie.GenreEntity
import com.example.moviesapp.moviesList.data.local.movie.MovieEntity
import com.example.moviesapp.moviesList.data.local.movie.SearchEntity
import com.example.moviesapp.moviesList.data.remote.respond.Genre
import com.example.moviesapp.moviesList.data.remote.respond.GenreDto
import com.example.moviesapp.moviesList.data.remote.respond.MovieDto
import com.example.moviesapp.moviesList.data.remote.respond.TvDto
import com.example.moviesapp.moviesList.domain.model.GenreModel
import com.example.moviesapp.moviesList.domain.model.Movie

fun MovieDto.toMovieEntity(
    category: String
): MovieEntity {
    return MovieEntity(
        adult = adult ?: false,
        backdrop_path = backdrop_path ?: "",
        original_language = original_language ?: "",
        overview = overview ?: "",
        poster_path = poster_path ?: "",
        release_date = release_date ?: "",
        title = title ?: "",
        vote_average = vote_average ?: 0.0,
        popularity = popularity ?: 0.0,
        vote_count = vote_count ?: 0,
        id = id ?: -1,
        original_title = original_title ?: "",
        video = video ?: false,

        category = category,

        genre_ids = try {
            genre_ids?.joinToString(",") ?: "-1,-2"
        } catch (e: Exception) {
            "-1,-2"
        },
        isFavorite = false
    )
}

fun TvDto.toTvEntity(
    category: String
) : TvEntity {
    return TvEntity(
        adult= adult ?: false,
       backdrop_path= backdrop_path ?: "",
    first_air_date = first_air_date ?: "",
    genre_ids= try {
        genre_ids?.joinToString(",") ?: "-1,-2"
    } catch (e: Exception) {
        "-1,-2"
    },
    id = id ?: -1,
    name = name ?: "",
    origin_country = origin_country?.toString() ?: "",
    original_language = original_language ?: "",
    original_name = original_name ?: "",
    overview = overview ?: "",
    popularity = popularity ?: 0.0,
    poster_path = poster_path ?: "",
    vote_average = vote_average ?: 0.0,
    vote_count = vote_count ?: 0,
    category = category,
    isFavorite = false
    )
}

fun Movie.toSearchMovieEntity(
): SearchEntity {
    return SearchEntity(
        adult = adult ?: false,
        backdrop_path = backdrop_path ?: "",
        original_language = original_language ?: "",
        overview = overview ?: "",
        poster_path = poster_path ?: "",
        release_date = release_date ?: "",
        title = title ?: "",
        vote_average = vote_average ?: 0.0,
        popularity = popularity ?: 0.0,
        vote_count = vote_count ?: 0,
        id = id ?: -1,
        original_title = original_title ?: "",
        video = video ?: false,

        category = category,

        genre_ids = try {
            genre_ids?.joinToString(",") ?: "-1,-2"
        } catch (e: Exception) {
            "-1,-2"
        },
        isFavorite = isFavorite

    )
}
fun SearchEntity.toMovie(
): Movie {
    return Movie(
        adult = adult ?: false,
        backdrop_path = backdrop_path ?: "",
        original_language = original_language ?: "",
        overview = overview ?: "",
        poster_path = poster_path ?: "",
        release_date = release_date ?: "",
        title = title ?: "",
        vote_average = vote_average ?: 0.0,
        popularity = popularity ?: 0.0,
        vote_count = vote_count ?: 0,
        id = id ?: -1,
        original_title = original_title ?: "",
        video = video ?: false,

        category = category,

        genre_ids = try {
            genre_ids.split(",").map { it.toInt() }
        } catch (e: Exception) {
            listOf(-1, -2)
        },
        isFavorite = isFavorite
    )
}


fun Movie.toFavoriteMovieEntity(
    isFavorite : Boolean
): FavoriteEntity {
    return FavoriteEntity(
        adult = adult ?: false,
        backdrop_path = backdrop_path ?: "",
        original_language = original_language ?: "",
        overview = overview ?: "",
        poster_path = poster_path ?: "",
        release_date = release_date ?: "",
        title = title ?: "",
        vote_average = vote_average ?: 0.0,
        popularity = popularity ?: 0.0,
        vote_count = vote_count ?: 0,
        id = id ?: -1,
        original_title = original_title ?: "",
        video = video ?: false,

        category = category,

        genre_ids = try {
            genre_ids?.joinToString(",") ?: "-1,-2"
        } catch (e: Exception) {
            "-1,-2"
        },
        isFavorite = isFavorite
    )
}
fun FavoriteEntity.toMovie(
): Movie {
    return Movie(
        adult = adult ?: false,
        backdrop_path = backdrop_path ?: "",
        original_language = original_language ?: "",
        overview = overview ?: "",
        poster_path = poster_path ?: "",
        release_date = release_date ?: "",
        title = title ?: "",
        vote_average = vote_average ?: 0.0,
        popularity = popularity ?: 0.0,
        vote_count = vote_count ?: 0,
        id = id ?: -1,
        original_title = original_title ?: "",
        video = video ?: false,

        category = category,

        genre_ids = try {
            genre_ids.split(",").map { it.toInt() }
        } catch (e: Exception) {
            listOf(-1, -2)
        },
        isFavorite = isFavorite
    )
}
fun MovieDto.toMovie(
    category: String
): Movie {
    return Movie(
        adult = adult ?: false,
        backdrop_path = backdrop_path ?: "",
        original_language = original_language ?: "",
        overview = overview ?: "",
        poster_path = poster_path ?: "",
        release_date = release_date ?: "",
        title = title ?: "",
        vote_average = vote_average ?: 0.0,
        popularity = popularity ?: 0.0,
        vote_count = vote_count ?: 0,
        id = id ?: -1,
        original_title = original_title ?: "",
        video = video ?: false,

        category = category,
        genre_ids = genre_ids ?: emptyList()
    )
}
fun MovieEntity.toMovie(
    category : String,
) : Movie{
    return Movie(backdrop_path = backdrop_path,
    original_language = original_language,
    overview = overview,
    poster_path = poster_path,
    release_date = release_date,
    title = title,
    vote_average = vote_average,
    popularity = popularity,
    vote_count = vote_count,
    video = video,
    id = id,
    adult = adult,
    original_title = original_title,

    category = category,

        genre_ids = try {
            genre_ids.split(",").map { it.toInt() }
        } catch (e: Exception) {
            listOf(-1, -2)
        },
        isFavorite = isFavorite
    )
}

fun TvEntity.toMovie(
    category : String,
) : Movie{
    return Movie(backdrop_path = backdrop_path,
        original_language = original_language,
        overview = overview,
        poster_path = poster_path,
        release_date = first_air_date,
        title = name,
        vote_average = vote_average,
        popularity = popularity,
        vote_count = vote_count,
        video = false,
        id = id,
        adult = adult,
        original_title = original_name,

        category = category,
        genre_ids = try {
            genre_ids.split(",").map { it.toInt() }
        } catch (e: Exception) {
            listOf(-1, -2)
        },
        isFavorite = isFavorite
    )
}

fun Genre.toGenreEntity(category: String) : GenreEntity{
    return GenreEntity(
        id = id ,
        name = name,
        category =  category
    )
}

fun Genre.toGenreTvEntity(category: String) : GenreEntity{
    return GenreEntity(
        id = id ,
        name = name,
        category = category
    )
}

fun GenreEntity.toGenre() : GenreModel{
    return  GenreModel(
        id = id,
        name = name,
        category = category
    )
}