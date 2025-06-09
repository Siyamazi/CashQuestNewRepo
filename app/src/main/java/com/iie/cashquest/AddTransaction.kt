package com.iie.cashquest

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.google.android.material.textfield.TextInputLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddTransaction : AppCompatActivity() {

    // Declare view variables
    private lateinit var labelInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var amountInput: EditText
    private lateinit var addTransactionBtn: Button
    private lateinit var labelLayout: TextInputLayout
    private lateinit var amountLayout: TextInputLayout
    private lateinit var transaction: Transaction
  //  private lateinit var closeBtn: Button

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_transaction)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_add_transaction)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        labelInput = findViewById(R.id.labelInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        amountInput = findViewById(R.id.amountInput)
        addTransactionBtn = findViewById(R.id.addTransactionBtn)
        labelLayout = findViewById(R.id.labelLayout)
        amountLayout = findViewById(R.id.amountLayout)

        // Add validation listeners
        labelInput.addTextChangedListener {
            if (it!!.isNotEmpty())
                labelLayout.error = null
        }

        amountInput.addTextChangedListener {
            if (it!!.isNotEmpty())
                amountLayout.error = null
        }

        addTransactionBtn.setOnClickListener {
            val label = labelInput.text.toString()
            val description = descriptionInput.text.toString()

            val amount = amountInput.text.toString().toDoubleOrNull()

            if (label.isEmpty()) {
                labelLayout.error = "Please enter a valid label"
            } else if (amount == null) {
                amountLayout.error = "Please enter a valid amount"
            }
            else {

            }

        }
        val closeBtn = findViewById<ImageButton>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            finish()
        }

        fun insert(transaction: Transaction){
            val db = Room.databaseBuilder(this,
                AppDatabase::class.java,
                "transactions").build()

            GlobalScope.launch {
                db.transactionDao().insertAll(transaction)
                finish()
            }
        }
    }



}
