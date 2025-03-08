package com.example.moviesapp.moviesList.data.local.movie

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Upsert
    suspend fun insertIntoFavoriteList(movie: FavoriteEntity)

    @Query("Select * from FavoriteEntity")
    fun getFavoriteList(): Flow<List<FavoriteEntity>>

    @Query("delete from FavoriteEntity where id = :id")
    suspend fun deleteFromFavorite( id : Int)

    @Query("update MovieEntity SET isFavorite = :isFavorite where id = :id")
    suspend fun updateMovieEntity(isFavorite : Boolean, id : Int)

    @Query("delete from FavoriteEntity")
    suspend fun deleteFavoriteHistory()

}