package com.iie.cashquest

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class ViewCategory : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private val categoryList = mutableListOf<Category>()
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_category)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ViewCategories)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.ViewCategories)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CategoryAdapter(categoryList,
            onEdit = { category ->
                showEditDialog(category)
            }
        )
        recyclerView.adapter = adapter

        loadCategories()
        setupSwipeToDelete()
    }

    private fun loadCategories() {
        db.collection("users").document(uid).collection("categories")
            .orderBy("createdAt")
            .get()
            .addOnSuccessListener { snapshot ->
                categoryList.clear()
                for (doc in snapshot) {
                    categoryList.add(Category(doc.id, doc.getString("name") ?: ""))
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun showEditDialog(category: Locale.Category) {
        val editText = EditText(this).apply {
            hint = "Enter new name"
            inputType = InputType.TYPE_CLASS_TEXT
            setText(category.name)
        }

        AlertDialog.Builder(this)
            .setTitle("Edit Category")
            .setView(editText)
            .setPositiveButton("Update") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    db.collection("users").document(uid)
                        .collection("categories").document(category.name)
                        .update("name", newName)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Category updated", Toast.LENGTH_SHORT).show()
                            loadCategories()
                        }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun onSwiped(holder: RecyclerView.ViewHolder, direction: Int) {
                val category = categoryList[holder.adapterPosition]
                db.collection("users").document(uid)
                    .collection("categories").document(category.id)
                    .delete()
                    .addOnSuccessListener {
                        categoryList.removeAt(holder.adapterPosition)
                        adapter.notifyItemRemoved(holder.adapterPosition)
                        Toast.makeText(this@ViewCategory, "Deleted", Toast.LENGTH_SHORT).show()
                    }
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}
