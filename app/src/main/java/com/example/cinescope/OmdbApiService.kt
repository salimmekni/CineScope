package com.example.cinescope.api

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Modèle pour les détails d'un film
data class MovieDetails(
    val Title: String,
    val Year: String,
    val Genre: String,
    val Director: String,
    val Actors: String,
    val Plot: String,
    val Poster: String
)

// Interface Retrofit pour OMDb API
interface OmdbApiService {
    @GET("/")
    fun getMovieById(
        @Query("apikey") apiKey: String,
        @Query("i") imdbId: String
    ): Call<MovieDetails>

    companion object {
        private const val BASE_URL = "https://www.omdbapi.com/"

        fun create(): OmdbApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(OmdbApiService::class.java)
        }
    }
}
