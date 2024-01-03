package com.radu.raduandnunocaffe.Adapters.UserAdapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.radu.raduandnunocaffe.Filters.HomeCoffeeFilter
import com.radu.raduandnunocaffe.Model.CoffeeModel
import com.radu.raduandnunocaffe.R
import com.radu.raduandnunocaffe.User_Coffee_Details_Activity
import com.squareup.picasso.Picasso

class UserCoffeeAdapter(var context: Context, @JvmField var coffeeList: ArrayList<CoffeeModel>) :
    RecyclerView.Adapter<UserCoffeeAdapter.CoffeeViewHolder>(), Filterable {
    var filterList: ArrayList<CoffeeModel>
    var filter: HomeCoffeeFilter? = null

    init {
        filterList = coffeeList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.raw_user_menu_item_, parent, false)
        return CoffeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoffeeViewHolder, position: Int) {
        val coffeeModel = coffeeList[position]
        val coffeeName = coffeeModel.coffee_name
        val coffeeImage = coffeeModel.coffee_image
        val coffeePrice = coffeeModel.price
        val coffeeId = coffeeModel.coffeeId
        val isCustomizeAvailable = coffeeModel.isCustomizeCusAvailable
        holder.coffee_name.text = coffeeName
        try {
            Picasso.get().load(coffeeImage).placeholder(R.drawable.spinner)
                .into(holder.coffee_image)
        } catch (e: Exception) {
            holder.coffee_image.setImageResource(R.drawable.spinner)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, User_Coffee_Details_Activity::class.java)
            intent.putExtra("coffeeName", coffeeName)
            intent.putExtra("coffeePrice", coffeePrice)
            intent.putExtra("coffeeImage", coffeeImage)
            intent.putExtra("coffeeId", coffeeId)
            intent.putExtra("isCustomizeAvailable", isCustomizeAvailable)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return coffeeList.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = HomeCoffeeFilter(this, filterList)
        }
        return filter as HomeCoffeeFilter
    }

    inner class CoffeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var price: TextView
        var coffee_name: TextView
        var coffee_image: ImageView

        init {
            price = itemView.findViewById(R.id.price)
            coffee_image = itemView.findViewById(R.id.coffee_image)
            coffee_name = itemView.findViewById(R.id.coffee_name)
        }
    }
}
