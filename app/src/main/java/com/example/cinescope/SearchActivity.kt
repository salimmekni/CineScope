package com.example.cinescope

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinescope.api.OmdbResponse
import com.example.cinescope.api.RetrofitClient
import com.example.cinescope.databinding.ActivitySearchBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Écouteur sur l'input pour détecter une recherche
        binding.searchInput.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchInput.text.toString()
            if (query.isNotEmpty()) {
                searchMovies(query)
            }
            true
        }
    }

    private fun searchMovies(query: String) {
        val apiKey = "388bae6e" // Remplace par ta clé API OMDb
        val call = RetrofitClient.apiService.searchMovies(query, apiKey)

        call.enqueue(object : Callback<OmdbResponse> {
            override fun onResponse(call: Call<OmdbResponse>, response: Response<OmdbResponse>) {
                if (response.isSuccessful) {
                    val movies = response.body()?.movies ?: emptyList()
                    binding.recyclerView.adapter = MovieAdapter(movies) { selectedMovie ->
                        val intent = Intent(this@SearchActivity, MovieDetailsActivity::class.java)
                        intent.putExtra("MOVIE_ID", selectedMovie.imdbID)
                        startActivity(intent)
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

        // Marque la section actuelle comme sélectionnée
        bottomNavigationView.selectedItemId = R.id.nav_search

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    return@setOnItemSelectedListener true
                }
                R.id.nav_search -> return@setOnItemSelectedListener true
                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    return@setOnItemSelectedListener true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }
}
