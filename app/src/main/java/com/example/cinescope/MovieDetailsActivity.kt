package com.example.cinescope

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cinescope.api.Movie
import com.example.cinescope.api.RetrofitClient
import com.example.cinescope.databinding.ActivityMovieDetailsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding
    private val apiKey = "388bae6e"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieId = intent.getStringExtra("MOVIE_ID")
        movieId?.let { fetchMovieDetails(it) }
    }

    private fun fetchMovieDetails(movieId: String) {
        val call = RetrofitClient.apiService.getMovieDetails(movieId, apiKey)
        call.enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                val movie = response.body()
                if (movie != null) {
                    binding.movieTitle.text = movie.title
                    binding.movieDescription.text = movie.plot
                    binding.movieRating.text = "⭐ ${movie.year}"

                    Glide.with(this@MovieDetailsActivity)
                        .load(movie.poster)
                        .into(binding.moviePoster)
                }
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                println("Erreur: ${t.message}")
            }
        })
    }
}
