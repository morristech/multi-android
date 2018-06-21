package com.distributedsystems.multi.transactions

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.distributedsystems.multi.R
import com.distributedsystems.multi.TransactionFeedQuery
import com.distributedsystems.multi.db.Wallet
import java.text.DecimalFormat

class TransactionsListAdapter(
        private val currentWallet: Wallet,
        private val transactionsList : List<TransactionFeedQuery.Transaction>,
        private val context: Context
) : RecyclerView.Adapter<TransactionsListAdapter.TransactionHolder>() {

    private val df = DecimalFormat("##.0000000")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.transaction_list_item, parent, false)
        return TransactionHolder(v)
    }

    override fun getItemCount(): Int {
        return transactionsList.size
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val transaction = transactionsList[position]
        val outboundTransaction = transaction.from()!!.hash().toString() == currentWallet.ethAddress
        var hash = ""
        var directionIcon : Drawable? = null

        if(outboundTransaction) {
            directionIcon = ContextCompat.getDrawable(context, R.drawable.ic_transaction_out)
            hash = transaction.to()!!.hash().toString()
        } else {
            directionIcon = ContextCompat.getDrawable(context, R.drawable.ic_transaction_in)
            hash = transaction.from()!!.hash().toString()
        }

        holder.transactionValue.text = context.getString(R.string.value_in_eth, df.format(transaction.value()!!.ether().value))
        holder.transactionDirection.setImageDrawable(directionIcon)
        holder.transactionHash.text = context.getString(R.string.wallet_address_subtext,
                hash.substring(hash.length - 8 until hash.length))

        if(transaction.status()!!) {
            holder.transactionStatus.text = context.getString(R.string.success)
        } else {
            holder.transactionStatus.text = context.getString(R.string.declined)
        }

    }

    class TransactionHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val transactionDirection : ImageView = view.findViewById(R.id.direction_icon)
        val transactionHash : TextView = view.findViewById(R.id.from_to_address)
        val transactionStatus : TextView = view.findViewById(R.id.transaction_status)
        val transactionValue : TextView = view.findViewById(R.id.transaction_value)
    }
}