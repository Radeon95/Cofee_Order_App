package com.radu.raduandnunocaffe.Adapters.UserAdapters

import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.radu.raduandnunocaffe.Adapters.UserAdapters.ShowOrdersAdapter.OrdersViewHolder
import com.radu.raduandnunocaffe.Model.OrdersModel
import com.radu.raduandnunocaffe.R
import com.radu.raduandnunocaffe.Show_Order_Details_Activity
import java.util.Calendar

class ShowOrdersAdapter(var context: Context, var ordersList: ArrayList<OrdersModel>) :
    RecyclerView.Adapter<OrdersViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_show_orders, parent, false)
        return OrdersViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val ordersModel = ordersList[position]
        val email = ordersModel.email
        val orderDate = ordersModel.orderDate
        val amount = ordersModel.finalPrice
        val mobile = ordersModel.mobile
        val address = ordersModel.address
        val username = ordersModel.username
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = orderDate?.toLong() ?: 0
        val formatDate = DateFormat.format("dd/MM/yyyy hh:mm a", calendar).toString()
        holder.emailTv.text = email
        holder.orderDateTv.text = formatDate
        holder.orderIdTv.text = orderDate
        holder.amountTv.text = "£ .$amount"
        holder.itemView.setOnClickListener {
            val intent = Intent(context, Show_Order_Details_Activity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("email", email)
            intent.putExtra("orderDate", orderDate)
            intent.putExtra("amount", amount)
            intent.putExtra("mobile", mobile)
            intent.putExtra("address", address)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return ordersList.size
    }

    inner class OrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var orderIdTv: TextView
        var emailTv: TextView
        var orderDateTv: TextView
        var amountTv: TextView

        init {
            emailTv = itemView.findViewById(R.id.emailTv)
            orderDateTv = itemView.findViewById(R.id.orderDateTv)
            amountTv = itemView.findViewById(R.id.amountTv)
            orderIdTv = itemView.findViewById(R.id.orderIdTv)
        }
    }
}
