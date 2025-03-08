package com.example.moviesapp.moviesList.data.local.movie

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.moviesapp.moviesList.data.local.TvEntity
import com.example.moviesapp.moviesList.data.remote.respond.Genre
import kotlinx.coroutines.flow.Flow


@Dao
interface MovieDao {
    @Upsert
    suspend fun upsertMovieList(movieList: List<MovieEntity>)

    @Upsert
    suspend fun upsertTvList(movieList: List<TvEntity>)

    @Query("Select * from MovieEntity where id= :id")
    suspend fun getMovieById(id : Int): MovieEntity

    @Query("Select * from MovieEntity where category = :category")
    fun getMovieByCategory(category: String): Flow<List<MovieEntity>>

    @Query("Select * from TvEntity where category = :category")
    fun geTvListByCategory(category: String): Flow<List<TvEntity>>

    @Query("Select * from TvEntity where category = :category")
    fun getTvBCategory(category: String): Flow<List<TvEntity>>

    @Query("Select * from GenreEntity")
    suspend fun getGenreList() : List<GenreEntity>

    @Upsert
    suspend fun  upsertGenreList(genreList : List<GenreEntity>)

    @Query("Select id from GenreEntity where name= :name and category=:category")
    fun getGenreID(name : String, category: String) : Flow<Int?>

    @Query("SELECT name FROM GenreEntity WHERE id IN (:genreIds)")
    suspend fun getGenreNamesByIds(genreIds: List<Int>): List<String>
}
