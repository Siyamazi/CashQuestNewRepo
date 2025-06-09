package com.iie.cashquest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private val expenses: MutableList<Expense>
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.tvTitle.text = expense.title
        holder.tvCategory.text = "Category: ${expense.category}"
        holder.tvAmount.text = "Amount: R${expense.amount}"
        holder.tvDate.text = "Date: ${expense.timestamp?.toDate()?.toString()?.substring(0, 10)}"
    }

    override fun getItemCount(): Int = expenses.size

    fun removeItem(position: Int) {
        expenses.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getExpenseAt(position: Int): Expense = expenses[position]
}
