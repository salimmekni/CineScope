package com.example.cinescope

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cinescope.api.Movie
import com.example.cinescope.api.RetrofitClient
import com.example.cinescope.databinding.ActivityMovieDetailsBinding
import com.example.cinescope.models.FavoriteMovie
import com.example.cinescope.utils.FavoritesManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieDetailsBinding
    private var movieId: String? = null
    private val apiKey = "388bae6e" // Remplace par ta propre clé API OMDb

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigation()

        //  Récupération des données envoyées par l'Intent
        movieId = intent.getStringExtra("MOVIE_ID")
        val movieTitle = intent.getStringExtra("MOVIE_TITLE") ?: "Titre inconnu"
        val moviePoster = intent.getStringExtra("MOVIE_POSTER") ?: ""
        val movieYear = intent.getStringExtra("MOVIE_YEAR") ?: "Année inconnue"
        val movieType = intent.getStringExtra("MOVIE_TYPE") ?: "Type inconnu"
        val moviePlot = intent.getStringExtra("MOVIE_PLOT") ?: "Synopsis non disponible"

        //  Mise à jour de l'UI avec les données du film
        updateUI(movieTitle, moviePoster, movieYear, movieType, moviePlot)

        //  Si `type` ou `plot` est manquant, on va chercher les détails depuis l'API OMDb
        if (movieType == "Type inconnu" || moviePlot == "Synopsis non disponible") {
            movieId?.let { fetchMovieDetails(it) }
        }

        checkIfFavorite()

        binding.favoriteButton.setOnClickListener {
            movieId?.let { id ->
                val movie = FavoriteMovie(id, movieTitle, moviePoster)
                toggleFavorite(movie)
            }
        }
    }

    private fun fetchMovieDetails(movieId: String) {
        val call = RetrofitClient.apiService.getMovieDetails(movieId, apiKey)
        call.enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                val movie = response.body()
                if (movie != null) {
                    val plot = if (movie.plot.isNullOrBlank()) "Synopsis non disponible" else movie.plot
                    val type = if (movie.type.isNullOrBlank()) "Type inconnu" else movie.type
                    updateUI(movie.title, movie.poster, movie.year, type, plot)
                }
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                Toast.makeText(this@MovieDetailsActivity, "Erreur: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(title: String, poster: String, year: String, type: String, plot: String) {
        binding.movieTitle.text = title
        binding.movieYear.text = "Année : $year"
        binding.movieType.text = "Type : $type"
        binding.moviePlot.text = "Synopsis : $plot"

        Glide.with(this)
            .load(poster)
            .placeholder(android.R.drawable.ic_menu_report_image)
            .into(binding.moviePoster)
    }

    private fun checkIfFavorite() {
        movieId?.let { id ->
            FavoritesManager.isFavorite(id) { isFav ->
                binding.favoriteButton.setImageResource(
                    if (isFav) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
                )
            }
        }
    }

    private fun toggleFavorite(movie: FavoriteMovie) {
        FavoritesManager.isFavorite(movie.imdbID) { isFav ->
            if (isFav) {
                FavoritesManager.removeFavorite(movie.imdbID) { success ->
                    if (success) {
                        Toast.makeText(this, "Supprimé des favoris", Toast.LENGTH_SHORT).show()
                        binding.favoriteButton.setImageResource(R.drawable.ic_favorite_border)
                    }
                }
            } else {
                FavoritesManager.addFavorite(movie) { success ->
                    if (success) {
                        Toast.makeText(this, "Ajouté aux favoris", Toast.LENGTH_SHORT).show()
                        binding.favoriteButton.setImageResource(R.drawable.ic_favorite_filled)
                    }
                }
            }
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = binding.bottomNavigationView

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    return@setOnItemSelectedListener true
                }
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    return@setOnItemSelectedListener true
                }
                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    return@setOnItemSelectedListener true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }
}
