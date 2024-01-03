package com.radu.raduandnunocaffe.Model

class CoffeeModel {
    var coffeeId: String? = null
    var coffee_image: String? = null
    var coffee_name: String? = null
    var price: String? = null
    var quantity: String? = null
    var timestamp: String? = null
    var isCustomizeCusAvailable: String? = null

    constructor()
    constructor(
        coffeeId: String?,
        coffee_image: String?,
        coffee_name: String?,
        price: String?,
        quantity: String?,
        timestamp: String?,
        isCustomizeCusAvailable: String?
    ) {
        this.coffeeId = coffeeId
        this.coffee_image = coffee_image
        this.coffee_name = coffee_name
        this.price = price
        this.quantity = quantity
        this.timestamp = timestamp
        this.isCustomizeCusAvailable = isCustomizeCusAvailable
    }
}
