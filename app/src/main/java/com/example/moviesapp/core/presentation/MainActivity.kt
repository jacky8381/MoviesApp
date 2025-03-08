package com.example.moviesapp.core.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moviesapp.R
import com.example.moviesapp.details.presentation.DetailsScreen
import com.example.moviesapp.details.presentation.DetailsViewModel
import com.example.moviesapp.presentation.MovieListUiEvent
import com.example.moviesapp.presentation.MoviesListViewModel
import com.example.moviesapp.ui.theme.MoviesAppTheme
import com.example.moviesapp.util.Category
import com.example.moviesapp.util.Screen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
       // WindowCompat.setDecorFitsSystemWindows(window, false)
       // enableEdgeToEdge()
        setContent {
            val moviesViewModel = hiltViewModel<MoviesListViewModel>()
            val movieState = moviesViewModel.movieListState.collectAsState().value
            val detailsViewModel = hiltViewModel<DetailsViewModel>()
            val detailsState = detailsViewModel.detailsState.collectAsState().value

//            enableEdgeToEdge(
//                statusBarStyle = SystemBarStyle.light(
//                    Color.TRANSPARENT, Color.TRANSPARENT
//                )
//            )
            MoviesAppTheme {
                // A surface container using the 'background' color from the theme Color(0xFF181A26)
               SetBarColor(color =Color(0xFF181A26) )
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val listOfString = listOf(Category.HOME_CATEGORY_TV, Category.HOME_CATEGORY_MOVIE)
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    var selectedButton by remember{
                        mutableStateOf("")
                    }
                    Scaffold(
                        bottomBar = {
                            if (currentRoute==Screen.Home.rout || currentRoute==Screen.SearchList.rout || currentRoute==Screen.FavoriteList.rout) {
                                BottomNavigationBar(
                                    bottomNavController = navController,
                                    onEvent = moviesViewModel::onEvent,
                                    moviesViewModel,
                                    detailsViewModel = detailsViewModel
                                ) {
                                    selectedButton = ""
                                }
                            }
                        },
                        floatingActionButton = {
                            if (currentRoute == Screen.Home.rout) {
                                if (selectedButton.isNotEmpty()) {
                                    Row {
                                        ElevatedCard(
                                            elevation = CardDefaults.cardElevation(7.dp),
                                            shape = RoundedCornerShape(35.dp),
                                            modifier = Modifier.wrapContentWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .padding(vertical = 10.dp, horizontal = 22.dp)
                                                    .height(25.dp)
                                                    .width(IntrinsicSize.Max),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Text(
                                                    text = selectedButton,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier
                                                    //.weight(1f)
                                                    ,
                                                    color = Color.White,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier .width(10.dp))

                                            Icon(painter = painterResource(id = R.drawable.ic_cancel_custom),
                                                modifier = Modifier
                                                    .padding(top = 9.dp)
                                                    .size(27.dp)
                                                    .clickable {
                                                        selectedButton = ""
                                                    },
                                                 contentDescription = "cancel",
                                                tint = Color.Unspecified
                                            )
                                    }
                                } else {
                                    ElevatedCard(
                                        elevation = CardDefaults.cardElevation(7.dp),
                                        shape = RoundedCornerShape(35.dp),
                                        modifier = Modifier.wrapContentWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .padding(vertical = 10.dp, horizontal = 22.dp)
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
                                                            selectedButton = it
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
                            }
                        },
                        floatingActionButtonPosition = FabPosition.Center,
                        modifier = Modifier.fillMaxSize() , // Remove system bars padding
                    ) {it->
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                        ) {
                            NavHost(navController = navController, startDestination = Screen.Splash.rout){
                                composable(Screen.Splash.rout){
                                    SplashScreen(navController)
                                }
                                composable(Screen.Home.rout){
                                    Dashboard(selectedButton = selectedButton,movieState = movieState, moviesViewModel, navHostController = navController)
                                }
                                composable(Screen.SearchList.rout){
                                    SearchScreen(moviesListViewModel = moviesViewModel, navController, detailsViewModel =detailsViewModel)
                                }
                                composable(Screen.FavoriteList.rout){
                                    FavoritesScreen(moviesListViewModel =moviesViewModel , navController =navController)
                                }
                                composable(Screen.Details.rout + "/{movie}",
                                    arguments = listOf(
                                        navArgument("movie"){type= NavType.StringType}
                                    )
                                ){backStackEntry->
                                    DetailsScreen(navController, moviesViewModel, detailsViewModel, detailsState)
                                }
                                composable(Screen.MoviesList.rout + "/{genreName}",
                                    arguments = listOf(
                                        navArgument("genreName"){type = NavType.StringType}
                                    )
                                ) { backStackEntry->
                                    val genreName = backStackEntry.arguments?.getString("genreName") ?: ""
                                    moviesViewModel.setListForMoviesListPage(genreName)
                                    MovieListPage(navController = navController, headerTitle = genreName, movieListViewModel = moviesViewModel, movieState)
                                }
                            }

                        }

                    }
                }
            }
        }
    }
    @Composable
    fun SetBarColor(color : androidx.compose.ui.graphics.Color){
        val systemUiController = rememberSystemUiController()
        LaunchedEffect(key1 = color){
            systemUiController.setSystemBarsColor(color)
        }
        

    }


}


@Composable
fun BottomNavigationBar(
    bottomNavController : NavHostController,
    onEvent : (MovieListUiEvent) -> Unit,
    moviesListViewModel: MoviesListViewModel,
    detailsViewModel : DetailsViewModel,
    onClick : () -> Unit
){
    val items= listOf(
        BottomItem("Home", Icons.Outlined.Home, Icons.Filled.Home, Screen.Home.rout),
        BottomItem("Search", Icons.Outlined.Search, Icons.Filled.Search, Screen.SearchList.rout),
        BottomItem("Favorite", Icons.Outlined.FavoriteBorder, Icons.Filled.Favorite, Screen.FavoriteList.rout)
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
                                    onClick.invoke()
                                    detailsViewModel.updateFromSearchScreen(false)
                                    onEvent(MovieListUiEvent.Navigate)
                                    //bottomNavController.popBackStack()
                                    bottomNavController.navigate(Screen.Home.rout) {
                                        popUpTo(bottomNavController.graph.findStartDestination().id) {
                                            saveState = true

                                        }
                                        restoreState = true
                                        launchSingleTop = true

                                    }

                                }
                                1 -> {
                                    onClick.invoke()
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
                                    onClick.invoke()
                                    detailsViewModel.updateFromSearchScreen(false)
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

data class BottomItem(
    val title : String,
    val icon : ImageVector,
    val selectedIcon : ImageVector,
    val route : String
)