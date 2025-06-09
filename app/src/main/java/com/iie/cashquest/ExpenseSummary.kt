package com.iie.cashquest

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.EventLogTags
import android.util.EventLogTags.Description
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore




class ExpenseSummary : AppCompatActivity() {
    private lateinit var pieChart: PieChart
    private lateinit var tvTotalSpent: TextView
    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_expense_summary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ExpenseSummary)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        pieChart = findViewById(R.id.pieChart)
        tvTotalSpent = findViewById(R.id.tvTotalSpent)

        loadExpenseSummary()

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

    @SuppressLint("SetTextI18n")
    private fun loadExpenseSummary() {
        db.collection("users")
            .document(currentUserId)
            .collection("expenses")
            .get()
            .addOnSuccessListener { snapshot ->
                val categoryTotals = mutableMapOf<String, Float>()
                var totalAmount = 0f

                for (doc in snapshot) {
                    val category = doc.getString("category") ?: "Other"
                    val amount = doc.getDouble("amount")?.toFloat() ?: 0f

                    totalAmount += amount
                    categoryTotals[category] = categoryTotals.getOrDefault(category, 0f) + amount
                }

                tvTotalSpent.text = "Total Spent: R$totalAmount"

                val entries = categoryTotals.map { PieEntry(it.value, it.key) }

                val dataSet = PieDataSet(entries, "Expenses by Category")
                dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
                dataSet.valueTextSize = 14f
                dataSet.valueTextColor = Color.WHITE

                val pieData = PieData(dataSet)

                pieChart.data = pieData
                pieChart.description = com.github.mikephil.charting.components.Description()
                    .apply { text = "" }
                pieChart.centerText = "Categories"
                pieChart.setUsePercentValues(true)
                pieChart.animateY(1000)
                pieChart.invalidate()
            }
    }
}
