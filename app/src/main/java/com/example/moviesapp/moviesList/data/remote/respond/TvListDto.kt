package com.example.moviesapp.moviesList.data.remote.respond

data class TvListDto(
    val page: Int,
    val results: List<TvDto>,
    val total_pages: Int,
    val total_results: Int
)