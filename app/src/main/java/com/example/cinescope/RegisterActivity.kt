package com.example.cinescope

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cinescope.ui.theme.CineScopeTheme
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CineScopeTheme {
                auth = FirebaseAuth.getInstance()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    TextFieldWithLabel("Nom", Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    TextFieldWithLabel("Prénom", Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    TextFieldWithLabel("Email", Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    TextFieldWithLabel("Mot de passe", Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        auth.createUserWithEmailAndPassword("test@example.com", "password")
                            .addOnCompleteListener(this@RegistrationActivity) { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this@RegistrationActivity, "Inscription réussie", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@RegistrationActivity, "Erreur d'inscription", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }) {
                        Text("S'inscrire")
                    }
                }
            }
        }
    }
}

@Composable
fun TextFieldWithLabel(label: String, modifier: Modifier) {
    TextField(
        value = "",
        onValueChange = {},
        label = { Text(label) },
        modifier = modifier
    )
}
