package com.example.jadikaya

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(private var transactions : List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionHolder> (){

    class TransactionHolder(view : View) : RecyclerView.ViewHolder(view){
        val label: TextView = view.findViewById(R.id.nama)
        val amount: TextView = view.findViewById(R.id.banyak)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout, parent, false)
        return TransactionHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val transaction : Transaction = transactions[position]
        val context : Context = holder.amount.context

        if(transaction.amount>=0){
            holder.amount.text= "+RP%d".format(transaction.amount)
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.blue))
        }else{
            holder.amount.text= "-RP%d".format(Math.abs(transaction.amount))
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
        }

        holder.label.text= transaction.label

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailedActivity::class.java)
            intent.putExtra("transaction", transaction)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun setData(transactions: List<Transaction>){
        this.transactions = transactions
        notifyDataSetChanged()
    }
}