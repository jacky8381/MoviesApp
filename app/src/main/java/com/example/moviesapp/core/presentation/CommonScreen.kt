package com.example.moviesapp.core.presentation

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.moviesapp.moviesList.data.remote.MovieApi
import com.example.moviesapp.moviesList.domain.model.Movie
import com.example.moviesapp.presentation.MovieListState
import com.example.moviesapp.presentation.MovieListUiEvent
import com.example.moviesapp.presentation.MoviesListViewModel
import com.example.moviesapp.util.Category
import com.example.moviesapp.util.Screen
import com.example.moviesapp.util.ScreenHeader
import com.google.gson.Gson

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommonScreen(
    navHostController : NavHostController,
    pagerList : List<Movie>,
    categoryLists: List<CategoryListConfig> = emptyList(),
    floatingActionCategory : Boolean = false
  ) {

    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val pageCount = pagerList.take(8).size
    val pagerState = rememberPagerState(
        pageCount = {
            pageCount
        },
        initialPage = 0,
    )
    val moviesList = pagerList.take(8)
    Log.d("Size", pagerList.size.toString())
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181A26))
            .padding(top = -statusBarHeight)
    ) {
        item(
        ) {
            //TransparentStatusBar()
            HorizontalPager(
                state = pagerState, modifier = Modifier
            ) { index ->
                if (moviesList.isNotEmpty()) {
                    DisplayMovie(movie = moviesList[index], navHostController)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                HorizontalPagerIndicator(
                    pageCount = pageCount,
                    currentPage = pagerState.currentPage,
                    targetPage = pagerState.targetPage
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
        categoryLists.forEach { config ->
            item {
                if (config.movieList.isNotEmpty()) {
                    MoviesListInRows(
                        navHostController = navHostController,
                        title = config.title,
                        movieList = config.movieList,
                        onChevronClick = {
                            navHostController.navigate(Screen.MoviesList.rout + "/${config.title}")
                        },
                        fetchDataFromApi = config.fetchData
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }else{
                    if (floatingActionCategory) {
                        config.fetchData.invoke()
                    }
                }
            }
        }
    }
}


@Composable
fun HorizontalPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    targetPage: Int,
    modifier: Modifier = Modifier,
    indicatorColor: Color = Color.White,
    unselectedIndicatorSize: Dp = 6.dp,
    unselectedColor: Color = Color.LightGray.copy(alpha = 0.5f),
    selectedIndicatorSize: Dp = 8.dp,
    indicatorPadding: Dp = 2.dp
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .wrapContentSize()
            .height(selectedIndicatorSize + indicatorPadding * 2)
    ) {
        repeat(pageCount) { page ->
            val size = if (currentPage == page || targetPage == page) selectedIndicatorSize else unselectedIndicatorSize
            val color = if (currentPage == page) indicatorColor else unselectedColor

            Box(
                modifier = Modifier
                    .padding(horizontal = indicatorPadding)
                    .size(size) // Width and height will be equal for circular shape
                    .clip(CircleShape) // Circle shape for the indicator
                    .background(color) // Background color for the indicator
            )
        }
    }
}

@Composable
fun DisplayMovie(movie : Movie, navHostController: NavHostController) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    val context = LocalContext.current
//    val backDropImageState = rememberAsyncImagePainter(
//        model = ImageRequest.Builder(LocalContext.current)
//            .data(MovieApi.IMAGE_BASE_URL + movie?.backdrop_path)
//            .size(Size.ORIGINAL)
//            .build()
//    ).state
    Column(modifier = Modifier .fillMaxWidth()) {
//        if(backDropImageState is AsyncImagePainter.State.Error){
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(250.dp)
//                    .background(MaterialTheme.colorScheme.primaryContainer),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    modifier = Modifier .size(70.dp),
//                    imageVector = Icons.Rounded.ImageNotSupported,
//                    contentDescription =movie?.title
//                )
//            }
//        }
//        if(backDropImageState is AsyncImagePainter.State.Success){
////            Image(
////                modifier = Modifier
////                    .fillMaxWidth()
////                    .height(250.dp),
////                painter = backDropImageState.painter,
////                contentDescription = movie?.original_title,
////                contentScale = ContentScale.Crop
////
////            )
//            AsyncImage(
//                model = ImageRequest.Builder(context)
//                    .data(MovieApi.IMAGE_BASE_URL + movie.backdrop_path)
//                    .crossfade(true)
//                    .build(),
//                contentDescription = movie.title,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier .height(250.dp),
//                filterQuality = FilterQuality.High
//            )
//        }
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(-statusBarHeight)
        ){
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(MovieApi.IMAGE_BASE_URL + movie.backdrop_path)
                    .crossfade(true)
                    .diskCacheKey(movie.id.toString()+ "backdrop")
                    .memoryCacheKey(movie.id.toString()+ "backdrop")
                    .size(Size.ORIGINAL)
                    .fallback(ColorDrawable(MaterialTheme.colorScheme.primaryContainer.toArgb()))
                    .error(ColorDrawable(MaterialTheme.colorScheme.primaryContainer.toArgb()))
                    .build(),
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()  // Ensures it covers status bar
                    .drawBehind {
                        drawRect(Color.Black) // In case image has transparency
                    }
                    .clickable {
                        val json = Uri.encode(Gson().toJson(movie))
                        navHostController.navigate(Screen.Details.rout + "/$json")
                    },
                filterQuality = FilterQuality.High
            )
        }


        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = movie.title,
            modifier=Modifier .fillMaxWidth(),
            color= Color.White,
            fontSize = 18.sp,
            maxLines = 1,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center

        )
        Spacer(modifier = Modifier.height(6.dp))
        Row( modifier = Modifier .fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Language - "+movie.original_language.uppercase(),
                modifier=Modifier .padding(start=16.dp),
                color= Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold

            )
            Text(
                text = "Rating - " + if (movie.adult) "A" else "UA",
                modifier=Modifier .padding(start=16.dp),
                color= Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text ="Score - ${String.format("%.1f", movie.vote_average)}/10",
                modifier=Modifier .padding(start=16.dp,end=8.dp),
                color= Color.White,
                fontSize = 15.sp,
                maxLines = 1,
                fontWeight = FontWeight.SemiBold
            )
        }


    }
}

