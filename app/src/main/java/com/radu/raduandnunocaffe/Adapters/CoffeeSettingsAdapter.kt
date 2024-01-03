package com.radu.raduandnunocaffe.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.radu.raduandnunocaffe.Admin.Fragments.Coffee_Edit_Popup_Fragment
import com.radu.raduandnunocaffe.Filters.CoffeeSettingsFilter
import com.radu.raduandnunocaffe.Model.CoffeeModel
import com.radu.raduandnunocaffe.R
import com.squareup.picasso.Picasso

class CoffeeSettingsAdapter(var context: Context, @JvmField var coffeeList: ArrayList<CoffeeModel>) :
    RecyclerView.Adapter<CoffeeSettingsAdapter.CoffeeViewHolder>(), Filterable {
    var filterList: ArrayList<CoffeeModel>
    var filter: CoffeeSettingsFilter? = null

    init {
        filterList = coffeeList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.raw_menu_item_edit, parent, false)
        return CoffeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoffeeViewHolder, position: Int) {
        val coffeeModel = coffeeList[position]
        val coffeeImage = coffeeModel.coffee_image
        val coffeeName = coffeeModel.coffee_name
        val coffeePrice = coffeeModel.price
        val isCustomizeAvailable = coffeeModel.isCustomizeCusAvailable
        val Quantity = coffeeModel.quantity
        val coffeeId = coffeeModel.coffeeId
        try {
            Picasso.get().load(coffeeImage).placeholder(R.drawable.spinner).into(holder.coffeeImage)
        } catch (e: Exception) {
            holder.coffeeImage.setImageResource(R.drawable.spinner)
        }
        holder.coffeeName.text = coffeeName
        holder.edit.setOnClickListener {
            val options = arrayOf("Edit", "Delete")
            val builder = AlertDialog.Builder(
                context
            )
            builder.setTitle("Select").setItems(options) { dialogInterface, i ->
                if (i == 0) {
                    val bundle = Bundle()
                    bundle.putString("coffeeImage", coffeeImage)
                    bundle.putString("coffeeName", coffeeName)
                    bundle.putString("coffeePrice", coffeePrice)
                    bundle.putString("isCustomizeAvailable", isCustomizeAvailable)
                    bundle.putString("Quantity", Quantity)
                    bundle.putString("coffeeId", coffeeId)
                    val coffee_edit_popup_fragment = Coffee_Edit_Popup_Fragment()
                    coffee_edit_popup_fragment.arguments = bundle
                    coffee_edit_popup_fragment.show(
                        (context as AppCompatActivity).supportFragmentManager,
                        coffee_edit_popup_fragment.javaClass.simpleName
                    )
                    dialogInterface.dismiss()
                } else {
                    dialogInterface.dismiss()
                    if (coffeeId != null) {
                        LoadDeleteDialog(coffeeId)
                    }
                }
            }
            builder.show()
        }
    }

    private fun LoadDeleteDialog(coffeeId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("CoffeeMenu")
        val builder1 = AlertDialog.Builder(
            context
        )
        builder1.setTitle("Delete Product")
        builder1.setMessage("You want to delete coffee?")
        builder1.setPositiveButton("Delete") { dialogInterface, i ->
            databaseReference.child(coffeeId).removeValue().addOnSuccessListener {
                Toast.makeText(context, "Coffee Deleted", Toast.LENGTH_SHORT).show()
                dialogInterface.dismiss()
            }
        }
            .setNegativeButton("No") { dialogInterface, i -> dialogInterface.dismiss() }
        builder1.show()
    }

    override fun getItemCount(): Int {
        return coffeeList.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = CoffeeSettingsFilter(this, filterList)
        }
        return filter as CoffeeSettingsFilter
    }

    inner class CoffeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var coffeeImage: ImageView
        var coffeeName: TextView
        var edit: ImageButton

        init {
            coffeeImage = itemView.findViewById(R.id.CoffeeImage)
            coffeeName = itemView.findViewById(R.id.coffeeName)
            edit = itemView.findViewById(R.id.edit)
        }
    }
}
