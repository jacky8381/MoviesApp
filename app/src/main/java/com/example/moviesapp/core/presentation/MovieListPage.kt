package com.example.moviesapp.core.presentation

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.moviesapp.moviesList.data.remote.MovieApi
import com.example.moviesapp.presentation.MovieListState
import com.example.moviesapp.presentation.MoviesListViewModel
import com.example.moviesapp.util.Screen
import com.example.moviesapp.util.ScreenHeader
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListPage(
    navController: NavHostController,
    headerTitle : String,
    movieListViewModel : MoviesListViewModel,
    moviesState: MovieListState
) {
    Scaffold(
        topBar = {
            TopAppBar(title ={
                Text(text =headerTitle,
                    color = Color.White,
                    modifier = Modifier .padding(start = 8.dp),
                    fontSize = 18.sp,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold
                )
            }, navigationIcon = {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back" ,
                    modifier = Modifier .clickable {
                    navController.popBackStack()
                }  .padding(start = 8.dp))
            }
                )
        }
    ) { it->
        val moviesList = moviesState.moviesListOnVerticalPage
        val context = LocalContext.current
        if (moviesList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()

            }
        } else {
                var isApiCallInProgress by remember { mutableStateOf(false) }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    //contentPadding = PaddingValues(vertical = 8.dp, horizontal = 2.dp)
                ) {
                    items(moviesList.size) { index ->
                        Log.d("HorrorMovies", "View" + moviesList.size.toString() + "index" + index)

                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(MovieApi.IMAGE_BASE_URL + moviesList[index].poster_path)
                                .crossfade(true)
                                .diskCacheKey(moviesList[index].id.toString() + "poster")
                                .memoryCacheKey(moviesList[index].id.toString() + "poster")
                                .fallback(ColorDrawable(MaterialTheme.colorScheme.primaryContainer.toArgb()))
                                .error(ColorDrawable(MaterialTheme.colorScheme.primaryContainer.toArgb()))
                                //.size(Size.ORIGINAL)
                                .build(),
                            contentDescription = moviesList[index].title,
                            modifier = Modifier

                                .padding(2.dp)
                                .clickable {
                                    val json = Uri.encode(Gson().toJson(moviesList[index]))
                                    navController.navigate(Screen.Details.rout +"/$json")

                                   // navController.navigate(Screen.Details.rout + "/${moviesList[index].id}")
                                }
                                // .width(120.dp)
                                .height(185.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            filterQuality = FilterQuality.High,
                            contentScale = ContentScale.FillBounds,

                            )
                        if (index == moviesList.size - 1 && !isApiCallInProgress) {
                            isApiCallInProgress = true
                            callApi(title = headerTitle,
                                isApiCallFailed = {
                                isApiCallInProgress = it
                                },
                              movieListViewModel=  movieListViewModel
                            )
                        }
                    }

                }
        }
    }
}

private fun callApi(title : String , isApiCallFailed : (Boolean) -> Unit, movieListViewModel: MoviesListViewModel) {
    when (title) {
        ScreenHeader.COMING_SOON -> movieListViewModel.getUpcomingMovieList(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.TOP_RATED -> movieListViewModel.getTopRatedMovieList(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.NOW_PLAYING -> movieListViewModel.getNowPlayingMovieList(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.HORROR -> movieListViewModel.getHorrorMoviesByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.ROMANCE -> movieListViewModel.getRomanceMoviesByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.DRAMA -> movieListViewModel.getDramaMoviesByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.HISTORY -> movieListViewModel.getHistoryMoviesByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.FAMILY -> movieListViewModel.getFamilyMoviesByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.COMEDY -> movieListViewModel.getComedyMoviesByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.ACTION -> movieListViewModel.getActionMoviesByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.MYSTERY -> movieListViewModel.getMysteryMoviesByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.CRIME -> movieListViewModel.getCrimeMoviesByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.SCI_FI -> movieListViewModel.getScifiMoviesByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.ADVENTURE -> movieListViewModel.getAdventureMoviesByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.ANIMATED -> movieListViewModel.getAnimatedMoviesByGenre(true) { isApiCallFailed.invoke(it) }

        // TV Shows
        ScreenHeader.POPULAR_SHOWS -> movieListViewModel.getPopularTvList(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.AIRING_SHOWS -> movieListViewModel.getAiringTodayTvList(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.SOAP -> movieListViewModel.getSoapTvShowsByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.Crime_SHOWS -> movieListViewModel.getCrimeTvShowsByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.Family_SHOWS -> movieListViewModel.getFamilyTvShowsByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.Action_SHOWS -> movieListViewModel.getActionTvShowsByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.Scifi_SHOWS -> movieListViewModel.getScifiTvShowsByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.Kids_SHOWS -> movieListViewModel.getKidsTvShowsByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.Animated_SHOWS -> movieListViewModel.getAnimatedTvShowsByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.Reality_SHOWS -> movieListViewModel.getRealityTvShowsByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.Talk_SHOWS -> movieListViewModel.getTalkTvShowsByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.News_SHOWS -> movieListViewModel.getNewsTvShowsByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.Drama_SHOWS -> movieListViewModel.getDramaTvShowsByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.Documentary_SHOWS -> movieListViewModel.getDocumentaryTvShowsByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.Comedy_SHOWS -> movieListViewModel.getComedyTvShowsByGenre(true) { isApiCallFailed.invoke(it) }
        ScreenHeader.Western_SHOWS -> movieListViewModel.getWesternTvShowsByGenre(true) { isApiCallFailed.invoke(it) }

        else -> {} // Handle unexpected categories safely
    }
}