@Composable
fun MoviesListInRows(
    navHostController: NavHostController,
    movieList : List<Movie>,
    title : String,
    onChevronClick : () -> Unit,
    fetchDataFromApi : () -> Unit = {}
) {
    var isScrolled by remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()
    val context = LocalContext.current
//    val movieLists = movieList
//        .groupBy { it.id } // Group movies by ID
//        .mapValues { (_, movies) -> movies.maxByOrNull { if (it.isFavorite) 1 else 0 }!! } // Prioritize isFavorite = true
//        .values.toList()
    val movieLists = movieList
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 7.dp, end = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                modifier = Modifier,
                color = Color.White,
                fontSize = 18.sp,
                maxLines = 1,
                fontWeight = FontWeight.Bold
            )
            if (isScrolled) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "chevron",
                    modifier = Modifier.clickable {
                        onChevronClick.invoke()
                    })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(modifier = Modifier.fillMaxWidth(), state = scrollState) {
            items(movieLists.size) { index ->

//                   val imageState = rememberAsyncImagePainter(model = ImageRequest.Builder(LocalContext.current)
//                   .data(MovieApi.IMAGE_BASE_URL+ movieList[index].poster_path)
//                   .size(Size.ORIGINAL)
//                   .build()
//                    ).state
//                   if(imageState is AsyncImagePainter.State.Error){
//                       Box(
//                           modifier = Modifier
//                               .fillMaxWidth()
//                               .padding(2.dp)
//                               .height(250.dp),
//                               //.clip(RoundedCornerShape(22.dp))
//                               //.background(MaterialTheme.colorScheme.primaryContainer),
//                           contentAlignment = Alignment.Center
//                       ) {
//                           Icon(modifier = Modifier .size(40.dp),imageVector = Icons.Rounded.ImageNotSupported, contentDescription =movieList[index].title )
//                       }
//                   }
//                   if(imageState is AsyncImagePainter.State.Success){
////                       Image(
////                           modifier = Modifier
////                               .fillMaxWidth()
////                               .padding(2.dp)
////                               .clip(RoundedCornerShape(5.dp))
////                               .height(175.dp),
////                           painter = imageState.painter,
////                           contentDescription = movieList[index].title,
////                          // contentScale = ContentScale.Crop
////                       )
//                       AsyncImage(
//                           model = ImageRequest.Builder(context)
//                               .data(MovieApi.IMAGE_BASE_URL +  movieList[index].poster_path)
//                               .crossfade(true)
//                               .build(),
//                           contentDescription = movieList[index].title,
//                           modifier = Modifier .padding(end=2.dp)
//                               .clip(RoundedCornerShape(5.dp))
//                               .height(175.dp),
//                           filterQuality = FilterQuality.High
//                       )
//                   }
                MoviePosterItem(
                    movie = movieLists[index],
                    navHostController = navHostController
                )
                if (index >= movieLists.size - 1) {
                    fetchDataFromApi.invoke()
                }

                LaunchedEffect(scrollState) {
                    snapshotFlow { scrollState.firstVisibleItemIndex }
                        .collect {
                            isScrolled = it > 0
                        }
                }

            }
        }
    }
}

