package com.example.moviesapp.moviesList.data.repository

import android.util.Log
import androidx.compose.ui.text.capitalize
import com.example.moviesapp.moviesList.data.local.movie.MovieDatabase
import com.example.moviesapp.moviesList.data.local.movie.SearchEntity
import com.example.moviesapp.moviesList.data.mappers.toFavoriteMovieEntity
import com.example.moviesapp.moviesList.data.mappers.toGenre
import com.example.moviesapp.moviesList.data.mappers.toGenreEntity
import com.example.moviesapp.moviesList.data.mappers.toGenreTvEntity
import com.example.moviesapp.moviesList.data.mappers.toMovie
import com.example.moviesapp.moviesList.data.mappers.toMovieEntity
import com.example.moviesapp.moviesList.data.mappers.toSearchMovieEntity
import com.example.moviesapp.moviesList.data.mappers.toTvEntity
import com.example.moviesapp.moviesList.data.remote.MovieApi
import com.example.moviesapp.moviesList.data.remote.respond.Genre
import com.example.moviesapp.moviesList.data.remote.respond.GenreDto
import com.example.moviesapp.moviesList.domain.model.GenreModel
import com.example.moviesapp.moviesList.domain.model.Movie
import com.example.moviesapp.moviesList.domain.model.Tv
import com.example.moviesapp.moviesList.domain.repository.MovieListRepository
import com.example.moviesapp.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class MovieListRepositoryImpl @Inject constructor(
    private val movieApi: MovieApi,
    private val movieDatabase : MovieDatabase,
) : MovieListRepository  {

    override suspend fun getMovieList(
        foreFetchFromRemote: Boolean,
        category: String,
        page: Int
    ): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading(true))
            if (!foreFetchFromRemote) {
                val localMovieList = movieDatabase.movieDao.getMovieByCategory(category).first()
                val shouldLoadLocalMovie = localMovieList.isNotEmpty()
                if (shouldLoadLocalMovie) {
                            emit(Resource.Success(
                                data = localMovieList.map { movieEntity ->
                                    movieEntity.toMovie(category)
                                }
                            ))
                            Log.d("MovieList", "$category   $localMovieList")
                            Log.d("ListInfoSize", "In Db $category   ${localMovieList.size}")

                            emit(Resource.Loading(false))
                            return@flow
                }
            }

            val moviesListFromAPI = try {
                movieApi.getMoviesList(category,page)
            } catch (e: IOException) {
                Log.d("APIError", e.stackTraceToString())
                e.printStackTrace()
                emit(Resource.Error(message = "Error loading movies"))
                return@flow
            } catch (e: Exception) {
                Log.d("APIError", e.stackTraceToString())

                e.printStackTrace()
                emit(Resource.Error(message = "Error loading movies"))
                return@flow
            }
            val movieEntities = moviesListFromAPI.results.let {
                it.map {movieDto ->
                    movieDto.toMovieEntity(category)
                }
            }
            Log.d("MovieList", "$category   $movieEntities")

            Log.d("ListInfoSize", "$category   ${movieEntities.size}")


            movieDatabase.movieDao.upsertMovieList(movieEntities)

            emit(Resource.Success(
                movieEntities.map { it.toMovie(category) }
            ))
            emit(Resource.Loading(false))


        }
    }

    override suspend fun getTvList(
        foreFetchFromRemote: Boolean,
        category: String,
        page: Int
    ): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading(true))
            val localMovieList = movieDatabase.movieDao.geTvListByCategory("$category tv").first()
            val shouldLoadLocalMovie = localMovieList.isNotEmpty() && !foreFetchFromRemote

            if(shouldLoadLocalMovie){
                emit(Resource.Success(
                    data = localMovieList.map {movieEntity->
                        movieEntity.toMovie("$category tv")
                    }
                ))
                Log.d("MovieList", "tv $category   $localMovieList")

                emit(Resource.Loading(false))
                return@flow
            }

            val moviesListFromAPI = try {
                movieApi.getTvList(category,page)
            } catch (e: IOException) {
                Log.d("APIError", e.stackTraceToString())
                e.printStackTrace()
                emit(Resource.Error(message = "Error loading movies"))
                return@flow
            } catch (e: Exception) {
                Log.d("APIError", e.stackTraceToString())

                e.printStackTrace()
                emit(Resource.Error(message = "Error loading movies"))
                return@flow
            }
            val movieEntities = moviesListFromAPI.results.let {
                it.map {movieDto ->
                    movieDto.toTvEntity("$category tv")
                }
            }
            Log.d("MovieList", "$category   $movieEntities")

            movieDatabase.movieDao.upsertTvList(movieEntities)

            emit(Resource.Success(
                movieEntities.map { it.toMovie("$category tv") }
            ))
            emit(Resource.Loading(false))


        }
    }

    override suspend fun getMovie(id: Int): Flow<Resource<Movie>> {
        return flow {

            emit(Resource.Loading(true))

            val movieEntity = movieDatabase.movieDao.getMovieById(id)

            if (movieEntity != null) {
                emit(
                    Resource.Success(movieEntity.toMovie(movieEntity.category))
                )

                emit(Resource.Loading(false))
                return@flow
            }

            emit(Resource.Error("Error no such movie"))

            emit(Resource.Loading(false))


        }
    }

    override suspend fun getGenreMovieList(): Flow<Resource<List<GenreModel>>> {
        return flow {
            emit(Resource.Loading(true))
            val localGenreList = movieDatabase.movieDao.getGenreList()

            if (localGenreList.isNotEmpty()){
                emit(Resource.Success(
                    data = localGenreList.map {genreEntity->
                        genreEntity.toGenre()
                    }
                ))
                emit(Resource.Loading(false))
                return@flow
            }

            val moviesGenres = try {
               movieApi.getGenreList("movie")
           } catch (e: IOException) {
               Log.d("APIError", e.stackTraceToString())
               e.printStackTrace()
               emit(Resource.Error(message = "Error loading movies"))
               return@flow
           } catch (e: Exception) {
               Log.d("APIError", e.stackTraceToString())

               e.printStackTrace()
               emit(Resource.Error(message = "Error loading movies"))
               return@flow
           }

            val genreEntity = moviesGenres.genres.let {
                it.map {genreDto->
                    genreDto.toGenreEntity("movie")
                }
            }

            movieDatabase.movieDao.upsertGenreList(genreEntity)
            emit(Resource.Success(genreEntity.map {
                it.toGenre()
            }))
            emit(Resource.Loading(false))
        }
    }

    override suspend fun getGenreTvList(): Flow<Resource<List<GenreModel>>> {
        return flow {
            emit(Resource.Loading(true))
            val localGenreList = movieDatabase.movieDao.getGenreList()

            if (localGenreList.isNotEmpty()){
                emit(Resource.Success(
                    data = localGenreList.map {genreEntity->
                        genreEntity.toGenre()
                    }
                ))
                emit(Resource.Loading(false))
                return@flow
            }

            val moviesGenres = try {
                movieApi.getGenreList("tv")
            } catch (e: IOException) {
                Log.d("APIError", e.stackTraceToString())
                e.printStackTrace()
                emit(Resource.Error(message = "Error loading movies"))
                return@flow
            } catch (e: Exception) {
                Log.d("APIError", e.stackTraceToString())

                e.printStackTrace()
                emit(Resource.Error(message = "Error loading movies"))
                return@flow
            }

            val genreEntity = moviesGenres.genres.let {
                it.map {genreDto->
                    genreDto.toGenreTvEntity("tv")
                }
            }

            movieDatabase.movieDao.upsertGenreList(genreEntity)
            emit(Resource.Success(genreEntity.map {
                it.toGenre()
            }))
            emit(Resource.Loading(false))
        }
    }

    override suspend fun getTrendingMovieList(
        foreFetchFromRemote: Boolean,
        category: String,
    ): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading(true))
            val localMovieList = movieDatabase.movieDao.getMovieByCategory(category).first()
            val shouldLoadLocalMovie = localMovieList.isNotEmpty() && !foreFetchFromRemote

            if(shouldLoadLocalMovie){
                emit(Resource.Success(
                    data = localMovieList.map {movieEntity->
                        movieEntity.toMovie(category)
                    }
                ))
                Log.d("MovieList", "$category   $localMovieList")

                emit(Resource.Loading(false))
                return@flow
            }

            val moviesListFromAPI = try {
                movieApi.getTrendingList()
            } catch (e: IOException) {
                Log.d("APIError", e.stackTraceToString())
                e.printStackTrace()
                emit(Resource.Error(message = "Error loading movies"))
                return@flow
            } catch (e: Exception) {
                Log.d("APIError", e.stackTraceToString())

                e.printStackTrace()
                emit(Resource.Error(message = "Error loading movies"))
                return@flow
            }
            val movieEntities = moviesListFromAPI.results.let {
                it.map {movieDto ->
                    movieDto.toMovieEntity(category)
                }
            }
            Log.d("MovieList", "$category   $movieEntities")

            movieDatabase.movieDao.upsertMovieList(movieEntities)

            emit(Resource.Success(
                movieEntities.map { it.toMovie(category) }
            ))
            emit(Resource.Loading(false))


        }
    }

    override suspend fun getMovieByGenre(
        foreFetchFromRemote: Boolean,
        type: String,
        genre : String,
        page: Int,
        category: String
    ): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading(true))


            val genreId= movieDatabase.movieDao.getGenreID(genre, "movie").first()
            val localMovieList = movieDatabase.movieDao.getMovieByCategory(category).first()
            val shouldLoadLocalMovie = localMovieList.isNotEmpty() && !foreFetchFromRemote


            if(shouldLoadLocalMovie){
                emit(Resource.Success(
                    data = localMovieList.map {movieEntity->
                        movieEntity.toMovie(category)
                    }
                ))
                Log.d("HorrorMovies", "$genre   $localMovieList")

                emit(Resource.Loading(false))
                return@flow
            }
            if (genreId!=null) {
                Log.d("HorrorMovies", "Inside IF")

                val moviesListFromAPI = try {
                    movieApi.discoverMovie(
                        genreId = genreId.toString(),
                        page = page,
                        type = type.lowercase()
                    )
                } catch (e: IOException) {
                    Log.d("APIError", e.stackTraceToString())
                    e.printStackTrace()
                    emit(Resource.Error(message = "Error loading movies"))
                    return@flow
                } catch (e: Exception) {
                    Log.d("APIError", e.stackTraceToString())

                    e.printStackTrace()
                    emit(Resource.Error(message = "Error loading movies"))
                    return@flow
                }
                Log.d("HorrorMovies",   "Api response - ${moviesListFromAPI.results.size}")

                val movieEntities = moviesListFromAPI.results.let {
                    it.map { movieDto ->
                        movieDto.toMovieEntity(category)
                    }
                }
                Log.d("HorrorMovies", "$genre   ${movieEntities.size}")

                movieDatabase.movieDao.upsertMovieList(movieEntities)

                emit(Resource.Success(
                    movieEntities.map { it.toMovie(category) }
                ))
                emit(Resource.Loading(false))
           }else{
                Log.d("HorrorMovies", "Inside else")
            }
        }
    }

    override suspend fun getTvByGenre(
        foreFetchFromRemote: Boolean,
        genre : String,
        page: Int,
        category: String
    ): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading(true))


            val genreId= movieDatabase.movieDao.getGenreID(genre, "tv").first()
            val localTvList = movieDatabase.movieDao.getTvBCategory(category).first()
            val shouldLoadLocallist = localTvList.isNotEmpty() && !foreFetchFromRemote


            if(shouldLoadLocallist){
                emit(Resource.Success(
                    data = localTvList.map {tvEntity->
                        tvEntity.toMovie(category)
                    }
                ))
                Log.d("HorrorMovies", "$genre   $localTvList")

                emit(Resource.Loading(false))
                return@flow
            }
            if (genreId!=null) {
                Log.d("HorrorMovies", "Inside IF")

                val moviesListFromAPI = try {
                    movieApi.discoverTv(
                        genreId = genreId.toString(),
                        page = page
                    )
                } catch (e: IOException) {
                    Log.d("APIError", e.stackTraceToString())
                    e.printStackTrace()
                    emit(Resource.Error(message = "Error loading movies"))
                    return@flow
                } catch (e: Exception) {
                    Log.d("APIError", e.stackTraceToString())

                    e.printStackTrace()
                    emit(Resource.Error(message = "Error loading movies"))
                    return@flow
                }
                Log.d("HorrorMovies",   "Api response - ${moviesListFromAPI.results.size}")

                val tvEntities = moviesListFromAPI.results.let {
                    it.map { movieDto ->
                        movieDto.toTvEntity(category)
                    }
                }
                Log.d("HorrorMovies", "$genre   ${tvEntities.size}")

                movieDatabase.movieDao.upsertTvList(tvEntities)

                emit(Resource.Success(
                    tvEntities.map { it.toMovie(genre) }
                ))
                emit(Resource.Loading(false))
            }else{
                Log.d("HorrorMovies", "Inside else")
            }
        }
    }

    override suspend fun searchMovies(value: String): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading(true))

            val moviesListApi = try {
                movieApi.searchApi(
                    query = value
                )
            } catch (e: IOException) {
                Log.d("APIError", e.stackTraceToString())
                e.printStackTrace()
                emit(Resource.Error(message = "Error loading movies"))
                return@flow
            } catch (e: Exception) {
                Log.d("APIError", e.stackTraceToString())
                e.printStackTrace()
                emit(Resource.Error(message = "Error loading movies"))
                return@flow
            }
            emit(Resource.Success(
                moviesListApi.results.let {
                   it.map {moviesDto->
                       moviesDto.toMovie("search")
                   }
                }
            ))
            emit(Resource.Loading(false))
        }
    }

    override suspend fun insertSeachedMovies(movie: Movie) {
      withContext(Dispatchers.IO){
          movieDatabase.searchDao.insertIntoSearchList(movie.toSearchMovieEntity())
          Log.d("DBCall",movie.toString() )

      }
    }

    override suspend fun getSearchMovies(): Flow<List<Movie>> {
        return movieDatabase.searchDao.getSearchList().map { searchEntities ->
            searchEntities.map { it.toMovie() } // Properly map each entity
        }
    }

    override suspend fun deleteSearchHistory() {
        movieDatabase.searchDao.deleteSeachHistory()
    }

    override suspend fun insertIntoFavoriteMovies(movie: Movie) {
        movieDatabase.favoriteDao.insertIntoFavoriteList(movie.toFavoriteMovieEntity(true))
    }

    override suspend fun getFavoriteMovies(): Flow<List<Movie>> {
        return movieDatabase.favoriteDao.getFavoriteList().map { favEntities ->
            favEntities.map { it.toMovie() } // Properly map each entity
        }
    }

    override suspend fun deleteFromFavoriteHistory(id: Int) {
        movieDatabase.favoriteDao.deleteFromFavorite(id)
    }

    override suspend fun updateMovieEntity(isFavorite: Boolean, id: Int) {
        movieDatabase.favoriteDao.updateMovieEntity(isFavorite, id)
    }

    override suspend fun deleteFavHistory() {
        movieDatabase.favoriteDao.deleteFavoriteHistory()
    }

    override suspend fun getGenreList(list: List<Int>): String {
        val genreNames = movieDatabase.movieDao.getGenreNamesByIds(list)
        return genreNames.distinct().joinToString(", ")
    }
}