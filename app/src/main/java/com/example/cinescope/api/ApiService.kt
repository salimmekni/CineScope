package com.example.cinescope.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("?type=movie")
    fun searchMovies(@Query("s") query: String, @Query("apikey") apiKey: String): Call<OmdbResponse>

    @GET("?plot=full")
    fun getMovieDetails(@Query("i") imdbID: String, @Query("apikey") apiKey: String): Call<Movie>
}
