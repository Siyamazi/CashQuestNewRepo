package com.iie.cashquest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddCategory : AppCompatActivity() {
    private lateinit var etCategoryName: EditText
    private lateinit var btnSaveCategory: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_category)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addCategory)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etCategoryName = findViewById(R.id.etCategoryName)
        btnSaveCategory = findViewById(R.id.btnSaveCategory)

        val fabSummary = findViewById<FloatingActionButton>(R.id.fabSummary)
        val fabDashboard = findViewById<FloatingActionButton>(R.id.fabDashboard)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        fabSummary.visibility = View.GONE  // Already here
        fabDashboard.setOnClickListener {
            startActivity(Intent(this, Dashboard::class.java))
        }

        btnBack.visibility = View.VISIBLE
        btnBack.setOnClickListener {
            finish()
        }

        btnSaveCategory.setOnClickListener {
            val categoryName = etCategoryName.text.toString().trim()

            if (categoryName.isEmpty()) {
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val categoryData = hashMapOf(
                "name" to categoryName,
                "createdAt" to System.currentTimeMillis()
            )

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("categories")
                .add(categoryData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Category saved successfully", Toast.LENGTH_SHORT).show()
                    etCategoryName.text.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save category", Toast.LENGTH_SHORT).show()
                }
        }
        val btnViewCategories = findViewById<Button>(R.id.btnViewCategories)
        btnViewCategories.setOnClickListener {
            startActivity(Intent(this, ViewCategory::class.java))
        }
    }
}

/**
 * Adapted from Firebase Android SDK samples
 * Copyright Google LLC
 * Licensed under Apache License 2.0
 * https://github.com/firebase/snippets-android
 */