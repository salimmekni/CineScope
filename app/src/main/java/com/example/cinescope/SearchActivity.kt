package com.example.cinescope

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
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
    private val apiKey = "388bae6e" // 🔴A éviter en dur, mieux vaut le stocker en `local.properties`

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Détection de la recherche quand l'utilisateur valide l'entrée
        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchInput.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchMovies(query)
                } else {
                    Toast.makeText(this, "Veuillez entrer un titre de film", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    private fun searchMovies(query: String) {
        binding.progressBar.visibility = android.view.View.VISIBLE // 🔵 Afficher le chargement

        val call = RetrofitClient.apiService.searchMovies(query, apiKey)
        call.enqueue(object : Callback<OmdbResponse> {
            override fun onResponse(call: Call<OmdbResponse>, response: Response<OmdbResponse>) {
                binding.progressBar.visibility = android.view.View.GONE // 🔵 Cacher le chargement

                if (response.isSuccessful) {
                    val movies = response.body()?.movies ?: emptyList()
                    if (movies.isNotEmpty()) {
                        binding.recyclerView.adapter = MovieAdapter(movies) { selectedMovie ->
                            val intent = Intent(this@SearchActivity, MovieDetailsActivity::class.java).apply {
                                putExtra("MOVIE_ID", selectedMovie.imdbID)
                            }
                            startActivity(intent, ActivityOptions.makeCustomAnimation(this@SearchActivity, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
                        }
                    } else {
                        Toast.makeText(this@SearchActivity, "Aucun film trouvé", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SearchActivity, "Erreur serveur: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OmdbResponse>, t: Throwable) {
                binding.progressBar.visibility = android.view.View.GONE // 🔵 Cacher le chargement
                Toast.makeText(this@SearchActivity, "Erreur de connexion: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.selectedItemId = R.id.nav_search

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navigateTo(HomeActivity::class.java)
                    true // Retourne `true`
                }
                R.id.nav_search -> true //Retourne `true`
                R.id.nav_favorites -> {
                    navigateTo(FavoritesActivity::class.java)
                    true
                }
                R.id.nav_profile -> {
                    navigateTo(ProfileActivity::class.java)
                    true
                }
                else -> false // Retourne `false` pour un item inconnu
            }
        }

    }

    private fun navigateTo(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent, ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
        finish()
    }
}
