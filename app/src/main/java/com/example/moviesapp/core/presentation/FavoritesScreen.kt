package com.example.moviesapp.core.presentation

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.ImageNotSupported
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.moviesapp.moviesList.data.remote.MovieApi
import com.example.moviesapp.moviesList.domain.model.Movie
import com.example.moviesapp.presentation.MovieListState
import com.example.moviesapp.presentation.MoviesListViewModel
import com.example.moviesapp.util.RatingBar
import com.example.moviesapp.util.Screen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.Gson

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    moviesListViewModel: MoviesListViewModel,
    navController: NavHostController
) {
    val uiState by moviesListViewModel.movieListState.collectAsState()
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color =Color(0xFF0F1014) // Dark icons when not focused (white background)
    )
    Scaffold(
      modifier = Modifier .padding(),
      topBar = {
            TopAppBar(
                title = {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F1014))
                    ){
                        Text(
                        text = "Favorites",
                        fontSize = 20.sp,
                            color = Color.White,
                            modifier = Modifier .weight(1f)
                        )
                        if (uiState.favoriteDBList.isNotEmpty()) {
                            Icon(imageVector = Icons.Rounded.Delete, contentDescription = "delete",
                                tint = Color(0xFFD0312D),
                                modifier = Modifier
                                    .padding(top = 5.dp, end = 10.dp)
                                    .clickable {
                                        moviesListViewModel.updateDialogValue(true)
                                    }
                            )
                        }
                    }

                },
                modifier = Modifier .shadow(2.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    Color(0xFF0F1014)
                )
            )

        }
  ) {

        if (uiState.showDeleteFavListDialog){
            DeleteDialog(onActionRequest = {
                moviesListViewModel.clearFavList()
            },
                onDissmissRequest = {
                    moviesListViewModel.updateDialogValue(false)
                })
        }
        if (uiState.favoriteDBList.isNotEmpty()) {
            FavoritesList(uiState, list = uiState.favoriteDBList, navController)
        }else{
            EmptyFavoritesScreen()
        }
  }
}

@Composable
fun FavoritesList(uiState: MovieListState,list : List<Movie>,  navHostController : NavHostController) {
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF0F1014))){
            itemsIndexed(list) { index: Int, item: Movie ->
                FavoriteItem(movie = item, navHostController = navHostController)
            }
    }
}

@Composable
fun FavoriteItem(movie : Movie,  navHostController : NavHostController) {
    val imageState = rememberAsyncImagePainter(model = ImageRequest.Builder(LocalContext.current)
        .data(MovieApi.IMAGE_BASE_URL+ movie.backdrop_path)
        .size(Size.ORIGINAL)
        .build()
    ).state
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val json = Uri.encode(Gson().toJson(movie))
                navHostController.navigate(Screen.Details.rout + "/$json")
            }
            .padding(10.dp)
    ){
        if(imageState is AsyncImagePainter.State.Error){
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(75.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(modifier = Modifier .size(40.dp),imageVector = Icons.Rounded.ImageNotSupported, contentDescription =movie.title )
            }
        }
        if(imageState is AsyncImagePainter.State.Success){
            Image(
                modifier = Modifier
                    .width(140.dp)
                    .height(75.dp)
                    .clip(RoundedCornerShape(5.dp)),
                painter = imageState.painter,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier .width(10.dp))
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier .height(15.dp))
            Text(
                text = movie.title,
                //modifier=Modifier .padding(start=16.dp,end=8.dp),
                color= Color.White,
                fontSize = 15.sp,
                maxLines = 1,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier .height(5.dp))
            Row {
                RatingBar(
                    starsModifier = Modifier.size(18.dp),
                    rating = movie.vote_average / 2
                )
                Spacer(modifier = Modifier .width(10.dp))
                Text(
                    text = (movie.vote_average / 2).toString().take(3),
                    modifier = Modifier.padding(start = 4.dp),
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }
        }
        Icon(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription ="item"
        )
    }
}

@Composable
fun DeleteDialog(
    onDissmissRequest: () -> Unit,
    onActionRequest: () -> Unit,
    ) {
    AlertDialog(
        containerColor = Color(0xFF2D2F3B),
        icon = {
        Icon(Icons.Rounded.DeleteForever, contentDescription = "Example Icon", tint = Color(0xFFD8DAE7), modifier = Modifier .size(30.dp))
    },
        title = {
            Text(text = "Are you sure you want to delete all your favorite movies?", color = Color(0xFFD8DAE7))
        },
        text = {
            //Text(text = dialogText)
        },
        onDismissRequest = {
            onDissmissRequest.invoke()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onActionRequest.invoke()
                    onDissmissRequest.invoke()
                }
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDissmissRequest.invoke()
                }
            ) {
                Text("No")
            }
        })
}

@Composable
fun EmptyFavoritesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1014))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.SentimentDissatisfied, // Outlined sad face
            contentDescription = "Sad Face",
            tint = Color.Gray,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "You haven't added anything in the favorites",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
    }
}

@Preview
@Composable
fun Item() {
DeleteDialog({},{})
}