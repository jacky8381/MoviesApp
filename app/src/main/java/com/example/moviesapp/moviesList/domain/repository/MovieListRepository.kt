package com.example.moviesapp.moviesList.domain.repository

import com.example.moviesapp.moviesList.data.local.movie.SearchEntity
import com.example.moviesapp.moviesList.data.remote.respond.Genre
import com.example.moviesapp.moviesList.data.remote.respond.GenreDto
import com.example.moviesapp.moviesList.domain.model.GenreModel
import com.example.moviesapp.moviesList.domain.model.Movie
import com.example.moviesapp.moviesList.domain.model.Tv
import com.example.moviesapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface MovieListRepository {
    suspend fun getMovieList(
        foreFetchFromRemote : Boolean,
        category : String,
        page : Int
    ) : Flow<Resource<List<Movie>>>

    suspend fun getTvList(
        foreFetchFromRemote : Boolean,
        category : String,
        page : Int
    ) : Flow<Resource<List<Movie>>>

    suspend fun getMovie(
        id : Int
    ) : Flow<Resource<Movie>>

    suspend fun getGenreMovieList() : Flow<Resource<List<GenreModel>>>

    suspend fun getGenreTvList() : Flow<Resource<List<GenreModel>>>


    suspend fun getTrendingMovieList(
        foreFetchFromRemote: Boolean,
        category: String
    ) : Flow<Resource<List<Movie>>>



    suspend fun getMovieByGenre(
        foreFetchFromRemote : Boolean,
        type : String,
        genre : String,
        page : Int,
        category: String
    ) : Flow<Resource<List<Movie>>>

    suspend fun getTvByGenre(
        foreFetchFromRemote : Boolean,
        genre : String,
        page : Int,
        category: String
    ) : Flow<Resource<List<Movie>>>

    suspend fun searchMovies(
        value : String
    ) : Flow<Resource<List<Movie>>>

    suspend fun insertSeachedMovies(
        movie : Movie
    )
    suspend fun getSearchMovies() : Flow<List<Movie>>

    suspend fun deleteSearchHistory()

    suspend fun insertIntoFavoriteMovies(
        movie : Movie
    )
    suspend fun getFavoriteMovies() : Flow<List<Movie>>

    suspend fun deleteFromFavoriteHistory(
        id: Int
    )

    suspend fun updateMovieEntity(
        isFavorite : Boolean,
        id: Int
    )

    suspend fun deleteFavHistory()

    suspend fun getGenreList(list : List<Int>) : String

}