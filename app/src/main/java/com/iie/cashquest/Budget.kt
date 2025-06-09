package com.iie.cashquest

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_budget")
data class MonthlyBudget(
    @PrimaryKey val monthYear: String, // Format "MM-yyyy"
    val amount: Double
)