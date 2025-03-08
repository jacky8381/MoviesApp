package com.example.moviesapp.details.presentation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.ImageNotSupported
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.moviesapp.R
import com.example.moviesapp.moviesList.data.remote.MovieApi
import com.example.moviesapp.moviesList.domain.model.Movie
import com.example.moviesapp.presentation.MoviesListViewModel
import com.example.moviesapp.util.RatingBar
import com.example.moviesapp.util.getAverageColor
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Composable
fun DetailsScreen(
    navController : NavHostController,
    moviesListViewModel: MoviesListViewModel,
    detailsViewModel: DetailsViewModel,
    detailsState : DetailsState
){
    val navBackStackEntry = remember { navController.currentBackStackEntry }
    val jsonMovie = navBackStackEntry?.arguments?.getString("movie")
    val movie = Gson().fromJson(jsonMovie, Movie::class.java)  // Deserialize JSON back to Movie object

    var isFavorite by remember {
        mutableStateOf(movie.isFavorite)
    }
    val coroutineScope = rememberCoroutineScope()

    detailsViewModel.getGenreList(movie.genre_ids)

    Log.d("movieadata", movie.toString())
    // val movie = jsonMovie?.let { Json.decodeFromString<Movie>(it) }

    navBackStackEntry?.arguments?.remove("movie")

    val backDropImageState = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(MovieApi.IMAGE_BASE_URL + movie?.backdrop_path)
            .size(Size.ORIGINAL)
            .build()
    ).state
    val posterImageState = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(MovieApi.IMAGE_BASE_URL +movie?.poster_path)
                .size(Size.ORIGINAL)
                .build()
            ).state


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if(backDropImageState is AsyncImagePainter.State.Error){

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier .size(70.dp),
                    imageVector = Icons.Rounded.ImageNotSupported,
                    contentDescription =detailsState.movie?.title
                )
            }
        }
        if(backDropImageState is AsyncImagePainter.State.Success){

            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                painter = backDropImageState.painter,
                contentDescription = movie?.title,
                contentScale = ContentScale.Crop

            )
        }
        
        Spacer(modifier = Modifier .height(16.dp))

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)

        ) {
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(240.dp)
            ) {
                if (posterImageState is AsyncImagePainter.State.Error) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(70.dp),
                            imageVector = Icons.Rounded.ImageNotSupported,
                            contentDescription = detailsState.movie?.title
                        )
                    }
                }
                if (posterImageState is AsyncImagePainter.State.Success) {

                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(14.dp)),
                        painter = posterImageState.painter,
                        contentDescription = detailsState.movie?.title,
                        contentScale = ContentScale.Crop

                    )
                }

            }
           movie?.let { movie ->
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = movie.title,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .padding(start = 16.dp)
                    ) {
                        RatingBar(
                            starsModifier = Modifier.size(18.dp),
                            rating = (movie.vote_average/2)
                        )

                        Text(
                            modifier = Modifier.padding(start = 4.dp),
                            text = (movie.vote_average/2).toString().take(3),
                            color = Color.LightGray,
                            fontSize = 14.sp,
                            maxLines = 1,
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            fontWeight = FontWeight.SemiBold,
                            text = stringResource(R.string.language)
                        )
                        Text(
                            modifier = Modifier.padding(start = 2.dp),
                            text =  movie.original_language
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            fontWeight = FontWeight.SemiBold,
                            text = stringResource(R.string.release_date)
                        )
                        Text(
                            modifier = Modifier.padding(start = 2.dp),
                            text =  movie.release_date
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (detailsState.genres.isNotEmpty()){
                        Row(
                            modifier = Modifier
                                .padding(start = 16.dp)
                        ) {
                                Text(
                                    fontWeight = FontWeight.SemiBold,
                                    text = "Genres: "
                                )
                                Text(
                                    modifier = Modifier.padding(start = 2.dp),
                                    text =  detailsState.genres
                                )
                         }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text =  movie.vote_count.toString() + " Votes"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (!detailsState.isFromSearchScreen) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .size(30.dp)
                                .clickable {
                                    isFavorite = !isFavorite
                                    if (!isFavorite) {
                                        detailsViewModel.updateMovieEntity(false, movie.id)
                                        detailsViewModel.deleteFromMovies(movie.id)
                                    } else {
                                        detailsViewModel.updateMovieEntity(true, movie.id)
                                        detailsViewModel.insertIntoFavorite(movie)
                                    }
                                    coroutineScope.launch {
                                        delay(1000)
                                        moviesListViewModel.refreshMoviesList(movie.category)
                                        Log.d("Info", movie.toString())
                                    }
                                },
                            contentDescription = "Icon",
                            tint = if (isFavorite) Color.Red else Color.LightGray
                        )
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = "Overview: ",
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

       movie?.let {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = it.overview,
                fontSize = 16.sp,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

    }

}