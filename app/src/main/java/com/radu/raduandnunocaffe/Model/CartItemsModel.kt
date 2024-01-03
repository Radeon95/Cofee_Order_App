package com.radu.raduandnunocaffe.Model

class CartItemsModel {
    var coffeeId: String? = null
    var coffeeImage: String? = null
    var coffeeName: String? = null
    var finalPrice: String? = null
    var isCustomizeAvailable: String? = null
    var quantity: String? = null
    var selectedAdditions: String? = null
    var selectedSize: String? = null
    var selectedSugar: String? = null
    var timeStamp: String? = null
    var uid: String? = null

    constructor()
    constructor(
        coffeeId: String?,
        coffeeImage: String?,
        coffeeName: String?,
        finalPrice: String?,
        isCustomizeAvailable: String?,
        quantity: String?,
        selectedAdditions: String?,
        selectedSize: String?,
        selectedSugar: String?,
        timeStamp: String?,
        uid: String?
    ) {
        this.coffeeId = coffeeId
        this.coffeeImage = coffeeImage
        this.coffeeName = coffeeName
        this.finalPrice = finalPrice
        this.isCustomizeAvailable = isCustomizeAvailable
        this.quantity = quantity
        this.selectedAdditions = selectedAdditions
        this.selectedSize = selectedSize
        this.selectedSugar = selectedSugar
        this.timeStamp = timeStamp
        this.uid = uid
    }
}