@Composable
fun MoviePosterItem(movie : Movie, navHostController: NavHostController) {
    val context = LocalContext.current

    val imageRequest = remember(movie.id) {
        ImageRequest.Builder(context)
            .data(MovieApi.IMAGE_BASE_URL + movie.poster_path)
            .crossfade(true)
            .diskCacheKey("${movie.id}_poster")
            .memoryCacheKey("${movie.id}_poster")
            //.size(120, 185) // Specify exact size needed
            .build()
    }
    var isLoading by remember { mutableStateOf(true) }
    Box(
        modifier = Modifier
            .padding(end = 4.dp)
            .height(185.dp)
            .clip(RoundedCornerShape(5.dp))
            .clickable {
                val json = Uri.encode(Gson().toJson(movie))
                Log.d("moviedata", json)
                navHostController.navigate(Screen.Details.rout + "/$json")
            }
    ) {
        if (isLoading) {
            ShimmerBox(
                modifier = Modifier.fillMaxSize()
            )
        }
        AsyncImage(
            model = imageRequest,
            contentDescription = movie.title,
            modifier = Modifier.fillMaxSize(),
            //contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.High,
            onState = { state ->
                isLoading = state is AsyncImagePainter.State.Loading
            }
        )
    }
}
//
//@Composable
//fun MoviePosterItem(movie : Movie, navHostController: NavHostController) {
//    val context = LocalContext.current
//
//    // Keep your existing ImageRequest configuration
//    val imageRequest = remember(movie.id) {
//        ImageRequest.Builder(context)
//            .data(MovieApi.IMAGE_BASE_URL + movie.poster_path)
//            .crossfade(true)
//            .diskCacheKey("${movie.id}_poster")
//            .memoryCacheKey("${movie.id}_poster")
//            .build()
//    }
//
//    // Use rememberAsyncImagePainter and track its state
//    val painter = rememberAsyncImagePainter(imageRequest)
//    val state = painter.state
//
//    // Control shimmer animation
//    val transition = rememberInfiniteTransition(label = "shimmer")
//    val translateAnimation = transition.animateFloat(
//        initialValue = 0f,
//        targetValue = 1000f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 1000, easing = LinearEasing),
//            repeatMode = RepeatMode.Restart
//        ),
//        label = "shimmer"
//    )
//
//    // Shimmer gradient brush
//    val shimmerBrush = Brush.linearGradient(
//        colors = listOf(
//            Color(0xFF1A1C25),  // Very dark (close to your app background)
//            Color(0xFF2E3142),  // Medium dark
//            Color(0xFF1A1C25)   // Very dark again
//        ),
//        start = androidx.compose.ui.geometry.Offset(
//            x = -translateAnimation.value,
//            y = -translateAnimation.value
//        ),
//        end = androidx.compose.ui.geometry.Offset(
//            x = translateAnimation.value,
//            y = translateAnimation.value
//        )
//    )
//
//    Box(
//        modifier = Modifier
//            .padding(end = 4.dp)
//            .height(185.dp)
//            .clip(RoundedCornerShape(5.dp))
//            .clickable {
//                val json = Uri.encode(Gson().toJson(movie))
//                Log.d("moviedata", json)
//                navHostController.navigate(Screen.Details.rout + "/$json")
//            }
//    ) {
//        // First show the shimmer background
//        if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
//            Spacer(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(shimmerBrush)
//            )
//        }
//
//        // Then draw the image on top once it's loaded
//        if (state is AsyncImagePainter.State.Success) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.Transparent)
//            ) {
//                androidx.compose.foundation.Image(
//                    painter = painter,
//                    contentDescription = movie.title,
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop
//                )
//            }
//        }
//    }
//
////    SubcomposeAsyncImage(
////        model = imageRequest,
////        contentDescription = movie.title,
////        modifier = Modifier
////            .padding(end = 4.dp)
////            .clickable {
////                val json = Uri.encode(Gson().toJson(movie))
////                Log.d(" moviedata", json)
////                navHostController.navigate(Screen.Details.rout + "/$json")
////
//////                            navHostController.navigate(Screen.Details.rout + "/${movieLists[index].id}")
////            }
////            // .width(120.dp)
////            .height(185.dp)
////            .clip(RoundedCornerShape(5.dp)),
////        filterQuality = FilterQuality.High,
////        //contentScale = ContentScale.FillBounds,
////
////    ){
////        when (painter.state) {
////            is AsyncImagePainter.State.Loading -> {
////                // Show shimmer effect while loading
////                Box(
////                    modifier = Modifier
////                        .fillMaxSize()
////                        .shimmerEffect()
////                )
////            }
////
////            is AsyncImagePainter.State.Error -> {
////                // Show placeholder for error
////                Box(
////                    modifier = Modifier
////                        .fillMaxSize()
////                        .background(Color(0xFF383838))
////                )
////            }
////
////            else -> {
////                SubcomposeAsyncImageContent()
////            }
////        }
////    }
//
//}

@Composable
fun ShimmerBox(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        Color(0xFF1F2129),  // Darker
        Color(0xFF383A45),  // Lighter
        Color(0xFF1F2129)   // Back to darker
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(0f, 0f),
        end = Offset(translateAnim.value, translateAnim.value)
    )

    Surface(
        shape = RoundedCornerShape(5.dp),
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .background(brush)
        )
    }
}
data class CategoryListConfig(
    val title: String,
    val movieList: List<Movie>,
    val fetchData: () -> Unit
)