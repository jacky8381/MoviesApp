package com.example.moviesapp.details.presentation

import com.example.moviesapp.moviesList.domain.model.Movie

data class DetailsState(
    val isLoading : Boolean = false,
    val movie : Movie? = null,
    val isFromSearchScreen : Boolean = false,
    val genres : String = ""
)
