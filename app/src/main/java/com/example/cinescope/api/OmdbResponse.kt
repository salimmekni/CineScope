package com.example.cinescope.api

import com.google.gson.annotations.SerializedName

data class OmdbResponse(
    @SerializedName("Search") val movies: List<Movie>
)
