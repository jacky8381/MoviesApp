package com.example.moviesapp.core.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moviesapp.presentation.MovieListUiEvent
import com.example.moviesapp.presentation.MoviesListViewModel
import com.example.moviesapp.util.Screen
//import java.lang.reflect.Modifier

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen( navController: NavHostController, moviesListViewModel : MoviesListViewModel) {
    val movieState by moviesListViewModel.movieListState.collectAsState()
    val bottomNavController = rememberNavController()
    val listOfString = listOf("TV", "Movies","More")
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        bottomBar = {
          //  BottomNavigationBar(bottomNavController = bottomNavController , onEvent = moviesListViewModel ::onEvent , moviesListViewModel)
        },
        floatingActionButton = {
            if (currentRoute == Screen.PopularMovieList.rout) {
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(7.dp),
                    shape = RoundedCornerShape(35.dp),
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(top = 15.dp, bottom = 15.dp, start = 22.dp, end = 22.dp)
                            .height(25.dp)
                            .width(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        listOfString.forEachIndexed { index, it ->
                            Text(
                                text = it,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    //.weight(1f)
                                    .clickable {

                                    },
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            if (index < listOfString.size - 1) {
                                Spacer(modifier = Modifier.width(12.dp))
                                Divider(
                                    color = Color.White,
                                    thickness = 0.2.dp,
                                    modifier = Modifier
                                        .height(20.dp)
                                        .width(0.5.dp)
                                    //.padding(horizontal = 4.dp)

                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        }
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
//        topBar = {
//            TopAppBar(
//                title = {
//                Text(
//                    text = if(movieState.isCurrentPopularScreen)
//                        stringResource(R.string.popular_movies)
//                    else
//                        stringResource(R.string.upcoming_movies),
//                    fontSize = 20.sp,
//
//                )
//            },
//                modifier = Modifier .shadow(2.dp),
//                colors = TopAppBarDefaults.topAppBarColors(
//                    MaterialTheme.colorScheme.inverseOnSurface
//                )
//            )
//
//        },
        modifier = Modifier.fillMaxSize() , // Remove system bars padding

    ) {it->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(it)
        ) {
            NavHost(navController = bottomNavController, startDestination = Screen.PopularMovieList.rout){
                composable(Screen.PopularMovieList.rout){
//                    PopularMovieScreen(
//                        navController=navController,
//                        movieListState = movieState,
//                        onEvent = moviesListViewModel::onEvent,
//                        onFloatingActionClick = {}
//                        )
                   // Dashboard(movieState = movieState, moviesListViewModel, navHostController = navController)
                }
                composable(Screen.UpcomingMovieList.rout){
                    UpcomingMovieScreen(
                        movieListState = movieState,
                        navController = navController,
                        onEvent = moviesListViewModel::onEvent
                    )
                }
                composable(Screen.SearchList.rout){
                  //  SearchScreen(moviesListViewModel = moviesListViewModel, navController)
                }
                composable(Screen.FavoriteList.rout){
                    FavoritesScreen(moviesListViewModel =moviesListViewModel , navController =navController)
                }
            }

        }

    }
}

@Composable
fun BottomNavigationBara(
    bottomNavController : NavHostController,
    onEvent : (MovieListUiEvent) -> Unit,
    moviesListViewModel: MoviesListViewModel
){
    val items= listOf(
        BottomItem("Home", Icons.Outlined.Home,Icons.Filled.Home, Screen.PopularMovieList.rout),
        BottomItem("Search", Icons.Outlined.Search,Icons.Filled.Search, Screen.SearchList.rout),
        BottomItem("Favorite",Icons.Outlined.FavoriteBorder,Icons.Filled.Favorite, Screen.FavoriteList.rout)
    )

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavigationBar {
        Row(
            modifier = Modifier .background(Color(0xFF2D2F3B))
        ) {
            items.forEachIndexed { index, bottomItem ->
                val isSelected = currentRoute == bottomItem.route

                NavigationBarItem(
                    selected =isSelected,
                    onClick = {
                        if (!isSelected) {
                            when (index) {
                                0 -> {
                                    onEvent(MovieListUiEvent.Navigate)
                                    //bottomNavController.popBackStack()
                                    bottomNavController.navigate(Screen.PopularMovieList.rout) {
                                        popUpTo(bottomNavController.graph.findStartDestination().id) {
                                            saveState = true

                                        }
                                        restoreState = true
                                        launchSingleTop = true

                                    }

                                }
                                1 -> {
                                    onEvent(MovieListUiEvent.Navigate)
                                    moviesListViewModel.getTrendingMovieList(true)
                                    //bottomNavController.popBackStack()
                                    bottomNavController.navigate(Screen.SearchList.rout) {
                                        popUpTo(bottomNavController.graph.findStartDestination().id) {
                                            saveState = true

                                        }
                                        restoreState = true
                                        launchSingleTop = true

                                    }

                                }
                                2 -> {
                                    onEvent(MovieListUiEvent.Navigate)
                                    //bottomNavController.popBackStack()
                                    bottomNavController.navigate(Screen.FavoriteList.rout) {
                                        popUpTo(bottomNavController.graph.findStartDestination().id) {
                                            saveState = true

                                        }
                                        restoreState = true
                                        launchSingleTop = true
                                    }
                                }
                            }
                        }

                    },
                    icon = {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isSelected) bottomItem.selectedIcon else bottomItem.icon,
                                contentDescription = bottomItem.title,
                                tint =  Color(0xFFE0E2Ef)
                            )
                        }
                    },
                    label = {
                        Text(text = bottomItem.title, color = Color(0xFFE0E2Ef), textAlign= TextAlign.Center)
                    }
                )
            }

        }
    }
}

data class BottomItema(
    val title : String,
    val icon : ImageVector,
    val selectedIcon : ImageVector,
    val route : String
)