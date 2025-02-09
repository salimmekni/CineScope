package com.example.cinescope.models

data class FavoriteMovie(
    val imdbID: String = "",
    val title: String = "",
    val poster: String = "",
    val year: String? = "Unknown",  // Ajouté pour éviter les valeurs null
    val type: String? = "movie",    // Ajouté pour éviter les valeurs null
    val plot: String? = "Synopsis non disponible" // Ajouté pour éviter les valeurs null
)
