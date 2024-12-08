package com.example.jadikaya

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailedActivity : AppCompatActivity() {

    private lateinit var updateBtn: Button
    private lateinit var closeBtn : ImageButton
    private lateinit var labelInput : TextInputEditText
    private lateinit var amountInput : TextInputEditText
    private lateinit var descInput : TextInputEditText
    private lateinit var labelLayout : TextInputLayout
    private lateinit var amountLayout : TextInputLayout
    private lateinit var rootView : View
    private lateinit var transaction : Transaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detailed)

        transaction = intent.getSerializableExtra("transaction") as Transaction

        updateBtn = findViewById(R.id.updateBtn)
        labelInput = findViewById(R.id.labelInput)
        amountInput = findViewById(R.id.amountInput)
        descInput = findViewById(R.id.descInput)
        labelLayout = findViewById(R.id.labelLayout)
        amountLayout = findViewById(R.id.amountLayout)
        closeBtn = findViewById(R.id.closeBtn)
        rootView = findViewById(R.id.rootView)

        labelInput.setText(transaction.label)
        amountInput.setText(transaction.amount.toString())
        descInput.setText(transaction.desc)

        rootView.setOnClickListener{
            this.window.decorView.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        updateBtn.setOnClickListener {
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
                updateBtn.visibility= View.VISIBLE
                val transaction = Transaction(transaction.id, label, amount, desc)
                update(transaction)
            }
        }
        closeBtn.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun update(transaction: Transaction){
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "transactions").build()

        GlobalScope.launch{
            db.transactionDao().update(transaction)
            finish()
        }
    }

}