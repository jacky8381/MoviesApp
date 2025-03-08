package com.example.moviesapp.moviesList.data.local.movie

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.moviesapp.moviesList.data.local.TvEntity


@Database(
    entities = [MovieEntity::class, GenreEntity::class, SearchEntity::class, FavoriteEntity::class, TvEntity :: class],
    version = 1
)
abstract class MovieDatabase : RoomDatabase() {
    abstract val movieDao : MovieDao
    abstract val searchDao : SearchDao
    abstract val favoriteDao : FavoriteDao
}