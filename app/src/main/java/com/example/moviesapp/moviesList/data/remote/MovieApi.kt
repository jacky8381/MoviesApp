package com.example.moviesapp.moviesList.data.remote

import com.example.moviesapp.moviesList.data.remote.respond.GenreDto
import com.example.moviesapp.moviesList.data.remote.respond.MovieListDto
import com.example.moviesapp.moviesList.data.remote.respond.TvListDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

    @GET("movie/{category}")
    suspend fun getMoviesList(
        @Path("category") category : String,
        @Query("page") page : Int,
        @Query("api_key") apiKey : String = API_KEY
        ) : MovieListDto

    @GET("tv/{category}")
    suspend fun getTvList(
        @Path("category") category : String,
        @Query("page") page : Int,
        @Query("api_key") apiKey : String = API_KEY
    ) : TvListDto


    @GET("genre/{category}/list")
    suspend fun getGenreList(
        @Path("category") category : String,
        @Query("api_key") apiKey : String = API_KEY
    ) : GenreDto

    @GET("discover/{type}")
    suspend fun discoverMovie(
        @Path("type") type : String,
        @Query("with_genres") genreId : String,
        @Query("include_adult") includeAdult : Boolean = false,
        @Query("page") page : Int,
        @Query("api_key") apiKey : String = API_KEY
    ) : MovieListDto

    @GET("search/movie")
    suspend fun searchApi(
        @Query("query") query : String,
        @Query("include_adult") includeAdult : Boolean = false,
        @Query("api_key") apiKey : String = API_KEY
    ): MovieListDto


    @GET("trending/movie/week")
    suspend fun getTrendingList(
        @Query("api_key") apiKey : String = API_KEY
    ) : MovieListDto

    @GET("discover/tv")
    suspend fun discoverTv(
        @Query("with_genres") genreId : String,
        @Query("include_adult") includeAdult : Boolean = false,
        @Query("page") page : Int,
        @Query("api_key") apiKey : String = API_KEY
    ) : TvListDto
    //https://api.themoviedb.org/3/discover/movie

//    @GET()
//    suspend fun getTVList(
//        @
//    )

    companion object{
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_BASE_URL="https://image.tmdb.org/t/p/w500"
        const val API_KEY="9eb4e4b783db50446ca81b56d50eeecf"
    }
}