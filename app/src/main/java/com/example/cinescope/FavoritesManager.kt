package com.example.cinescope.utils

import com.example.cinescope.models.FavoriteMovie
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FavoritesManager {
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    // 📌 Ajouter un film aux favoris
    fun addFavorite(movie: FavoriteMovie, onComplete: (Boolean) -> Unit) {
        user?.let { currentUser ->
            db.collection("users").document(currentUser.uid)
                .collection("favorites").document(movie.imdbID)
                .set(movie)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        }
    }

    // 📌 Supprimer un film des favoris
    fun removeFavorite(movieId: String, onComplete: (Boolean) -> Unit) {
        user?.let { currentUser ->
            db.collection("users").document(currentUser.uid)
                .collection("favorites").document(movieId)
                .delete()
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        }
    }

    // 📌 Vérifier si un film est dans les favoris
    fun isFavorite(movieId: String, onResult: (Boolean) -> Unit) {
        user?.let { currentUser ->
            db.collection("users").document(currentUser.uid)
                .collection("favorites").document(movieId)
                .get()
                .addOnSuccessListener { document -> onResult(document.exists()) }
                .addOnFailureListener { onResult(false) }
        }
    }

    // 📌 Récupérer la liste des films favoris
    fun getFavorites(onResult: (List<FavoriteMovie>) -> Unit) {
        user?.let { currentUser ->
            db.collection("users").document(currentUser.uid)
                .collection("favorites")
                .get()
                .addOnSuccessListener { result ->
                    val favoritesList = result.documents.mapNotNull { it.toObject(FavoriteMovie::class.java) }
                    onResult(favoritesList)
                }
                .addOnFailureListener {
                    onResult(emptyList())
                }
        }
    }
}
