package com.radu.raduandnunocaffe.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.radu.raduandnunocaffe.Coffee_Details_Activity
import com.radu.raduandnunocaffe.Filters.CoffeeFilter
import com.radu.raduandnunocaffe.Model.CoffeeModel
import com.radu.raduandnunocaffe.R
import com.squareup.picasso.Picasso

class CoffeeAdapter(var context: Context, @JvmField var coffeeList: ArrayList<CoffeeModel>) :
    RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder>(), Filterable {
    var filterList: ArrayList<CoffeeModel>
    var filter: CoffeeFilter? = null

    init {
        filterList = coffeeList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.raw_menu_item, parent, false)
        return CoffeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoffeeViewHolder, position: Int) {
        val coffeeModel = coffeeList[position]
        val coffeeName = coffeeModel.coffee_name
        val coffeeImage = coffeeModel.coffee_image
        val coffeePrice = coffeeModel.price
        val isCustomizeAvailable = coffeeModel.isCustomizeCusAvailable
        holder.coffeeName.text = coffeeName
        try {
            Picasso.get().load(coffeeImage).placeholder(R.drawable.spinner).into(holder.CoffeeImage)
        } catch (e: Exception) {
            holder.CoffeeImage.setImageResource(R.drawable.spinner)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, Coffee_Details_Activity::class.java)
            intent.putExtra("coffeeName", coffeeName)
            intent.putExtra("coffeePrice", coffeePrice)
            intent.putExtra("coffeeImage", coffeeImage)
            intent.putExtra("isCustomizeAvailable", isCustomizeAvailable)
            context.startActivity(intent)
        }
        holder.more.setOnClickListener {
            val intent = Intent(context, Coffee_Details_Activity::class.java)
            intent.putExtra("coffeeName", coffeeName)
            intent.putExtra("coffeePrice", coffeePrice)
            intent.putExtra("coffeeImage", coffeeImage)
            intent.putExtra("isCustomizeAvailable", isCustomizeAvailable)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return coffeeList.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = CoffeeFilter(this, filterList)
        }
        return filter as CoffeeFilter
    }

    inner class CoffeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var coffeeName: TextView
        var CoffeeImage: ImageView
        var more: ImageButton

        init {
            coffeeName = itemView.findViewById(R.id.coffeeName)
            CoffeeImage = itemView.findViewById(R.id.CoffeeImage)
            more = itemView.findViewById(R.id.more)
        }
    }
}
