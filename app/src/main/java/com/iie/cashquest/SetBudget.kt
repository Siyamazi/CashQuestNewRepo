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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class SetBudget : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var etBudgetAmount: EditText
    private lateinit var btnSave: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_set_budget)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.SetBudget)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        etBudgetAmount = findViewById(R.id.etBudgetAmount)
        btnSave = findViewById(R.id.btnSaveBudget)

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



        btnSave.setOnClickListener {
            val budget = etBudgetAmount.text.toString().toDoubleOrNull()
            val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

            if (budget != null) {
                val userId = auth.currentUser?.uid ?: return@setOnClickListener
                val data = hashMapOf(
                    "monthly_budget" to budget,
                    "month_year" to currentMonth
                )

                firestore.collection("user_settings")
                    .document(userId)
                    .set(data)
                    .addOnSuccessListener {
                        Toast.makeText(this@SetBudget, "Budget saved", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@SetBudget, "Failed to save", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this@SetBudget, "Enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        }



    }
}
