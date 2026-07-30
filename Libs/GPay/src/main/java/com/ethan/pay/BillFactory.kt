package com.ethan.pay

import android.content.Context
import com.ethan.pay.impl.ClientController
import com.ethan.pay.impl.GPayImpl
import com.ethan.pay.impl.PurchaseType
import com.ethan.pay.lifetime.LifeTimeImpl
import com.ethan.pay.model.OrderInfo
import com.ethan.pay.onetime.OneTimeImpl
import com.ethan.pay.subs.SubscribeImpl


object BillFactory {

    private var subscribeImpl: GPayImpl? = null
    private var lifeTimeImpl: GPayImpl? = null
    private var oneTimeImpl: GPayImpl? = null

    suspend fun init(context: Context): Int {
        return ClientController.connect(context)
    }

    fun getSubscribe(): GPayImpl {
        if (subscribeImpl == null) subscribeImpl = SubscribeImpl()
        return subscribeImpl!!
    }

    fun getLifeTime(): GPayImpl {
        if (lifeTimeImpl == null) lifeTimeImpl = LifeTimeImpl()
        return lifeTimeImpl!!
    }

    fun getOneTime(): GPayImpl {
        if (oneTimeImpl == null) oneTimeImpl = OneTimeImpl()
        return oneTimeImpl!!
    }



    /**
     * 不要在多个地方设置这个变量！！！！！！！！！！！！！！！！！！！
     */
    var globalSuccessCallBack: (orderList: MutableList<OrderInfo>, type: PurchaseType) -> Unit = { _, _ -> }
        set(value) {
            ClientController.globalSuccessCallBack = value
            field = value
        }
        get() {
            return ClientController.globalSuccessCallBack
        }
}
