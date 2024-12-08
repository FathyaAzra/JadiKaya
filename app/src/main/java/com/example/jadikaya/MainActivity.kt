package com.example.jadikaya

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var deleteTransaction : Transaction
    private lateinit var transactions: List<Transaction>
    private lateinit var oldtransactions: List<Transaction>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var linearlayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView

    private lateinit var totalamount: TextView
    private lateinit var uangmasuk: TextView
    private lateinit var uangkeluar: TextView
    private lateinit var tambah: FloatingActionButton

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Link the TextViews to their respective IDs in the XML layout
        totalamount = findViewById(R.id.totalamount)
        uangmasuk = findViewById(R.id.uangmasuk)
        uangkeluar = findViewById(R.id.uangkeluar)
        tambah = findViewById(R.id.tambah)

        recyclerView = findViewById(R.id.recyclerview)
        transactions = arrayListOf()

        transactionAdapter = TransactionAdapter(transactions)
        linearlayoutManager = LinearLayoutManager(this)

        db= Room.databaseBuilder(this, AppDatabase::class.java, "transactions").build()
        recyclerView.apply {
            adapter = transactionAdapter
            layoutManager = linearlayoutManager
        }

        val itemTouchHelper = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTransaction(transactions[viewHolder.adapterPosition])
            }
        }

        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(recyclerView)


        tambah.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent) // Start the AddTransactionActivity when the button is clicked
        }
    }

    private fun fetchAll(){
        GlobalScope.launch {
//            db.transactionDao().insertAll(Transaction())
            transactions = db.transactionDao().getAll()

            runOnUiThread {
                updateDashboard()
                transactionAdapter.setData(transactions)
            }
        }
    }
    private fun updateDashboard() {
        val totalAmount: Long = transactions.map { it.amount }.sum()
        val Pemasukan: Long = transactions.filter { it.amount > 0 }.map { it.amount }.sum()
        val Pengeluaran: Long = transactions.filter { it.amount <= 0 }.map { it.amount }.sum()

        // Set the formatted values to the TextViews
        totalamount.text = "RP %,d".format(totalAmount)
        uangmasuk.text = "RP %,d".format(Pemasukan)
        uangkeluar.text = "RP %,d".format(Pengeluaran)
    }

    private fun deleteTransaction(transaction: Transaction){
        deleteTransaction = transaction
        oldtransactions = transactions

        GlobalScope.launch {
            // Delete transaction from the database
            db.transactionDao().delete(transaction)

            // Update the local list by removing the transaction
            transactions = transactions.filter { it.id != transaction.id }

            // Run on the main thread to update UI
            runOnUiThread {
                // Notify the adapter that the item has been removed
                transactionAdapter.setData(transactions)
                updateDashboard()

                // You can also use this if you prefer to call the specific item removal:
                val position = oldtransactions.indexOf(transaction)
                if (position >= 0) {
                    transactionAdapter.notifyItemRemoved(position)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        fetchAll()
    }
}
