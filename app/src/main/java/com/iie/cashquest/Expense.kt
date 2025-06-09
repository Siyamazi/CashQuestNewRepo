package com.iie.cashquest

data class Expense(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val amount: Double = 0.0,
    val timestamp: com.google.firebase.Timestamp? = null
)