package com.example.cinescope

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.cinescope.api.Movie
import com.example.cinescope.api.OmdbResponse
import com.example.cinescope.api.RetrofitClient
import com.example.cinescope.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val apiKey = "388bae6e" // Clé API OMDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuration du RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Charger les films tendances
        fetchMovies("Batman") //

        // Gérer le clic sur "Détails"
        binding.detailsButton.setOnClickListener {
            val intent = Intent(this, MovieDetailsActivity::class.java)
            intent.putExtra("MOVIE_ID", "tt7286456") // Joker (exemple)
            startActivity(intent)
        }

        // Configuration de la barre de navigation
        setupBottomNavigation()
    }

    private fun fetchMovies(query: String) {
        val call = RetrofitClient.apiService.searchMovies(query, apiKey)
        call.enqueue(object : Callback<OmdbResponse> {
            override fun onResponse(call: Call<OmdbResponse>, response: Response<OmdbResponse>) {
                if (response.isSuccessful) {
                    val movies = response.body()?.movies ?: emptyList()

                    // Configuration de l'adapter pour afficher les films
                    binding.recyclerView.adapter = MovieAdapter(movies) { selectedMovie ->
                        val intent = Intent(this@HomeActivity, MovieDetailsActivity::class.java)
                        intent.putExtra("MOVIE_ID", selectedMovie.imdbID)
                        startActivity(intent)
                    }

                    // Afficher le premier film en grand
                    if (movies.isNotEmpty()) {
                        Glide.with(this@HomeActivity)
                            .load(movies[0].poster)
                            .into(binding.topMovieImage)
                    }
                }
            }

            override fun onFailure(call: Call<OmdbResponse>, t: Throwable) {
                println("Erreur: ${t.message}")
            }
        })
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = binding.bottomNavigationView

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
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
