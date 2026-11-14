package com.example.simpleauthentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        btnLogin.setOnClickListener {

            val userEmail = email.text.toString().trim()
            val userPassword = password.text.toString()

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {

                auth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            val uid = auth.currentUser?.uid

                            if (uid != null) {

                                db.collection("users").document(uid)
                                    .get()
                                    .addOnSuccessListener { document ->

                                        val fullName = document.getString("fullName")
                                        val username = document.getString("username")

                                        val intent = Intent(this, HomeActivity::class.java)

                                        intent.putExtra("fullName", fullName)
                                        intent.putExtra("username", username)

                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { error ->

                                        Toast.makeText(
                                            this,
                                            "Unable to retrieve user information: ${error.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }

                        } else {
                            Toast.makeText(
                                this,
                                task.exception?.localizedMessage ?: "Login failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}