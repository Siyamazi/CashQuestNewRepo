package com.iie.cashquest

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class Dashboard : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var profileImage: ImageView
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        checkIfBudgetExceeded()

        val tvBudgetWarning = findViewById<TextView>(R.id.tvBudgetWarning)
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val btnAddExpense = findViewById<Button>(R.id.btnAddExpense)
        val btnAddCategory = findViewById<Button>(R.id.btnAddCategory)
        val btnViewSummary = findViewById<Button>(R.id.btnViewSummary)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnViewExpenses = findViewById<Button>(R.id.btnViewExpenses)
        val btnAddBudget = findViewById<Button>(R.id.btnAddBudget)
        profileImage = findViewById(R.id.profileImage)

        val fabSummary = findViewById<FloatingActionButton>(R.id.fabSummary)
        val fabDashboard = findViewById<FloatingActionButton>(R.id.fabDashboard)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        fabSummary.setOnClickListener {
            startActivity(Intent(this, ExpenseSummary::class.java))
        }

        fabDashboard.visibility = View.GONE  // Already on dashboard
        btnBack.visibility = View.GONE

        val currentUser = auth.currentUser
        tvWelcome.text = "Welcome, ${currentUser?.email}"

        btnAddExpense.setOnClickListener {
            startActivity(Intent(this, AddExpense::class.java))
        }

        btnAddCategory.setOnClickListener {
            startActivity(Intent(this, AddCategory::class.java))
        }

        btnViewSummary.setOnClickListener {
            startActivity(Intent(this, ExpenseSummary::class.java))
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, Welcome::class.java))
            finish()
        }

        btnViewExpenses.setOnClickListener {
            startActivity(Intent(this, Expense_list::class.java))
        }

        btnAddBudget.setOnClickListener {
            startActivity(Intent(this, SetBudget::class.java))
        }

        tvBudgetWarning.setOnClickListener {
            startActivity(Intent(this, SetBudget::class.java))
        }

        // Profile image picker
        profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        loadProfileImage()

        showBudgetExceededDialog()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            profileImage.setImageURI(imageUri)
            uploadImageToFirebase()
        }
    }

    private fun uploadImageToFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_pictures/$userId.jpg")

        imageUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        // Save URL to Firestore
                        FirebaseFirestore.getInstance().collection("users")
                            .document(userId)
                            .update("profileImageUrl", downloadUrl.toString())
                            .addOnSuccessListener {
                                Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to save image URL.", Toast.LENGTH_SHORT).show()
                            }

                        // Also update UI immediately
                        Glide.with(this)
                            .load(downloadUrl)
                            .into(profileImage)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Upload failed!", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun loadProfileImage() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val imageUrl = document.getString("profileImageUrl")
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(imageUrl)
                        .into(profileImage)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load profile picture", Toast.LENGTH_SHORT).show()
            }
    }

    // Function inspired by Firebase Firestore usage examples and budget tracking tutorials
    private fun checkIfBudgetExceeded() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Log.d("BudgetCheck", "No user ID")
            return
        }

        val firestore = FirebaseFirestore.getInstance()
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        val tvBudgetWarning = findViewById<TextView>(R.id.tvBudgetWarning)
        val tvMonthlyBudget = findViewById<TextView>(R.id.tvMonthlyBudget)

        Log.d("BudgetCheck", "Starting budget check for user: $userId")

        firestore.collection("user_settings").document(userId).get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    Log.d("BudgetCheck", "No user settings document")
                    return@addOnSuccessListener
                }

                val budget = document.getDouble("monthly_budget") ?: run {
                    Log.d("BudgetCheck", "No budget set")
                    return@addOnSuccessListener
                }

                Log.d("BudgetCheck", "Budget found: $budget")
                tvMonthlyBudget.text = "Monthly Budget: R%.2f".format(budget)

                // ✅ Correct path to match Firestore security rules
                firestore.collection("users")
                    .document(userId)
                    .collection("expenses")
                    .get()
                    .addOnSuccessListener { expenses ->
                        var total = 0.0
                        for (expense in expenses) {
                            val date = expense.getString("date")
                            if (date != null && date.startsWith(currentMonth)) {
                                total += expense.getDouble("amount") ?: 0.0
                            }
                        }

                        Log.d("BudgetCheck", "Total expenses this month: $total")

                        if (total > budget) {
                            Log.d("BudgetCheck", "Budget exceeded! Showing warning")
                            tvBudgetWarning.visibility = View.VISIBLE
                            tvBudgetWarning.text = "⚠ You have exceeded your budget! Budget: R$budget | Spent: R$total"
                            showBudgetExceededDialog()
                        } else {
                            Log.d("BudgetCheck", "Budget not exceeded")
                            tvBudgetWarning.visibility = View.GONE
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("BudgetCheck", "Error getting expenses", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("BudgetCheck", "Error getting budget", e)
            }
    }




    private fun showBudgetExceededDialog() {
        AlertDialog.Builder(this)
            .setTitle("Budget Exceeded")
            .setMessage("You have exceeded your monthly budget. Would you like to set a new one?")
            .setPositiveButton("Set New Budget") { _, _ ->
                startActivity(Intent(this, SetBudget::class.java))
            }
            .setNegativeButton("Cancel", null)
            .setIcon(R.drawable.warning)
            .show()
    }
}

/**
 * Adapted from Firebase Android SDK samples
 * Copyright Google LLC
 * Licensed under Apache License 2.0
 * https://github.com/firebase/snippets-android
 */
