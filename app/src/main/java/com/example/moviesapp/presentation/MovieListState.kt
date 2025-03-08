package com.example.moviesapp.presentation

import com.example.moviesapp.moviesList.domain.model.GenreModel
import com.example.moviesapp.moviesList.domain.model.Movie

data class MovieListState(
    val isLoading : Boolean = false,
    val popularMovieListPage : Int = 1,
    val upcomingMovieListPage : Int = 1,
    val topRatedMoviesPage : Int = 1,
    val nowPlayingMoviesPage : Int = 1,
    val horrorMoviesListPage : Int = 1,
    val soapTvListPage : Int = 1,
    val animatedMoviesPage : Int = 1,
    val crimeMoviesPage : Int = 1,
    val adventureMoviesPage : Int = 1,
    val scifiMoviesPage : Int = 1,
    val mysteryMoviesPage : Int = 1,
    val actionMoviesPage : Int = 1,
    val popularTvListPage : Int = 1,
    val airingTodayTvListPage : Int = 1,
    val romanceMoviesPage : Int = 1,
    val dramaMoviesPage : Int = 1,
    val familyMoviesPage : Int = 1,
    val historyMoviesPage : Int = 1,
    val comedyMoviesPage : Int = 1,


    val crimeTvListPage : Int = 1,
    val documentaryTvListPage : Int = 1,
    val talkTvListPage : Int = 1,
    val actionTvListPage : Int = 1,
    val scifiTvListPage : Int = 1,
    val familyTvListPage : Int = 1,
    val comedyTvListPage : Int = 1,
    val dramaTvListPage : Int = 1,
    val kidsTvListPage : Int = 1,
   val  animatedTvListPage : Int = 1,
    val realityTvListPage : Int = 1,
    val newsTvListPage : Int = 1,
    val westernTvListPage : Int = 1,


    val isCurrentPopularScreen : Boolean = true,

    val popularMovieList : List<Movie> = emptyList(),
    val trendingMovieList : List<Movie> = emptyList(),

    val upcomingMovieList : List<Movie> = emptyList(),
    val topRatedMovieList : List<Movie> = emptyList(),
    val nowPlayingMovieList : List<Movie> = emptyList(),


    val allMoviesList : List<Movie> = emptyList(),

    val genreList : List<GenreModel> = emptyList(),
    val popularTvList : List<Movie> = emptyList(),
    val airTodayTvList : List<Movie> = emptyList(),


    val horrorMoviesList : List<Movie> = emptyList(),
    val romanceMoviesList : List<Movie> = emptyList(),
    val familyMoviesList : List<Movie> = emptyList(),
    val historyMoviesList : List<Movie> = emptyList(),
    val dramaMoviesList : List<Movie> = emptyList(),
    val comedyMoviesList : List<Movie> = emptyList(),
    val adventureMoviesList : List<Movie> = emptyList(),
    val scifiMoviesList : List<Movie> = emptyList(),
    val crimeMoviesList : List<Movie> = emptyList(),
    val mysteryMoviesList : List<Movie> = emptyList(),
    val animatedMoviesList : List<Movie> = emptyList(),
    val actionMoviesList : List<Movie> = emptyList(),


    val searchedMoviesList : List<Movie> = emptyList(),

    val searchedDBList : List<Movie> = emptyList(),
    val favoriteDBList : List<Movie> = emptyList(),

    val moviesListOnVerticalPage : List<Movie> = emptyList(),

    val showDeleteFavListDialog : Boolean = false,


    val soapTvList : List<Movie> = emptyList(),
    val crimeTvList : List<Movie> = emptyList(),
    val documentaryTvList : List<Movie> = emptyList(),
    val talkTvList : List<Movie> = emptyList(),
    val actionAndAdventureTvList : List<Movie> = emptyList(),
    val comedyTvList : List<Movie> = emptyList(),
    val dramaTvList : List<Movie> = emptyList(),
    val familyTvList : List<Movie> = emptyList(),
    val kidsTvList : List<Movie> = emptyList(),
    val newsTvList : List<Movie> = emptyList(),
    val realityTvList : List<Movie> = emptyList(),
    val scifiAndFantasyTvList : List<Movie> = emptyList(),
    val westernTvList : List<Movie> = emptyList(),
    val animatedTvList : List<Movie> = emptyList(),



    )