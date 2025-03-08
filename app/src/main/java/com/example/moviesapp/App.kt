package com.example.moviesapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class SApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}