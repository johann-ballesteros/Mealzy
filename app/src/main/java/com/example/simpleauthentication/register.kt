
package com.example.simpleauthentication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        val etFullName = findViewById<EditText>(R.id.etRegFullName)
        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etUsername = findViewById<EditText>(R.id.etRegUsername)
        val etPassword = findViewById<EditText>(R.id.etRegPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)

        btnRegister.setOnClickListener {

            val fullName = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (fullName.isEmpty() || email.isEmpty() ||
                username.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()

            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()

            } else if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()

            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            val user = auth.currentUser

                            if (user != null) {
                                val userData = HashMap<String, Any>()

                                userData["fullName"] = fullName
                                userData["email"] = email
                                userData["username"] = username

                                db.collection("users")
                                    .document(user.uid)
                                    .set(userData)
                                    .addOnSuccessListener {

                                        Toast.makeText(
                                            this,
                                            "Registration successful!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        auth.signOut()
                                        finish()
                                    }
                                    .addOnFailureListener { e ->

                                        Toast.makeText(
                                            this,
                                            "Error saving data: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }

                        } else {
                            Toast.makeText(
                                this,
                                task.exception?.localizedMessage ?: "Registration failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }

        tvLogin.setOnClickListener {
            finish()
        }
    }
}
