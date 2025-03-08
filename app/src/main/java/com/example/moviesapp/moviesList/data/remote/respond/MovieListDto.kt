package com.example.moviesapp.moviesList.data.remote.respond

data class MovieListDto(
    val dates: Dates,
    val page: Int,
    val results: List<MovieDto>,
    val total_pages: Int,
    val total_results: Int
)