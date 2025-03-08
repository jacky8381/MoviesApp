package com.example.moviesapp.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesapp.moviesList.domain.model.Movie
import com.example.moviesapp.moviesList.domain.repository.MovieListRepository
import com.example.moviesapp.util.Category
import com.example.moviesapp.util.Resource
import com.example.moviesapp.util.ScreenHeader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesListViewModel @Inject constructor(
    private val movieListRepository: MovieListRepository,
    private val savedStateHandle : SavedStateHandle

): ViewModel() {
    private val genreName= savedStateHandle.get<String>("genreName")

    private val _movieListState = MutableStateFlow(MovieListState())
    val movieListState = _movieListState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            searchQuery
                .debounce(500L)  // Debounce delay
                .distinctUntilChanged()  // Only emit if value changed
                .flowOn(Dispatchers.Default)  // Run on background thread
                .collect { query ->
                    if (query.isEmpty()) {
                        clearSearchedList()
                    } else {
                        getSearchedMoviesList(query)
                    }
                }
        }
        getGenreMovieList()
        getGenreTvList()
        getSearchedMoviesDBList()
        getFavoriteMovies()
        getPopularMovieList(false)
        getUpcomingMovieList(false)
        getTopRatedMovieList(false)
        getNowPlayingMovieList(false)
        getAiringTodayTvList(false)

       getTrendingMovieList(false)
        getPopularTvList(false)
    }

    fun updateSearchQuery(query: String) {
        viewModelScope.launch {
            _searchQuery.emit(query)
        }
    }

    fun onEvent(event: MovieListUiEvent){
        when(event){
            MovieListUiEvent.Navigate -> {
                _movieListState.update {
                    it.copy(
                        isCurrentPopularScreen = !movieListState.value.isCurrentPopularScreen
                    )
                }
            }
            is MovieListUiEvent.Paginate -> {
                when (event.category) {
                    //Movies
                    Category.POPULAR -> getPopularMovieList(true)
                    Category.UPCOMING -> getUpcomingMovieList(true)
                    Category.TOP_RATED -> getTopRatedMovieList(true)
                    Category.NOW_PLAYING -> getNowPlayingMovieList(true)
                    Category.HORROR -> getHorrorMoviesByGenre(true)
                    Category.ROMANCE -> getRomanceMoviesByGenre(true)
                    Category.DRAMA -> getDramaMoviesByGenre(true)
                    Category.HISTORY -> getHistoryMoviesByGenre(true)
                    Category.FAMILY -> getFamilyMoviesByGenre(true)
                    Category.COMEDY -> getComedyMoviesByGenre(true)
                    Category.ANIMATED -> getAnimatedMoviesByGenre(true)
                    Category.ACTION -> getActionMoviesByGenre(true)
                    Category.MYSTERY -> getMysteryMoviesByGenre(true)
                    Category.CRIME -> getCrimeMoviesByGenre(true)
                    Category.TRENDING -> getTrendingMovieList(true)
                    Category.SCI_FI -> getScifiMoviesByGenre(true)
                    Category.ADVENTURE -> getAdventureMoviesByGenre(true)

                    // TV Shows
                    Category.POPULAR_TV -> getPopularTvList(true)
                    Category.AIRING_TODAY -> getAiringTodayTvList(true)
                    Category.Soap_Tv -> getSoapTvShowsByGenre(true)
                    Category.Crime_Tv -> getCrimeTvShowsByGenre(true)
                    Category.Family_Tv -> getFamilyTvShowsByGenre(true)
                    Category.Action_Tv -> getActionTvShowsByGenre(true)
                    Category.Scifi_Tv -> getScifiTvShowsByGenre(true)
                    Category.Kids_Tv -> getKidsTvShowsByGenre(true)
                    Category.Animated_Tv -> getAnimatedTvShowsByGenre(true)
                    Category.Reality_Tv -> getRealityTvShowsByGenre(true)
                    Category.Talk_Tv -> getTalkTvShowsByGenre(true)
                    Category.News_Tv -> getNewsTvShowsByGenre(true)
                    Category.Drama_Tv -> getDramaTvShowsByGenre(true)
                    Category.Documentary_TV -> getDocumentaryTvShowsByGenre(true)
                    Category.Comedy_Tv -> getComedyTvShowsByGenre(true)
                    Category.Western_Tv -> getWesternTvShowsByGenre(true)

                    else -> {} // Handle unexpected categories safely
                }
            }
        }
    }
    fun getPopularMovieList(forceFetchFromRemote : Boolean, isFromMovieList : Boolean = false){
        viewModelScope.launch(Dispatchers.IO) {
            if (isFromMovieList){
                delay(100)
            }
            _movieListState.update {
                it.copy(isLoading = true)
            }
            movieListRepository.getMovieList(
                forceFetchFromRemote,
                Category.POPULAR,
                movieListState.value.popularMovieListPage
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }

                    }
                    is Resource.Success -> {
                        result.data?.let {popularList->
                            _movieListState.update {
                                it.copy(
                                    popularMovieList = (movieListState.value.popularMovieList + popularList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    popularMovieListPage = movieListState.value.popularMovieListPage + 1,
                                )
                            }

                        }
                        Log.d("MovieList", movieListState.value.popularMovieList.toString())
                        if (isFromMovieList){
                            delay(100)
                        }

                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                }
                }
            }
        }

    }

    fun getUpcomingMovieList(forceFetchFromRemote : Boolean, isFromMovieList: Boolean = false, isApiCallFailed : (Boolean) -> Unit = {}){
         Log.d("UpcomingCall", "Flow")
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            if (isFromMovieList){
                delay(100)
            }
            movieListRepository.getMovieList(
                forceFetchFromRemote,
                Category.UPCOMING,
                movieListState.value.upcomingMovieListPage
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }

                    }
                    is Resource.Success -> {
                        result.data?.let {upcomingList->
                            _movieListState.update {
                                it.copy(
                                    upcomingMovieList = (movieListState.value.upcomingMovieList + upcomingList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    upcomingMovieListPage = movieListState.value.upcomingMovieListPage + 1,
                                )
                            }
                            isApiCallFailed.invoke(false)
                            if (isFromMovieList){
                                delay(100)
                            }

                        }

                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }


    }

    fun getTopRatedMovieList(forceFetchFromRemote : Boolean, isFromMovieList: Boolean = false,isApiCallFailed : (Boolean) -> Unit = {}){
         Log.d("TopRated", "Flow")
         viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            if (isFromMovieList){
                delay(100)
            }
            movieListRepository.getMovieList(
                forceFetchFromRemote,
                Category.TOP_RATED,
                movieListState.value.topRatedMoviesPage
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }

                    }
                    is Resource.Success -> {
                        result.data?.let {topRatedList->
                            _movieListState.update {
                                it.copy(
                                    topRatedMovieList = (movieListState.value.topRatedMovieList + topRatedList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    topRatedMoviesPage = movieListState.value.topRatedMoviesPage + 1,
                                )
                            }

                        }
                        isApiCallFailed.invoke(false)
                        if (isFromMovieList){
                            delay(100)
                        }
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }


    }

    fun getNowPlayingMovieList(forceFetchFromRemote : Boolean, isFromMovieList: Boolean = false, isApiCallFailed : (Boolean) -> Unit = {}){
         Log.d("NowPlaying", "Flow")
         viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            if (isFromMovieList){
                delay(100)
            }
            movieListRepository.getMovieList(
                forceFetchFromRemote,
                Category.NOW_PLAYING,
                movieListState.value.nowPlayingMoviesPage
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }

                    }
                    is Resource.Success -> {
                        result.data?.let {nowPlayingList->
                            _movieListState.update {
                                it.copy(
                                    nowPlayingMovieList = (movieListState.value.nowPlayingMovieList + nowPlayingList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    nowPlayingMoviesPage = movieListState.value.nowPlayingMoviesPage + 1,
                                )
                            }
                            Log.d("SizeCheck", nowPlayingList.size.toString())
                            Log.d("SizeCheck", "Value" + nowPlayingList.filter { it.isFavorite }.toString())

                        }
                        isApiCallFailed.invoke(false)
                        if (isFromMovieList){
                            delay(100)
                        }
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }

    private fun getGenreMovieList(){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            movieListRepository.getGenreMovieList().collectLatest{ result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {genreList->
                            _movieListState.update {
                                it.copy(
                                        genreList = genreList
                                )
                            }
                            getHorrorMoviesByGenre(false)
                            getAnimatedMoviesByGenre(false)
                            getDramaMoviesByGenre(false)
                            getHistoryMoviesByGenre(false)
                            getFamilyMoviesByGenre(false)
                            getComedyMoviesByGenre(false)
                            getRomanceMoviesByGenre(false)
                            getCrimeMoviesByGenre(false)
                            getAdventureMoviesByGenre(false)
                            getActionMoviesByGenre(false)
                            getMysteryMoviesByGenre(false)
                            getScifiMoviesByGenre(false)
                            Log.d("GenreList", "Movie   -> $genreList")

                        }

                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }

            }
        }

    private fun getGenreTvList(){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            movieListRepository.getGenreTvList().collectLatest{ result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {genreList->
                            _movieListState.update {
                                it.copy(
                                    genreList = genreList
                                )
                            }
                            getSoapTvShowsByGenre(false)
                            getCrimeTvShowsByGenre(false)
                            getFamilyTvShowsByGenre(false)
                            getActionTvShowsByGenre(false)
                            getScifiTvShowsByGenre(false)
                            getKidsTvShowsByGenre(false)
                            getAnimatedTvShowsByGenre(false)
                            getRealityTvShowsByGenre(false)
                            getTalkTvShowsByGenre(false)
                            getNewsTvShowsByGenre(false)
                            getDramaTvShowsByGenre(false)
                            getDocumentaryTvShowsByGenre(false)
                            getComedyTvShowsByGenre(false)
                            getWesternTvShowsByGenre(false)
                            Log.d("GenreList", "Tv  -> $genreList")

                        }

                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }

        }
    }


    fun setListForMoviesListPage(name : String) {
        Log.d("ListPage" , name)
        _movieListState.update {
            it.copy(
                moviesListOnVerticalPage = when (name) {
                    ScreenHeader.COMING_SOON -> movieListState.value.upcomingMovieList
                    ScreenHeader.TOP_RATED -> movieListState.value.topRatedMovieList
                    ScreenHeader.NOW_PLAYING -> movieListState.value.nowPlayingMovieList
                    ScreenHeader.HORROR -> movieListState.value.horrorMoviesList
                    ScreenHeader.DRAMA -> movieListState.value.dramaMoviesList
                    ScreenHeader.ROMANCE -> movieListState.value.romanceMoviesList
                    ScreenHeader.HISTORY -> movieListState.value.historyMoviesList
                    ScreenHeader.FAMILY -> movieListState.value.familyMoviesList
                    ScreenHeader.COMEDY -> movieListState.value.comedyMoviesList
                    ScreenHeader.ACTION -> movieListState.value.actionMoviesList
                    ScreenHeader.MYSTERY -> movieListState.value.mysteryMoviesList
                    ScreenHeader.SCI_FI -> movieListState.value.scifiMoviesList
                    ScreenHeader.CRIME -> movieListState.value.crimeMoviesList
                    ScreenHeader.ADVENTURE -> movieListState.value.adventureMoviesList
                    ScreenHeader.ANIMATED -> movieListState.value.animatedMoviesList

                    ScreenHeader.POPULAR_SHOWS -> movieListState.value.popularTvList
                    ScreenHeader.AIRING_SHOWS -> movieListState.value.airTodayTvList
                    ScreenHeader.SOAP -> movieListState.value.soapTvList
                    ScreenHeader.Comedy_SHOWS -> movieListState.value.comedyTvList
                    ScreenHeader.Action_SHOWS -> movieListState.value.actionAndAdventureTvList
                    ScreenHeader.Scifi_SHOWS -> movieListState.value.scifiAndFantasyTvList
                    ScreenHeader.Family_SHOWS -> movieListState.value.familyTvList
                    ScreenHeader.Kids_SHOWS -> movieListState.value.kidsTvList
                    ScreenHeader.Animated_SHOWS -> movieListState.value.animatedTvList
                    ScreenHeader.Reality_SHOWS -> movieListState.value.realityTvList
                    ScreenHeader.Talk_SHOWS -> movieListState.value.talkTvList
                    ScreenHeader.Documentary_SHOWS -> movieListState.value.documentaryTvList
                    ScreenHeader.Crime_SHOWS -> movieListState.value.crimeTvList
                    ScreenHeader.Drama_SHOWS -> movieListState.value.dramaTvList
                    ScreenHeader.News_SHOWS -> movieListState.value.newsTvList
                    ScreenHeader.Western_SHOWS -> movieListState.value.westernTvList
                    else -> emptyList()
                }
            )
        }
    }

    fun getHorrorMoviesByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getMovieByGenre(
                forceFetchFromRemote,
                type ="movie",
                page = movieListState.value.horrorMoviesListPage,
                genre = "Horror",
                category = Category.HORROR
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    horrorMoviesList = (movieListState.value.horrorMoviesList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    horrorMoviesListPage = movieListState.value.horrorMoviesListPage + 1,
                                )
                            }

                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }
    fun getRomanceMoviesByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getMovieByGenre(
                forceFetchFromRemote,
                type ="movie",
                page = movieListState.value.romanceMoviesPage,
                genre = "Romance",
                category = Category.ROMANCE
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    romanceMoviesList = (movieListState.value.romanceMoviesList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    romanceMoviesPage = movieListState.value.romanceMoviesPage + 1,
                                )
                            }

                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }

    fun getHistoryMoviesByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getMovieByGenre(
                forceFetchFromRemote,
                type ="movie",
                page = movieListState.value.historyMoviesPage,
                genre = "History",
                category = Category.HISTORY
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    historyMoviesList = (movieListState.value.historyMoviesList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    historyMoviesPage = movieListState.value.historyMoviesPage + 1,
                                )
                            }

                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }
    fun getDramaMoviesByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getMovieByGenre(
                forceFetchFromRemote,
                type ="movie",
                page = movieListState.value.dramaMoviesPage,
                genre = "Drama",
                category = Category.DRAMA
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    dramaMoviesList = (movieListState.value.dramaMoviesList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    dramaMoviesPage = movieListState.value.dramaMoviesPage + 1,
                                )
                            }

                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }
    fun getFamilyMoviesByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getMovieByGenre(
                forceFetchFromRemote,
                type ="movie",
                page = movieListState.value.familyMoviesPage,
                genre = "Family",
                category = Category.FAMILY
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    familyMoviesList = (movieListState.value.familyMoviesList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    familyMoviesPage = movieListState.value.familyMoviesPage + 1,
                                )
                            }

                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }
    fun getComedyMoviesByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getMovieByGenre(
                forceFetchFromRemote,
                type ="movie",
                page = movieListState.value.comedyMoviesPage,
                genre = "Comedy",
                category = Category.COMEDY
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    comedyMoviesList = (movieListState.value.comedyMoviesList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    comedyMoviesPage = movieListState.value.comedyMoviesPage + 1,
                                )
                            }

                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }

    fun getSoapTvShowsByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getTvByGenre(
                forceFetchFromRemote,
                page = movieListState.value.soapTvListPage,
                genre = "Soap",
                category = Category.Soap_Tv
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    soapTvList = (movieListState.value.soapTvList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    soapTvListPage = movieListState.value.soapTvListPage + 1,
                                )
                            }
                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }
    fun getCrimeTvShowsByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getTvByGenre(
                forceFetchFromRemote,
                page = movieListState.value.crimeTvListPage,
                genre = "Crime",
                category = Category.Crime_Tv
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    crimeTvList = (movieListState.value.crimeTvList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    crimeTvListPage = movieListState.value.crimeTvListPage + 1,
                                )
                            }
                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }
    fun getFamilyTvShowsByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getTvByGenre(
                forceFetchFromRemote,
                page = movieListState.value.familyTvListPage,
                genre = "Family",
                category = Category.Family_Tv
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    familyTvList = (movieListState.value.familyTvList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    familyTvListPage = movieListState.value.familyTvListPage + 1,
                                )
                            }
                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }
    fun geCrimeTvShowsByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getTvByGenre(
                forceFetchFromRemote,
                page = movieListState.value.crimeTvListPage,
                genre = "Crime",
                category = Category.Crime_Tv
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    crimeTvList = (movieListState.value.crimeTvList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    crimeTvListPage = movieListState.value.crimeTvListPage + 1,
                                )
                            }
                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }
    fun getActionTvShowsByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getTvByGenre(
                forceFetchFromRemote,
                page = movieListState.value.actionTvListPage,
                genre = "Action & Adventure",
                category = Category.Action_Tv
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    actionAndAdventureTvList = (movieListState.value.actionAndAdventureTvList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    actionTvListPage = movieListState.value.actionTvListPage + 1,
                                )
                            }
                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }
    fun getDocumentaryTvShowsByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getTvByGenre(
                forceFetchFromRemote,
                page = movieListState.value.documentaryTvListPage,
                genre = "Documentary",
                category = Category.Documentary_TV
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    documentaryTvList = (movieListState.value.documentaryTvList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    documentaryTvListPage = movieListState.value.documentaryTvListPage + 1,
                                )
                            }
                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }
    fun getKidsTvShowsByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getTvByGenre(
                forceFetchFromRemote,
                page = movieListState.value.kidsTvListPage,
                genre = "Kids",
                category = Category.Kids_Tv
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    kidsTvList = (movieListState.value.kidsTvList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    kidsTvListPage = movieListState.value.kidsTvListPage + 1,
                                )
                            }
                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }
    fun getAnimatedTvShowsByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getTvByGenre(
                forceFetchFromRemote,
                page = movieListState.value.animatedTvListPage,
                genre = "Animation",
                category = Category.Animated_Tv
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    animatedTvList = (movieListState.value.animatedTvList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    animatedTvListPage = movieListState.value.animatedTvListPage + 1,
                                )
                            }
                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }
    fun getScifiTvShowsByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getTvByGenre(
                forceFetchFromRemote,
                page = movieListState.value.scifiTvListPage,
                genre = "Sci-Fi & Fantasy",
                category = Category.Scifi_Tv
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    scifiAndFantasyTvList = (movieListState.value.scifiAndFantasyTvList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    scifiTvListPage = movieListState.value.scifiTvListPage + 1,
                                )
                            }
                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }
    fun getRealityTvShowsByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getTvByGenre(
                forceFetchFromRemote,
                page = movieListState.value.realityTvListPage,
                genre = "Reality",
                category = Category.Reality_Tv
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    realityTvList = (movieListState.value.realityTvList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    realityTvListPage = movieListState.value.realityTvListPage + 1,
                                )
                            }
                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }
    fun getTalkTvShowsByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getTvByGenre(
                forceFetchFromRemote,
                page = movieListState.value.talkTvListPage,
                genre = "Talk",
                category = Category.Talk_Tv
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    talkTvList = (movieListState.value.talkTvList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    talkTvListPage = movieListState.value.talkTvListPage + 1,
                                )
                            }
                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }
    fun getNewsTvShowsByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getTvByGenre(
                forceFetchFromRemote,
                page = movieListState.value.newsTvListPage,
                genre = "News",
                category = Category.News_Tv
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    newsTvList = (movieListState.value.newsTvList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    newsTvListPage = movieListState.value.newsTvListPage + 1,
                                )
                            }
                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }
    fun getDramaTvShowsByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getTvByGenre(
                forceFetchFromRemote,
                page = movieListState.value.dramaTvListPage,
                genre = "Drama",
                category = Category.Drama_Tv
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    dramaTvList = (movieListState.value.dramaTvList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    dramaTvListPage = movieListState.value.dramaTvListPage + 1,
                                )
                            }
                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }
    fun getWesternTvShowsByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getTvByGenre(
                forceFetchFromRemote,
                page = movieListState.value.westernTvListPage,
                genre = "Western",
                category = Category.Western_Tv
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    westernTvList = (movieListState.value.westernTvList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    westernTvListPage = movieListState.value.westernTvListPage + 1,
                                )
                            }
                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }
    fun getComedyTvShowsByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getTvByGenre(
                forceFetchFromRemote,
                page = movieListState.value.comedyTvListPage,
                genre = "Comedy",
                category = Category.Comedy_Tv
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    comedyTvList = (movieListState.value.comedyTvList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    comedyTvListPage = movieListState.value.comedyTvListPage + 1,
                                )
                            }
                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }


    fun getScifiMoviesByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getMovieByGenre(
                forceFetchFromRemote,
                type ="movie",
                page = movieListState.value.scifiMoviesPage,
                genre = "Science Fiction",
                category = Category.SCI_FI
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    scifiMoviesList = (movieListState.value.scifiMoviesList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    scifiMoviesPage = movieListState.value.scifiMoviesPage + 1,
                                )
                            }

                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }

    fun getPopularTvList(forceFetchFromRemote : Boolean, isFromMovieList : Boolean = false, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            if (isFromMovieList){
                delay(100)
            }
            _movieListState.update {
                it.copy(isLoading = true)
            }
            movieListRepository.getTvList(
                forceFetchFromRemote,
                Category.POPULAR_TV,
                movieListState.value.popularTvListPage
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }

                    }
                    is Resource.Success -> {
                        result.data?.let {popularList->
                            _movieListState.update {
                                it.copy(
                                    popularTvList = (movieListState.value.popularTvList + popularList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    popularTvListPage = movieListState.value.popularTvListPage + 1,
                                )
                            }
                            isFailed.invoke(false)
                        }
                        Log.d("MovieList", movieListState.value.popularMovieList.toString())
                        if (isFromMovieList){
                            delay(100)
                        }

                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }

    }

    fun getAiringTodayTvList(forceFetchFromRemote : Boolean, isFromMovieList : Boolean = false, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            if (isFromMovieList){
                delay(100)
            }
            _movieListState.update {
                it.copy(isLoading = true)
            }
            movieListRepository.getTvList(
                forceFetchFromRemote,
                Category.AIRING_TODAY,
                movieListState.value.airingTodayTvListPage
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }

                    }
                    is Resource.Success -> {
                        result.data?.let {popularList->
                            _movieListState.update {
                                it.copy(
                                    airTodayTvList = (movieListState.value.airTodayTvList + popularList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    airingTodayTvListPage = movieListState.value.airingTodayTvListPage + 1,
                                )
                            }
                            isFailed.invoke(false)
                            Log.d("AiringList", movieListState.value.airTodayTvList.toString())
                        }
                        if (isFromMovieList){
                            delay(100)
                        }

                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }

    }


    fun getMysteryMoviesByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getMovieByGenre(
                forceFetchFromRemote,
                type ="movie",
                page = movieListState.value.mysteryMoviesPage,
                genre = "Mystery",
                category = Category.MYSTERY
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            Log.d("MovieSize", "Mystery ${horrorList.size}")

                            _movieListState.update {
                                it.copy(
                                    mysteryMoviesList = (movieListState.value.mysteryMoviesList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    mysteryMoviesPage = movieListState.value.mysteryMoviesPage + 1,
                                )
                            }

                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }

    fun getCrimeMoviesByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getMovieByGenre(
                forceFetchFromRemote,
                type ="movie",
                page = movieListState.value.crimeMoviesPage,
                genre = "Crime",
                category = Category.CRIME
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            _movieListState.update {
                                it.copy(
                                    crimeMoviesList = (movieListState.value.crimeMoviesList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    crimeMoviesPage = movieListState.value.crimeMoviesPage + 1,
                                )
                            }

                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }

    fun getAdventureMoviesByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getMovieByGenre(
                forceFetchFromRemote,
                type ="movie",
                page = movieListState.value.adventureMoviesPage,
                genre = "Adventure",
                category = Category.ADVENTURE
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            Log.d("MovieSize", "Adventure ${horrorList.size}")
                            _movieListState.update {
                                it.copy(
                                    adventureMoviesList = (movieListState.value.adventureMoviesList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    adventureMoviesPage = movieListState.value.adventureMoviesPage + 1,
                                )
                            }

                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }

    fun getActionMoviesByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getMovieByGenre(
                forceFetchFromRemote,
                type ="movie",
                page = movieListState.value.actionMoviesPage,
                genre = "Action",
                category = Category.ACTION
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {horrorList->
                            Log.d("MovieSize", "Action ${horrorList.size}")

                            _movieListState.update {
                                it.copy(
                                    actionMoviesList = (movieListState.value.actionMoviesList + horrorList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    actionMoviesPage = movieListState.value.actionMoviesPage + 1,
                                )
                            }

                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }

    fun getTrendingMovieList(forceFetchFromRemote : Boolean){
        viewModelScope.launch(Dispatchers.IO) {

            _movieListState.update {
                it.copy(isLoading = true)
            }
            movieListRepository.getTrendingMovieList(
                forceFetchFromRemote,
                "Trending"
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }

                    }
                    is Resource.Success -> {
                        result.data?.let {popularList->
                            _movieListState.update {
                                it.copy(
                                    trendingMovieList = (movieListState.value.trendingMovieList + popularList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                )
                            }
                        }
                        Log.d("MovieList", movieListState.value.trendingMovieList.toString())
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }

    }

    fun getAnimatedMoviesByGenre(forceFetchFromRemote : Boolean, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.getMovieByGenre(
                forceFetchFromRemote,
                type ="movie",
                page = movieListState.value.animatedMoviesPage,
                genre = "Animation",
                category = Category.ANIMATED
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {animatedList->
                            _movieListState.update {
                                it.copy(
                                    animatedMoviesList = (movieListState.value.animatedMoviesList + animatedList)
                                        .associateBy { it.id } // Convert to Map (latest entry wins)
                                        .values.toList(),
                                    animatedMoviesPage = movieListState.value.animatedMoviesPage + 1,
                                )
                            }

                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }

    fun getSearchedMoviesList(value : String, isFailed : (Boolean) -> Unit = {}){
        viewModelScope.launch(Dispatchers.IO) {
            _movieListState.update {
                it.copy(isLoading = true)
            }
            Log.d("DbCall", "Flow")
            movieListRepository.searchMovies(
                value = value
            ).collectLatest { result->
                when(result){
                    is Resource.Error -> {
                        _movieListState.update {
                            it.copy(isLoading = false)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {searchList->
                            _movieListState.update {
                                it.copy(
                                    searchedMoviesList =   searchList,
                                    isLoading = false
                                )
                            }
                            Log.d("SearchApi", searchList.toString())
                        }
                        isFailed.invoke(false)
                    }
                    is Resource.Loading -> {
                        _movieListState.update {
                            it.copy(isLoading = result.isLoading)
                        }
                    }
                }
            }
        }
    }

    fun updateLoadingState(){
        _movieListState.update {
            it.copy(
                isLoading = true
            )
        }
    }

    fun clearSearchedList(){
        _movieListState.update {
            it.copy(
                searchedMoviesList = emptyList()
            )
        }
    }

    fun updateDialogValue(value : Boolean){
        _movieListState.update {
            it.copy(
                showDeleteFavListDialog = value
            )
        }
    }

    fun clearFavList(){
        viewModelScope.launch {
            movieListRepository.deleteFavHistory()
        }
    }

    fun updateDBForSearchItems(movie : Movie){
    viewModelScope.launch(Dispatchers.IO) {
        movieListRepository.insertSeachedMovies(movie)
        Log.d("DBCall", "VMUpdate $movie")

    }
    }

    fun clearSearchHistory(){
        viewModelScope.launch(Dispatchers.IO) {
            movieListRepository.deleteSearchHistory()
        }
    }

    fun getSearchedMoviesDBList(){
        viewModelScope.launch(Dispatchers.IO) {
           movieListRepository.getSearchMovies().collectLatest{ movies->
               _movieListState.update {
                   it.copy(
                       searchedDBList = movies
                   )
               }
               Log.d("DBCall", "VMGet $movies")
           }
        }
    }

    fun getFavoriteMovies(){
        viewModelScope.launch(Dispatchers.IO) {
            movieListRepository.getFavoriteMovies().collectLatest{ movies->
                _movieListState.update {
                    it.copy(
                        favoriteDBList = movies
                    )
                }
            }
        }
    }

    fun refreshMoviesList(category: String) {
        when (category) {
            //Movies
            Category.POPULAR -> getPopularMovieList(false)
            Category.UPCOMING -> getUpcomingMovieList(false)
            Category.TOP_RATED -> getTopRatedMovieList(false)
            Category.NOW_PLAYING -> getNowPlayingMovieList(false)
            Category.HORROR -> getHorrorMoviesByGenre(false)
            Category.ROMANCE -> getRomanceMoviesByGenre(false)
            Category.DRAMA -> getDramaMoviesByGenre(false)
            Category.HISTORY -> getHistoryMoviesByGenre(false)
            Category.FAMILY -> getFamilyMoviesByGenre(false)
            Category.COMEDY -> getComedyMoviesByGenre(false)
            Category.ANIMATED -> getAnimatedMoviesByGenre(false)
            Category.ACTION -> getActionMoviesByGenre(false)
            Category.MYSTERY -> getMysteryMoviesByGenre(false)
            Category.CRIME -> getCrimeMoviesByGenre(false)
            Category.TRENDING -> getTrendingMovieList(false)
            Category.SCI_FI -> getScifiMoviesByGenre(false)
            Category.ADVENTURE -> getAdventureMoviesByGenre(false)

            // TV Shows
            Category.POPULAR_TV -> getPopularTvList(false)
            Category.AIRING_TODAY -> getAiringTodayTvList(false)
            Category.Soap_Tv -> getSoapTvShowsByGenre(false)
            Category.Crime_Tv -> getCrimeTvShowsByGenre(false)
            Category.Family_Tv -> getFamilyTvShowsByGenre(false)
            Category.Action_Tv -> getActionTvShowsByGenre(false)
            Category.Scifi_Tv -> getScifiTvShowsByGenre(false)
            Category.Kids_Tv -> getKidsTvShowsByGenre(false)
            Category.Animated_Tv -> getAnimatedTvShowsByGenre(false)
            Category.Reality_Tv -> getRealityTvShowsByGenre(false)
            Category.Talk_Tv -> getTalkTvShowsByGenre(false)
            Category.News_Tv -> getNewsTvShowsByGenre(false)
            Category.Drama_Tv -> getDramaTvShowsByGenre(false)
            Category.Documentary_TV -> getDocumentaryTvShowsByGenre(false)
            Category.Comedy_Tv -> getComedyTvShowsByGenre(false)
            Category.Western_Tv -> getWesternTvShowsByGenre(false)

            else -> {
                println("Invalid category: $category")
            }
        }
    }



}