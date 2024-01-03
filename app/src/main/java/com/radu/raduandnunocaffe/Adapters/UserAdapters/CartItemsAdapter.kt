package com.radu.raduandnunocaffe.Adapters.UserAdapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.radu.raduandnunocaffe.Adapters.UserAdapters.CartItemsAdapter.CartViewHolder
import com.radu.raduandnunocaffe.Model.CartItemsModel
import com.radu.raduandnunocaffe.R
import com.squareup.picasso.Picasso

class CartItemsAdapter(var context: Context, var cartItemsList: ArrayList<CartItemsModel>) :
    RecyclerView.Adapter<CartViewHolder>() {
    var mAuth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItemsModel = cartItemsList[position]
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val coffeeName = cartItemsModel.coffeeName
        val isCustomizeAvailable = cartItemsModel.isCustomizeAvailable
        val finalPrice = cartItemsModel.finalPrice
        val coffeeImage = cartItemsModel.coffeeImage
        val quantity = cartItemsModel.quantity
        val timestamp = cartItemsModel.timeStamp
        val uid = cartItemsModel.uid
        holder.coffeeName.text = coffeeName
        holder.itemPrice.text = "Rs. $finalPrice ($quantity)"
        if ((isCustomizeAvailable == "true")) {
            val selectedSize = cartItemsModel.selectedSize
            val selectedSugar = cartItemsModel.selectedSugar
            val selectedAdditions = cartItemsModel.selectedAdditions
            holder.customizeItemsLL.visibility = View.VISIBLE
            holder.cardBackground.setBackgroundResource(R.drawable.card_bg)
            if ((selectedSize == "small")) {
                holder.sizeTv.text = "(" + "S" + ")"
                holder.sizeIv.setImageResource(R.drawable.small_tint)
            } else if ((selectedSize == "medium")) {
                holder.sizeTv.text = "(" + "M" + ")"
                holder.sizeIv.setImageResource(R.drawable.medium_tint)
            } else if ((selectedSize == "large")) {
                holder.sizeTv.text = "(" + "L" + ")"
                holder.sizeIv.setImageResource(R.drawable.large_tint)
            } else {
            }
            if ((selectedSugar == "noSugar")) {
                holder.sugarTypeIv.setImageResource(R.drawable.no_sugar_tint)
            } else if ((selectedSugar == "smallSugar")) {
                holder.sugarTypeIv.setImageResource(R.drawable.one_sugar_tint)
            } else if ((selectedSugar == "mediumSugar")) {
                holder.sugarTypeIv.setImageResource(R.drawable.medium_sugar_tint)
            } else if ((selectedSugar == "highSugar")) {
                holder.sugarTypeIv.setImageResource(R.drawable.high_sugar_tint)
            } else {
            }
            if ((selectedAdditions == "empty")) {
                holder.additionsLL.visibility = View.GONE
            } else {
                holder.additionsLL.visibility = View.VISIBLE
                if ((selectedAdditions == "cream")) {
                    holder.additionsIv.setImageResource(R.drawable.addition_cream_tint)
                } else if ((selectedAdditions == "sticker")) {
                    holder.additionsIv.setImageResource(R.drawable.addition_ice_tint)
                } else {
                }
            }
        } else {
            holder.customizeItemsLL.visibility = View.GONE
            holder.cardBackground.setBackgroundResource(R.drawable.card_bg)
        }
        try {
            Picasso.get().load(coffeeImage).placeholder(R.drawable.spinner).into(holder.CoffeeImage)
        } catch (e: Exception) {
            holder.CoffeeImage.setImageResource(R.drawable.spinner)
        }
        holder.deleteBtn.setOnClickListener {
            if (timestamp != null) {
                if (uid != null) {
                    LoadDeleteDialogBox(timestamp, uid)
                }
            }
        }
    }

    private fun LoadDeleteDialogBox(timestamp: String, uid: String) {
        Log.d("UIDDD", uid)
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete")
        builder.setMessage("Do you want to delete this item?")
        builder.setPositiveButton("Delete") { dialogInterface, i ->
            DeleteItem(
                timestamp,
                dialogInterface,
                uid
            )
        }
            .setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }
        builder.show()
    }

    private fun DeleteItem(timestamp: String, dialogInterface: DialogInterface, uid: String) {
        val databaseReference = database!!.getReference("Users")
        databaseReference.child(uid).child("CartItems").child(timestamp).removeValue()
            .addOnSuccessListener(
                OnSuccessListener {
                    dialogInterface.dismiss()
                    Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show()
                })
    }

    override fun getItemCount(): Int {
        return cartItemsList.size
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var coffeeName: TextView
        var itemPrice: TextView
        var sizeTv: TextView
        var customizeItemsLL: LinearLayout
        var additionsLL: LinearLayout
        var CoffeeImage: ImageView
        var sizeIv: ImageView
        var sugarTypeIv: ImageView
        var additionsIv: ImageView
        var cardBackground: RelativeLayout
        var deleteBtn: ImageButton

        init {
            coffeeName = itemView.findViewById(R.id.coffeeName)
            itemPrice = itemView.findViewById(R.id.itemPrice)
            sizeTv = itemView.findViewById(R.id.sizeTv)
            customizeItemsLL = itemView.findViewById(R.id.customizeItemsLL)
            CoffeeImage = itemView.findViewById(R.id.CoffeeImage)
            sizeIv = itemView.findViewById(R.id.sizeIv)
            sugarTypeIv = itemView.findViewById(R.id.sugarTypeIv)
            additionsIv = itemView.findViewById(R.id.additionsIv)
            cardBackground = itemView.findViewById(R.id.cardBackground)
            deleteBtn = itemView.findViewById(R.id.deleteBtn)
            additionsLL = itemView.findViewById(R.id.additionsLL)
        }
    }
}
