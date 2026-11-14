
package com.example.simpleauthentication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
        val btnManage = findViewById<Button>(R.id.btnManage)

        val user = auth.currentUser

        if (user != null) {

            db.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->

                    if (document.exists()) {
                        tvFullName.text = "Full Name: ${document.getString("fullName")}"
                        tvEmail.text = "Email: ${document.getString("email")}"
                        tvUsername.text = "Username: ${document.getString("username")}"
                    }
                }
        }

        btnManage.setOnClickListener {
            showManageDialog()
        }
    }

    private fun showManageDialog() {

        val options = arrayOf("Update Data", "Delete Data")

        AlertDialog.Builder(this)
            .setTitle("Manage Account")
            .setItems(options) { _, which ->

                when (which) {
                    0 -> showUpdateDialog()
                    1 -> showDeleteDialog()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showUpdateDialog() {

        val user = auth.currentUser ?: return

        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val layout = LinearLayout(this)
                    layout.orientation = LinearLayout.VERTICAL
                    layout.setPadding(50, 20, 50, 0)

                    val etFullName = EditText(this)
                    etFullName.hint = "Full Name"
                    etFullName.setText(document.getString("fullName"))

                    val etUsername = EditText(this)
                    etUsername.hint = "Username"
                    etUsername.setText(document.getString("username"))

                    layout.addView(etFullName)
                    layout.addView(etUsername)

                    AlertDialog.Builder(this)
                        .setTitle("Update Details")
                        .setView(layout)
                        .setPositiveButton("Update") { _, _ ->

                            val fullName = etFullName.text.toString().trim()
                            val username = etUsername.text.toString().trim()

                            if (fullName.isEmpty() || username.isEmpty()) {
                                Toast.makeText(
                                    this,
                                    "Please fill in all fields",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {

                                val updatedData = hashMapOf<String, Any>(
                                    "fullName" to fullName,
                                    "username" to username
                                )

                                db.collection("users")
                                    .document(user.uid)
                                    .update(updatedData as Map<String, Any>)
                                    .addOnSuccessListener {

                                        Toast.makeText(
                                            this,
                                            "Details updated successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        recreate()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this,
                                            "Update failed: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
    }

    private fun showDeleteDialog() {

        val user = auth.currentUser ?: return

        AlertDialog.Builder(this)
            .setTitle("Delete Account Data")
            .setMessage("Are you sure you want to delete your user data?")
            .setPositiveButton("Delete") { _, _ ->

                db.collection("users")
                    .document(user.uid)
                    .delete()
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "User data deleted successfully!",
                            Toast.LENGTH_SHORT
                        ).show()

                        auth.signOut()

                        val intent = android.content.Intent(
                            this,
                            MainActivity::class.java
                        )
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Delete failed: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
