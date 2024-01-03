package com.radu.raduandnunocaffe.Filters

import android.widget.Filter
import com.radu.raduandnunocaffe.Adapters.CoffeeAdapter
import com.radu.raduandnunocaffe.Model.CoffeeModel
import java.util.Locale

class CoffeeFilter(var coffeeAdapter: CoffeeAdapter, var coffeeModelList: ArrayList<CoffeeModel>) :
    Filter() {
    override fun performFiltering(charSequence: CharSequence): FilterResults {
        var charSequence: CharSequence? = charSequence
        val results = FilterResults()
        if (charSequence != null && charSequence.length > 0) {
            charSequence = charSequence.toString().uppercase(Locale.getDefault())
            val filterModel = ArrayList<CoffeeModel>()
            for (i in coffeeModelList.indices) {
                if (coffeeModelList[i].coffee_name?.uppercase(Locale.getDefault())
                        ?.contains(charSequence) == true
                ) {
                    filterModel.add(coffeeModelList[i])
                }
            }
            results.count = filterModel.size
            results.values = filterModel
        } else {
            results.count = coffeeModelList.size
            results.values = coffeeModelList
        }
        return results
    }

    override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
        coffeeAdapter.coffeeList = filterResults.values as ArrayList<CoffeeModel>
        coffeeAdapter.notifyDataSetChanged()
    }
}
