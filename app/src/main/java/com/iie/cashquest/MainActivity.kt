package com.iie.cashquest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp


import com.google.firebase.firestore.FirebaseFirestore



class MainActivity : AppCompatActivity() {


    private lateinit var db: FirebaseFirestore


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val balanceText = findViewById<TextView>(R.id.balance)

        FirebaseApp.initializeApp(this)

        db = FirebaseFirestore.getInstance()

        // Example: Add a user to Firestore
        val user = hashMapOf(
            "name" to "John Doe",
            "email" to "john@example.com"
        )

        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "User added with ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding user: $e", Toast.LENGTH_SHORT).show()
            }

}
    }



