package com.example.jadikaya

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var addTransactionBtn: Button
    private lateinit var closeBtn : ImageButton
    private lateinit var labelInput : TextInputEditText
    private lateinit var amountInput : TextInputEditText
    private lateinit var descInput : TextInputEditText
    private lateinit var labelLayout : TextInputLayout
    private lateinit var amountLayout : TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_transaction)

        addTransactionBtn = findViewById(R.id.addTransactionBtn)
        labelInput = findViewById(R.id.labelInput)
        amountInput = findViewById(R.id.amountInput)
        descInput = findViewById(R.id.descInput)
        labelLayout = findViewById(R.id.labelLayout)
        amountLayout = findViewById(R.id.amountLayout)
        closeBtn = findViewById(R.id.closeBtn)

        addTransactionBtn.setOnClickListener {
            val label = labelInput.text.toString()
            val amount = amountInput.text.toString().toLongOrNull()
            val desc = descInput.text.toString()

            if (label.isEmpty()||amount==null) {
                if (label.isEmpty())
                    labelLayout.error = "Please enter a valid label"
                else
                    labelLayout.error = null  // Clear error when input is valid

                if (amount == null)
                    amountLayout.error = "Please enter a valid amount"
                else
                    amountLayout.error = null  // Clear error when input is valid
            } else
            {
                val transaction = Transaction(0, label, amount, desc)
                insert(transaction)
            }
        }

        closeBtn.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun insert(transaction: Transaction){
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "transactions").build()

        GlobalScope.launch{
            db.transactionDao().insertAll(transaction)
            finish()
        }
    }

}