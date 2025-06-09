package com.iie.cashquest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddExpense : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    private lateinit var etTitle: EditText
    private lateinit var etAmount: EditText
    private lateinit var etNote: EditText
    private lateinit var tvSelectedCategory: TextView
    private lateinit var btnSelectCategory: Button
    private lateinit var btnSubmit: Button

    private var selectedCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_expense)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.AddExpense)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etTitle = findViewById(R.id.etExpenseTitle)
        etAmount = findViewById(R.id.etExpenseAmount)
        etNote = findViewById(R.id.etExpenseNote)
        tvSelectedCategory = findViewById(R.id.tvSelectedCategory)
        btnSelectCategory = findViewById(R.id.btnSelectCategory)
        btnSubmit = findViewById(R.id.btnSubmitExpense)

        btnSelectCategory.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = currentUser.uid
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("categories")
                .get()
                .addOnSuccessListener { snapshot ->
                    val categoryList = snapshot.documents.mapNotNull { it.getString("name") }

                    if (categoryList.isEmpty()) {
                        Toast.makeText(this, "No categories found. Please add some.", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Choose Category")
                    builder.setItems(categoryList.toTypedArray()) { _, which ->
                        selectedCategory = categoryList[which]
                        tvSelectedCategory.text = "Selected: $selectedCategory"
                    }
                    builder.show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load categories", Toast.LENGTH_SHORT).show()
                }
        }


        btnSubmit.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val amountStr = etAmount.text.toString().trim()
            val note = etNote.text.toString().trim()

            if (title.isEmpty() || amountStr.isEmpty() || selectedCategory == null) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null) {
                Toast.makeText(this, "Amount must be a valid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = currentUser.uid

            val expenseData = hashMapOf(
                "title" to title,
                "amount" to amount,
                "note" to note,
                "category" to selectedCategory,
                "timestamp" to Timestamp.now()
            )

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("expenses")
                .add(expenseData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Expense saved successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Expense_list::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save: ${e.message}", Toast.LENGTH_LONG).show()
                }





        }
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
    }
}

/**
 * Adapted from Firebase Android SDK samples
 * Copyright Google LLC
 * Licensed under Apache License 2.0
 * https://github.com/firebase/snippets-android
 */