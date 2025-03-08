package com.example.moviesapp.moviesList.data.local.movie

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {

    @Upsert
    suspend fun insertIntoSearchList(movie: SearchEntity)

    @Query("Select * from SearchEntity")
    fun getSearchList(): Flow<List<SearchEntity>>

    @Query("delete from SearchEntity")
    suspend fun deleteSeachHistory()
}