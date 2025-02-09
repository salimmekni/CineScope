package com.example.cinescope
import androidx.recyclerview.widget.GridLayoutManager
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinescope.api.Movie
import com.example.cinescope.api.RetrofitClient
import com.example.cinescope.databinding.ActivityFavoritesBinding
import com.example.cinescope.models.FavoriteMovie
import com.example.cinescope.utils.FavoritesManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private val favoritesList = mutableListOf<FavoriteMovie>()
    private val apiKey = "388bae6e" // Clé API OMDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewFavorites.layoutManager = GridLayoutManager(this, 2) // 2 colonnes

        loadFavorites()
        setupBottomNavigation()
    }

    private fun loadFavorites() {
        FavoritesManager.getFavorites { favorites ->
            favoritesList.clear()
            favoritesList.addAll(favorites)

            val movieList = mutableListOf<Movie>()

            favoritesList.forEach { favoriteMovie ->
                if (favoriteMovie.poster.isNullOrEmpty()) {
                    // Si le poster est vide, on récupère les détails du film depuis l'API OMDb
                    fetchMovieDetails(favoriteMovie.imdbID) { movie ->
                        movieList.add(movie)
                        if (movieList.size == favoritesList.size) {
                            updateRecyclerView(movieList)
                        }
                    }
                } else {
                    // Sinon, on utilise les données de Firestore directement
                    movieList.add(
                        Movie(
                            imdbID = favoriteMovie.imdbID,
                            title = favoriteMovie.title,
                            poster = favoriteMovie.poster,
                            year = favoriteMovie.year ?: "Unknown",
                            type = favoriteMovie.type ?: "Movie",
                            plot = "Synopsis non disponible"
                        )
                    )
                    if (movieList.size == favoritesList.size) {
                        updateRecyclerView(movieList)
                    }
                }
            }
        }
    }

    private fun fetchMovieDetails(movieId: String, callback: (Movie) -> Unit) {
        val call = RetrofitClient.apiService.getMovieDetails(movieId, apiKey)
        call.enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                val movie = response.body()
                if (movie != null) {
                    callback(movie)
                }
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                println("Erreur API OMDb: ${t.message}")
            }
        })
    }

    private fun updateRecyclerView(movies: List<Movie>) {
        binding.recyclerViewFavorites.adapter = MovieAdapter(movies) { movie ->
            val intent = Intent(this, MovieDetailsActivity::class.java).apply {
                putExtra("MOVIE_ID", movie.imdbID)
                putExtra("MOVIE_TITLE", movie.title)
                putExtra("MOVIE_POSTER", movie.poster)
                putExtra("MOVIE_YEAR", movie.year)
                putExtra("MOVIE_TYPE", movie.type)
                putExtra("MOVIE_PLOT", movie.plot)
            }
            startActivity(intent)
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.selectedItemId = R.id.nav_favorites

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener true
                }
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener true
                }
                R.id.nav_favorites -> return@setOnItemSelectedListener true
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }
}
