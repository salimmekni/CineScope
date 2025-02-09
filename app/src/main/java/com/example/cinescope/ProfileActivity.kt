package com.example.cinescope

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cinescope.databinding.ActivityProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        binding.profileName.text = user?.displayName ?: "Utilisateur"
        binding.profileEmail.text = user?.email ?: "Email inconnu"

        // Configuration du bouton Déconnexion
        binding.logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Configuration du Bottom Navigation
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = binding.bottomNavigationView

        // Sélectionner l'élément actuel du menu
        bottomNavigationView.selectedItemId = R.id.nav_profile

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> navigateToActivity(HomeActivity::class.java)
                R.id.nav_search -> navigateToActivity(SearchActivity::class.java)
                R.id.nav_favorites -> navigateToActivity(FavoritesActivity::class.java)
                R.id.nav_profile -> true
                else -> false
            }
        }
    }

    private fun navigateToActivity(activityClass: Class<*>) : Boolean {
        if (this::class.java != activityClass) {
            startActivity(Intent(this, activityClass))
            overridePendingTransition(0, 0) // Désactive l'animation de transition
            finish()
        }
        return true
    }
}
