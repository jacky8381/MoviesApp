package com.example.moviesapp.moviesList.data.local.movie

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GenreEntity (
    @PrimaryKey(autoGenerate = true)
    val entityId: Int = 0,
    val id: Int,
    val name: String,
    val category : String
)