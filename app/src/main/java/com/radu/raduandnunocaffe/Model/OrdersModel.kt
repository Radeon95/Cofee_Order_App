package com.radu.raduandnunocaffe.Model

class OrdersModel {
    var username: String? = null
    var email: String? = null
    var address: String? = null
    var finalPrice: String? = null
    var mobile: String? = null
    var orderDate: String? = null
    var isExpandable = false

    constructor()
    constructor(
        username: String?,
        email: String?,
        address: String?,
        finalPrice: String?,
        mobile: String?,
        orderDate: String?,
        isExpandable: Boolean
    ) {
        this.username = username
        this.email = email
        this.address = address
        this.finalPrice = finalPrice
        this.mobile = mobile
        this.orderDate = orderDate
        this.isExpandable = isExpandable
    }
}
