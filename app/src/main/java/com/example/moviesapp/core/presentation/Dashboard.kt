package com.example.moviesapp.core.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.moviesapp.presentation.MovieListState
import com.example.moviesapp.presentation.MoviesListViewModel
import com.example.moviesapp.util.Category
import com.example.moviesapp.util.ScreenHeader

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Dashboard(selectedButton : String, movieState: MovieListState, moviesListViewModel: MoviesListViewModel, navHostController : NavHostController) {

    val commonCategoryList = listOf(
        CategoryListConfig(
            title = ScreenHeader.TOP_RATED,
            movieList = movieState.topRatedMovieList,
            fetchData = { moviesListViewModel.getTopRatedMovieList(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.AIRING_SHOWS,
            movieList = movieState.airTodayTvList,
            fetchData = { moviesListViewModel.getAiringTodayTvList(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.COMING_SOON,
            movieList = movieState.upcomingMovieList,
            fetchData = { moviesListViewModel.getUpcomingMovieList(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.POPULAR_SHOWS,
            movieList = movieState.popularTvList,
            fetchData = { moviesListViewModel.getPopularTvList(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.NOW_PLAYING,
            movieList = movieState.nowPlayingMovieList,
            fetchData = { moviesListViewModel.getNowPlayingMovieList(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.HORROR,
            movieList = movieState.horrorMoviesList,
            fetchData = { moviesListViewModel.getHorrorMoviesByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.ANIMATED,
            movieList = movieState.animatedMoviesList,
            fetchData = { moviesListViewModel.getAnimatedMoviesByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.SOAP,
            movieList = movieState.soapTvList,
            fetchData = { moviesListViewModel.getSoapTvShowsByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.Documentary_SHOWS,
            movieList = movieState.documentaryTvList,
            fetchData = { moviesListViewModel.getDocumentaryTvShowsByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.FAMILY,
            movieList = movieState.familyMoviesList,
            fetchData = { moviesListViewModel.getFamilyMoviesByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.Kids_SHOWS,
            movieList = movieState.kidsTvList,
            fetchData = { moviesListViewModel.getKidsTvShowsByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.COMEDY,
            movieList = movieState.comedyMoviesList,
            fetchData = { moviesListViewModel.getComedyMoviesByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.Crime_SHOWS,
            movieList = movieState.crimeTvList,
            fetchData = { moviesListViewModel.getCrimeTvShowsByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.SCI_FI,
            movieList = movieState.scifiMoviesList,
            fetchData = { moviesListViewModel.getScifiMoviesByGenre(true) }
        ),
    )

    val tvCategoryList = listOf(
        CategoryListConfig(
            title = ScreenHeader.AIRING_SHOWS,
            movieList = movieState.airTodayTvList,
            fetchData = { moviesListViewModel.getAiringTodayTvList(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.Documentary_SHOWS,
            movieList = movieState.documentaryTvList,
            fetchData = { moviesListViewModel.getDocumentaryTvShowsByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.Comedy_SHOWS,
            movieList = movieState.comedyTvList,
            fetchData = { moviesListViewModel.getComedyTvShowsByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.Drama_SHOWS,
            movieList = movieState.dramaTvList,
            fetchData = { moviesListViewModel.getDramaTvShowsByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.Action_SHOWS,
            movieList = movieState.actionAndAdventureTvList,
            fetchData = { moviesListViewModel.getActionTvShowsByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.Kids_SHOWS,
            movieList = movieState.kidsTvList,
            fetchData = { moviesListViewModel.getKidsTvShowsByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.Family_SHOWS,
            movieList = movieState.familyTvList,
            fetchData = { moviesListViewModel.getFamilyTvShowsByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.Talk_SHOWS,
            movieList = movieState.talkTvList,
            fetchData = { moviesListViewModel.getTalkTvShowsByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.Reality_SHOWS,
            movieList = movieState.realityTvList,
            fetchData = { moviesListViewModel.getRealityTvShowsByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.Animated_SHOWS,
            movieList = movieState.animatedTvList,
            fetchData = { moviesListViewModel.getAnimatedTvShowsByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.Crime_SHOWS,
            movieList = movieState.crimeTvList,
            fetchData = { moviesListViewModel.getCrimeTvShowsByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.Scifi_SHOWS,
            movieList = movieState.scifiAndFantasyTvList,
            fetchData = { moviesListViewModel.getScifiTvShowsByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.News_SHOWS,
            movieList = movieState.newsTvList,
            fetchData = { moviesListViewModel.getNewsTvShowsByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.Western_SHOWS,
            movieList = movieState.westernTvList,
            fetchData = { moviesListViewModel.getWesternTvShowsByGenre(true) }
        )
    )

    val movieCategoryList = listOf(
        CategoryListConfig(
            title = ScreenHeader.TOP_RATED,
            movieList = movieState.topRatedMovieList,
            fetchData = { moviesListViewModel.getTopRatedMovieList(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.ACTION,
            movieList = movieState.actionMoviesList,
            fetchData = { moviesListViewModel.getActionMoviesByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.FAMILY,
            movieList = movieState.familyMoviesList,
            fetchData = { moviesListViewModel.getFamilyMoviesByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.ROMANCE,
            movieList = movieState.romanceMoviesList,
            fetchData = { moviesListViewModel.getRomanceMoviesByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.HORROR,
            movieList = movieState.horrorMoviesList,
            fetchData = { moviesListViewModel.getHorrorMoviesByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.ANIMATED,
            movieList = movieState.animatedMoviesList,
            fetchData = { moviesListViewModel.getAnimatedMoviesByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.NOW_PLAYING,
            movieList = movieState.nowPlayingMovieList,
            fetchData = { moviesListViewModel.getNowPlayingMovieList(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.COMEDY,
            movieList = movieState.comedyMoviesList,
            fetchData = { moviesListViewModel.getComedyMoviesByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.COMING_SOON,
            movieList = movieState.upcomingMovieList,
            fetchData = { moviesListViewModel.getUpcomingMovieList(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.HISTORY,
            movieList = movieState.historyMoviesList,
            fetchData = { moviesListViewModel.getHistoryMoviesByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.DRAMA,
            movieList = movieState.dramaMoviesList,
            fetchData = { moviesListViewModel.getDramaMoviesByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.MYSTERY,
            movieList = movieState.mysteryMoviesList,
            fetchData = { moviesListViewModel.getMysteryMoviesByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.SCI_FI,
            movieList = movieState.scifiMoviesList,
            fetchData = { moviesListViewModel.getScifiMoviesByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.CRIME,
            movieList = movieState.crimeMoviesList,
            fetchData = { moviesListViewModel.getCrimeMoviesByGenre(true) }
        ),
        CategoryListConfig(
            title = ScreenHeader.ADVENTURE,
            movieList = movieState.adventureMoviesList,
            fetchData = { moviesListViewModel.getAdventureMoviesByGenre(true) }
        )
    )
    if (selectedButton == Category.HOME_CATEGORY_TV){
        CommonScreen(
            navHostController = navHostController,
            pagerList = movieState.popularTvList,
            categoryLists = tvCategoryList,
            floatingActionCategory = true
        )
    }else if (selectedButton == Category.HOME_CATEGORY_MOVIE){
        CommonScreen(
            navHostController = navHostController,
            pagerList = movieState.topRatedMovieList,
            categoryLists = movieCategoryList,
            floatingActionCategory = true
        )
    }else{
        CommonScreen(
            navHostController = navHostController,
            pagerList = movieState.trendingMovieList,
            categoryLists = commonCategoryList
        )
    }

}

