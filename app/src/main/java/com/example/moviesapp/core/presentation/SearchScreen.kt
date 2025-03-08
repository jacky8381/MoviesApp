package com.example.moviesapp.core.presentation

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.moviesapp.details.presentation.DetailsViewModel
import com.example.moviesapp.moviesList.data.remote.MovieApi
import com.example.moviesapp.moviesList.domain.model.Movie
import com.example.moviesapp.presentation.MovieListState
import com.example.moviesapp.presentation.MovieListUiEvent
import com.example.moviesapp.presentation.MoviesListViewModel
import com.example.moviesapp.util.Category
import com.example.moviesapp.util.Chips
import com.example.moviesapp.util.CollapsingLayout
import com.example.moviesapp.util.Screen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.Gson
import kotlin.math.max

@Composable
fun SearchScreen(
    moviesListViewModel: MoviesListViewModel,
    navController: NavHostController,
    detailsViewModel: DetailsViewModel
) {
    var textFieldValue by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

//    val scope = rememberCoroutineScope()
//    var searchJob by remember { mutableStateOf<Job?>(null) }

    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color =Color(0xFF0F1014) // Dark icons when not focused (white background)
    )
    val uiState by moviesListViewModel.movieListState.collectAsState()
    if (textFieldValue.isEmpty()){
        moviesListViewModel.clearSearchedList()
    }
    var currentList by remember { mutableStateOf(uiState.trendingMovieList.distinctBy { it.id }) }
    var currentCategory by remember {
        mutableStateOf("This Week")
    }

    LaunchedEffect(
        currentCategory,
        uiState.trendingMovieList,
        uiState.actionMoviesList,
        uiState.mysteryMoviesList,
        uiState.crimeMoviesList,
        uiState.adventureMoviesList,
        uiState.scifiMoviesList,
        uiState.horrorMoviesList,
        uiState.popularTvList
    ) {
        currentList = when (currentCategory) {
            Chips.THIS_WEEK -> uiState.trendingMovieList
            Chips.POPULAR_SHOWS -> uiState.popularTvList
            Chips.ACTION -> uiState.actionMoviesList
            Chips.MYSTERY -> uiState.mysteryMoviesList
            Chips.CRIME -> uiState.crimeMoviesList
            Chips.ADVENTURE -> uiState.adventureMoviesList
            Chips.SCI_FI -> uiState.scifiMoviesList
            null -> uiState.trendingMovieList
            else -> uiState.trendingMovieList
        }
    }

    Scaffold (
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color(0xFF0F1014))
                    .padding(horizontal = 6.dp)
            ) {
               Spacer(modifier = Modifier .height(20.dp))
                BasicTextField(value = textFieldValue,
                    onValueChange = {newValue->
                        try {
                            textFieldValue = newValue
                            moviesListViewModel.updateLoadingState()
                            moviesListViewModel.updateSearchQuery(newValue)
                        }catch (e : Exception){
                            Log.d("Exception", e.stackTraceToString())
                        }
                    }, modifier = Modifier
                        .focusRequester(focusRequester)
                        .onFocusChanged { isFocused = it.isFocused },
                    cursorBrush = SolidColor(Color.White),
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp
                    )
                ) { innerField->
                    Row (
                        modifier = Modifier
                            .padding(7.dp)
                            .height(45.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(5.dp))
                            .background(if (isFocused) MaterialTheme.colorScheme.inverseOnSurface else Color.White),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Icon(imageVector = Icons.Default.Search,
                            contentDescription = "SearchIcon",
                            tint = if (isFocused) Color.White else Color.DarkGray,
                            modifier = Modifier
                                .padding(5.dp)
                                .size(26.dp))
                        Box (
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .weight(1f)
                        ){
                            innerField()
                            if (textFieldValue.isEmpty()) {
                                Text(
                                    text = "Search for any `movies`",
                                    modifier = Modifier,
                                    color = if (isFocused) Color(0xFF999DA0) else Color(0xFFC0C0C0),
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                )
                            }
                        }
                        if(textFieldValue.isNotEmpty()){
                            Icon(imageVector = Icons.Filled.Cancel,
                                contentDescription = "SearchIcon",
                                tint = Color.White, modifier =
                                Modifier
                                    .clickable {
                                        textFieldValue = ""
                                    }
                                    .padding(7.dp)
                                    .size(26.dp)
                            )
                        }
                    }
                }
            }
        }
    ){it->
            CollapsingLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(Color(0xFF0F1014)),
                bodyContent = {
                    if (textFieldValue.isEmpty()) {
                        CollapsingBody(
                            uiState,
                            Chips.categories,
                            currentList,
                            navHostController = navController,
                            context = context,
                            selectedString = {value->
                                currentCategory = value
                                 when(value){
                                    Chips.THIS_WEEK->{
                                            currentList =  uiState.trendingMovieList.distinctBy { it.id }
                                            if (uiState.trendingMovieList.isEmpty()){
                                                moviesListViewModel.onEvent(MovieListUiEvent.Paginate(Category.TRENDING))
                                            }
                                        }
                                     Chips.POPULAR_SHOWS->{
                                            currentList = uiState.popularTvList.distinctBy { it.id }
                                            if (uiState.popularTvList.isEmpty()){
                                                moviesListViewModel.onEvent(MovieListUiEvent.Paginate(Category.POPULAR_TV))
                                            }
                                        }
                                     Chips.ACTION->{
                                            currentList = uiState.actionMoviesList.distinctBy { it.id }
                                            if (uiState.actionMoviesList.isEmpty()){
                                                moviesListViewModel.onEvent(MovieListUiEvent.Paginate(Category.ACTION))
                                            }
                                        }
                                     Chips.MYSTERY->{
                                            currentList = uiState.mysteryMoviesList.distinctBy { it.id }
                                            if (uiState.mysteryMoviesList.isEmpty()){
                                                moviesListViewModel.onEvent(MovieListUiEvent.Paginate(Category.MYSTERY))
                                            }
                                        }
                                     Chips.CRIME->{
                                            currentList = uiState.crimeMoviesList.distinctBy { it.id }
                                            if (uiState.crimeMoviesList.isEmpty()){
                                                moviesListViewModel.onEvent(MovieListUiEvent.Paginate(Category.CRIME))
                                            }
                                        }
                                     Chips.ADVENTURE->{
                                            currentList = uiState.adventureMoviesList.distinctBy { it.id }
                                            if (uiState.adventureMoviesList.isEmpty()){
                                                moviesListViewModel.onEvent(MovieListUiEvent.Paginate(Category.ADVENTURE))
                                            }
                                        }
                                     Chips.SCI_FI->{
                                            currentList = uiState.scifiMoviesList.distinctBy { it.id }
                                            if (uiState.scifiMoviesList.isEmpty()){
                                                moviesListViewModel.onEvent(MovieListUiEvent.Paginate(Category.SCI_FI))
                                            }
                                        }
                                }
                            },
                            updateSearchFlag = {
                                detailsViewModel.updateFromSearchScreen(true)
                            }
                        )
                    }else{
                        if (uiState.searchedMoviesList.isNotEmpty()) {
                            SearchedListItems(
                                list = uiState.searchedMoviesList,
                                context = context,
                                navController,
                                moviesListViewModel
                            ) {
                                detailsViewModel.updateFromSearchScreen(true)
                            }
                        }else if (uiState.isLoading){
                            Box (
                                modifier = Modifier .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ){
                                CircularProgressIndicator()
                            }
                        }else{
                            NoRecordFoundCompose()
                        }
                    }
                },
                collapsingTop = {
                    if (textFieldValue.isEmpty()) {
                        CollapsingHeader(
                            uiState.searchedDBList,
                            context = context,
                            navHostController = navController,

                         {
                            moviesListViewModel.clearSearchHistory()
                        },
                            updateSearchFlag = {
                                detailsViewModel.updateFromSearchScreen(true)
                            }
                        )
                    }
                }
            )
    }
}

