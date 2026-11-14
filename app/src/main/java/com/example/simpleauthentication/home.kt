
package com.example.simpleauthentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tvFullName = findViewById<TextView>(R.id.tvFullName)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val logout = findViewById<Button>(R.id.btnLogout)

        val user = auth.currentUser

        if (user != null) {

            db.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->

                    if (document.exists()) {

                        val fullName = document.getString("fullName") ?: ""
                        val email = document.getString("email") ?: ""
                        val username = document.getString("username") ?: ""

                        tvFullName.text = "Full Name: $fullName"
                        tvEmail.text = "Email: $email"
                        tvUsername.text = "Username: $username"

                    } else {
                        Toast.makeText(this, "User details not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Error retrieving data: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        logout.setOnClickListener {
            auth.signOut()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
