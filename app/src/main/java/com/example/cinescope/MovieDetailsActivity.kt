package com.example.cinescope

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cinescope.api.MovieDetails
import com.example.cinescope.api.OmdbApiService
import com.example.cinescope.databinding.ActivityMovieDetailsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding
    private val apiService = OmdbApiService.create()
    private val apiKey = "c1f730b" // Remplacez par votre clé API OMDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imdbId = intent.getStringExtra("IMDB_ID") ?: ""
        if (imdbId.isNotEmpty()) {
            fetchMovieDetails(imdbId)
        } else {
            Toast.makeText(this, "ID IMDB manquant", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchMovieDetails(imdbId: String) {
        apiService.getMovieById(apiKey, imdbId).enqueue(object : Callback<MovieDetails> {
            override fun onResponse(call: Call<MovieDetails>, response: Response<MovieDetails>) {
                if (response.isSuccessful) {
                    response.body()?.let { displayMovieDetails(it) }
                } else {
                    Toast.makeText(this@MovieDetailsActivity, "Erreur de récupération", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MovieDetails>, t: Throwable) {
                Toast.makeText(this@MovieDetailsActivity, "Erreur réseau : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayMovieDetails(movie: MovieDetails) {
        binding.movieTitle.text = movie.Title
        binding.movieYear.text = movie.Year
        binding.movieGenre.text = movie.Genre
        binding.movieDirector.text = "Directeur : ${movie.Director}"
        binding.movieActors.text = "Acteurs : ${movie.Actors}"
        binding.moviePlot.text = movie.Plot
        Glide.with(this).load(movie.Poster).into(binding.moviePoster)
    }
}
