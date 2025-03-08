package com.example.moviesapp.util

sealed class Screen(val rout: String) {
    object Splash : Screen("splash")
    object Home : Screen("main")
    object PopularMovieList : Screen("popularMovie")
    object UpcomingMovieList : Screen("upcomingMovie")
    object Details : Screen("details")
    object MoviesList : Screen("moviesList")
    object SearchList : Screen("serachScreen")
    object FavoriteList : Screen("favoriteScreen")

}