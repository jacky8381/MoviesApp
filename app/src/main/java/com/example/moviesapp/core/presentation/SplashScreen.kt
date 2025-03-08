package com.example.moviesapp.core.presentation

import android.animation.Animator
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.airbnb.lottie.LottieAnimationView
import com.example.moviesapp.R
import com.example.moviesapp.util.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val ANIMATION_URL = "https://lottie.host/96b62701-00b8-4a1d-b752-d49c8d322aad/L3MV5fiEX1.json"
@Composable
fun SplashScreen(navController: NavHostController) {
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // Create LottieAnimationView once and reuse
    val lottieAnimationView = remember {
        LottieAnimationView(context).apply {
            // Set layout params to match parent
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

            // Configure animation
            repeatCount = 0
            scaleType = ImageView.ScaleType.FIT_CENTER
            enableMergePathsForKitKatAndAbove(true)
        }
    }

    // LaunchedEffect to load animation and manage navigation
    LaunchedEffect(key1 = true) {
        try {
            // Preload the animation
            lottieAnimationView.setAnimationFromUrl(ANIMATION_URL)

            // Add listener to handle navigation
            lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    isLoading = false
                    navigateToHome(navController)

                }

                override fun onAnimationCancel(animation: Animator) {
                    isLoading = false
                    navigateToHome(navController)

                }

                override fun onAnimationRepeat(animation: Animator) {}
            })

            // Failure listener
            lottieAnimationView.setFailureListener {
                isLoading = false
                navigateToHome(navController)

            }
        } catch (e: Exception) {
            isLoading = false
            navigateToHome(navController)
        }
    }

    // Render Lottie Animation
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = {
                lottieAnimationView.apply {
                    playAnimation()
                }
            },
            modifier = Modifier.size(250.dp)
        )
    }
}

private fun navigateToHome(navController: NavHostController) {
    navController.navigate(Screen.Home.rout) {
        popUpTo(Screen.Splash.rout) { inclusive = true }
    }
}