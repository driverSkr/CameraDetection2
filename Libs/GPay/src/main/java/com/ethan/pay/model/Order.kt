package com.ethan.pay.model

import androidx.annotation.Keep
import com.android.billingclient.api.Purchase

@Keep
class OrderInfo {
    var orderId: String? = null
    var goodsId: String? = null
    var token: String? = null
    var signature: String? = null
    var json: String? = null

    fun createOrderInfo(purchase: Purchase): OrderInfo {
        this.orderId = purchase.orderId
        this.goodsId = if (purchase.products.isNotEmpty()) purchase.products[0] else null
        this.token = purchase.purchaseToken
        this.signature = purchase.signature
        this.json = purchase.originalJson
        return this
    }

}

@Keep
data class Receipt(var receipt: String?, var signature: String?)
