package com.example.moviesapp.details.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesapp.moviesList.domain.model.Movie
import com.example.moviesapp.moviesList.domain.repository.MovieListRepository
import com.example.moviesapp.presentation.MovieListState
import com.example.moviesapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val movieListRepository: MovieListRepository,
    private val savedStateHandle : SavedStateHandle
) : ViewModel(){

    private val movieId= savedStateHandle.get<Int>("movieId")
    private var _detailsState = MutableStateFlow(DetailsState())
    val detailsState=_detailsState.asStateFlow()

    init {
        getMovie(movieId?: -1)
    }
    private fun getMovie(id : Int){
        viewModelScope.launch {
            _detailsState.update {
                it.copy(isLoading = true)
            }
            movieListRepository.getMovie(id).collectLatest {result->
                when(result){
                    is Resource.Error -> {
                        _detailsState.update {
                            it.copy(isLoading = false)
                        }

                    }
                    is Resource.Loading -> {
                        _detailsState.update {
                            it.copy(isLoading = result.isLoading)
                        }

                    }
                    is Resource.Success -> {
                        result.data ?. let {movie->
                            _detailsState.update {
                                it.copy(movie=movie)
                            }
                        }

                    }
                }

            }
        }
    }

    fun insertIntoFavorite(movie : Movie){
        viewModelScope.launch(Dispatchers.IO){
            movieListRepository.insertIntoFavoriteMovies(movie)
        }
    }

    fun deleteFromMovies(id : Int){
        viewModelScope.launch(Dispatchers.IO){
            movieListRepository.deleteFromFavoriteHistory(id)
        }
    }

    fun updateMovieEntity(isFavorite: Boolean, id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            movieListRepository.updateMovieEntity(isFavorite, id)
        }
    }

    fun updateFromSearchScreen(value : Boolean){
        _detailsState.update {
            it.copy(
                isFromSearchScreen = value
            )
        }
    }

    fun getGenreList(list : List<Int>) {
        viewModelScope.launch {
            _detailsState.update { it.copy(
                genres = movieListRepository.getGenreList(list) ?: ""
            ) }

        }
    }



}