@Composable
fun CollapsingHeader(moviesList : List<Movie>,context: Context, navHostController : NavHostController, clearSearchHistory : () -> Unit, updateSearchFlag : () -> Unit) {
    Column (
        modifier = Modifier
        .padding(horizontal = 13.dp)
    ) {
        if (moviesList.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Recent Searches",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Clear All",
                    modifier = Modifier.clickable {
                        clearSearchHistory.invoke()
                    },
                    color = Color(0xff0096FF),
                    fontSize = 14.sp,
                )

            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(moviesList.size) {
                    Column(
                        modifier = Modifier.width(140.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(MovieApi.IMAGE_BASE_URL + moviesList[it].backdrop_path)
                                .crossfade(true)
                                .diskCacheKey(moviesList[it].backdrop_path.toString() + "poster1")
                                .memoryCacheKey(moviesList[it].backdrop_path.toString() + "poster1")
                                .fallback(ColorDrawable(MaterialTheme.colorScheme.primaryContainer.toArgb()))
                                .error(ColorDrawable(MaterialTheme.colorScheme.primaryContainer.toArgb()))
                                //.size(Size.ORIGINAL)
                                .build(),
                            contentDescription = moviesList[it].title,
                            modifier = Modifier
                                .width(140.dp)
                                .height(75.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .clickable {
                                    val json = Uri.encode(Gson().toJson(moviesList[it]))
                                    navHostController.navigate(Screen.Details.rout + "/$json")
                                    updateSearchFlag.invoke()
                                    // navHostController.navigate(Screen.Details.rout + "/${moviesList[it].id}")
                                },
                            filterQuality = FilterQuality.High,
                            contentScale = ContentScale.FillBounds,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = moviesList[it].title,
                            color = Color.White,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(35.dp))

        Text(
            text = "Trending in",
            color = Color.White,
            fontSize = 18.sp,
            maxLines = 1,
            fontWeight = FontWeight.ExtraBold,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(5.dp))

    }
}
@Composable
fun CollapsingBody(
    uiState: MovieListState,
    listOfChips : List<String>,
    movieLists : List<Movie>,
    navHostController : NavHostController,
    context: Context,
    selectedString : (String) -> Unit,
    updateSearchFlag : () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val minColumnWidth = 120.dp
    val columns = max(2, (screenWidth / minColumnWidth).toInt())
    var scrollIndex by remember {
        mutableIntStateOf(0)
    }
    var selectedChip by remember {
        mutableStateOf(listOfChips[0])
    }
    Column {
        LazyRow(modifier = Modifier .padding(13.dp),  horizontalArrangement = Arrangement.spacedBy(9.dp)){
            items(listOfChips.size) {
                Surface(
                    shape = RoundedCornerShape(7.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .background(
                                if (selectedChip != listOfChips[it]) MaterialTheme.colorScheme.inverseOnSurface else Color(
                                    0xFF27282D
                                )
                            )
                            .border(
                                BorderStroke(
                                    width = 1.dp,
                                    color = if (selectedChip != listOfChips[it]) MaterialTheme.colorScheme.inverseOnSurface else Color(
                                        0xFF999DA0
                                    ),
                                ),
                                shape = RoundedCornerShape(7.dp)
                            )

                            .clickable {
                                scrollIndex = 0
                                selectedString.invoke(listOfChips[it])
                                selectedChip = listOfChips[it]
                            },
                    ) {
                        Text(
                            text = listOfChips[it],
                            modifier = Modifier .padding(6.dp),
                            color =  if (selectedChip != listOfChips[it]) Color(0xFF999DA0) else Color.White,
                            fontSize = 16.sp,
                            maxLines = 1,
                        )

                    }
                }
            }
        }
        if (!uiState.isLoading) {

            LazyVerticalStaggeredGrid(
                state = LazyStaggeredGridState(scrollIndex),
                modifier = Modifier.fillMaxSize(),
                columns = StaggeredGridCells.Fixed(columns),
                verticalItemSpacing = 4.dp,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                content = {
                    items(movieLists.size) { index ->
                        val aspectRatio = remember(index, movieLists.size) {
                            when ((index + movieLists.size) % 7) {
                                0 -> 2f / 3f  // Standard poster
                                1 -> 1f / 2f  // Taller
                                2 -> 3f / 4f  // Shorter
                                3 -> 3f / 5f  // Even taller
                                4 -> 4f / 5f  // Almost square
                                5 -> 2f / 4f  // Very tall
                                else -> 3f / 4f
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .clickable {
                                    val json = Uri.encode(Gson().toJson(movieLists[index]))

                                    navHostController.navigate(Screen.Details.rout + "/$json")
                                    updateSearchFlag.invoke()
                                    //navHostController.navigate(Screen.Details.rout + "/${movieLists[index].id}")
                                }
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(MovieApi.IMAGE_BASE_URL + movieLists[index].poster_path)
                                    .crossfade(true)
                                    .diskCacheKey(movieLists[index].id.toString() + "poster")
                                    .memoryCacheKey(movieLists[index].id.toString() + "poster")
                                    .fallback(ColorDrawable(MaterialTheme.colorScheme.primaryContainer.toArgb()))
                                    .error(ColorDrawable(MaterialTheme.colorScheme.primaryContainer.toArgb()))
                                    //.size(Size.ORIGINAL)
                                    .build(),
                                contentDescription = movieLists[index].title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(
                                        ratio = aspectRatio,
                                        matchHeightConstraintsFirst = false
                                    ),
                                filterQuality = FilterQuality.High,
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                },
            )
        }else{
            Box (
                modifier = Modifier .fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()

            }
        }
    }
}

@Composable
fun SearchedListItems(list : List<Movie>, context : Context, navHostController : NavHostController, moviesListViewModel: MoviesListViewModel, updateSearchFlag : () -> Unit) {
    LazyVerticalGrid(columns = GridCells.Fixed(2) ,
        contentPadding = PaddingValues(4.dp)
    ){
        items(list){movie->
            Column(
                modifier = Modifier .padding(vertical = 6.dp, horizontal = 3.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(MovieApi.IMAGE_BASE_URL + movie.backdrop_path)
                        .crossfade(true)
                        .diskCacheKey(movie.id.toString() + "poster2")
                        .memoryCacheKey(movie.id.toString() + "poster2")
                        .fallback(ColorDrawable(MaterialTheme.colorScheme.primaryContainer.toArgb()))
                        .error(ColorDrawable(MaterialTheme.colorScheme.primaryContainer.toArgb()))
                        //.size(Size.ORIGINAL)
                        .build(),
                    contentDescription = movie.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .clickable {
                            moviesListViewModel.updateDBForSearchItems(movie)
                            val json = Uri.encode(Gson().toJson(movie))
                            navHostController.navigate(Screen.Details.rout + "/$json")
                            updateSearchFlag.invoke()

                            // navHostController.navigate(Screen.Details.rout + "/${movie.id}")
                        },
                    filterQuality = FilterQuality.High,
                    contentScale = ContentScale.FillBounds,
                )
                Spacer(modifier = Modifier .height(3.dp))
                Text(
                    text = movie.title,
                    modifier = Modifier,
                    color = Color.White,
                    fontSize = 13.sp,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier .height(2.dp))
                Row {
                    Text(
                        text = movie.release_date.split("-").firstOrNull()
                            ?: "Unknown",
                        modifier = Modifier,
                        color = Color(0xFF999DA0),
                        fontSize = 11.sp,
                        maxLines = 1,
                    )
                    Spacer(modifier = Modifier .width(5.dp))
                    Text(
                        text =  "language: " + movie.original_language,
                        modifier = Modifier,
                        color = Color(0xFF999DA0),
                        fontSize = 11.sp,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
fun NoRecordFoundCompose() {
    Column (
        modifier = Modifier .fillMaxSize(),
       verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        val infiniteTransition = rememberInfiniteTransition()
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )

        Icon(
            imageVector = Icons.Outlined.SearchOff,
            contentDescription = "No results found",
            modifier = Modifier
                .size(80.dp)
                .scale(scale),
            tint = MaterialTheme.colorScheme.inverseOnSurface
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Results Found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC0C0C0)

        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}