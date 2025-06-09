package com.iie.cashquest

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Expense_list : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinnerFilter: Spinner
    private val expenseList = mutableListOf<Expense>()
    private lateinit var adapter: ExpenseAdapter
    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_expense_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Expense_list)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.recyclerViewExpenses)
        spinnerFilter = findViewById(R.id.spinnerFilter)

        adapter = ExpenseAdapter(expenseList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        setupFilter()
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // Not used
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val expense = adapter.getExpenseAt(position)

                // Delete from Firestore
                db.collection("users")
                    .document(currentUserId)
                    .collection("expenses")
                    .document(expense.id)
                    .delete()
                    .addOnSuccessListener {
                        adapter.removeItem(position)
                    }
                    .addOnFailureListener {
                        // Optionally show a message or recover the item
                    }

            }



        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupFilter() {
        val filters = arrayOf("Date", "Amount", "Category")
        spinnerFilter.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, filters)

        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = when (filters[position]) {
                    "Amount" -> "amount"
                    "Category" -> "category"
                    else -> "timestamp"
                }
                loadExpensesSortedBy(selected)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadExpensesSortedBy(field: String) {
        db.collection("users").document(currentUserId).collection("expenses")
            .orderBy(field, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                expenseList.clear()
                for (doc in snapshot) {
                    val expense = Expense(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        category = doc.getString("category") ?: "",
                        amount = doc.getDouble("amount") ?: 0.0,
                        timestamp = doc.getTimestamp("timestamp")
                    )
                    expenseList.add(expense)
                }
                adapter.notifyDataSetChanged()
            }
    }




}
