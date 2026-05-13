package com.findhiddencamera.spycameralocator.ui.subscribe.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ethan.pay.BillFactory
import com.ethan.pay.model.Goods
import com.ethan.pay.model.OnPayResultCallback
import com.ethan.pay.model.OrderInfo
import com.ethan.pay.utils.SubHelper
import com.findhiddencamera.spycameralocator.model.SubModel
import com.findhiddencamera.spycameralocator.utils.SubscribeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SubscribeViewModel: ViewModel() {
    var isBuySuccess: MutableState<Int> = mutableIntStateOf(0)
    var isBuyDiscordSuccess: MutableState<Int> = mutableIntStateOf(0)

    suspend fun querySubProduct(context: Context) = suspendCoroutine { suspendCoroutine ->
        viewModelScope.launch(Dispatchers.Default) {
            var isQueryPrice = false
            val goodsList = arrayListOf(SubHelper.getProductId(), SubHelper.getProductId(), SubHelper.getProductId())
            val planList = arrayListOf(SubHelper.getMonthPlanId(), SubHelper.getWeekPlanId(), SubHelper.getYearPlanId())
            val offerList = arrayListOf("", "", "")
            val skuList = arrayListOf(SubHelper.getMonthSkuId(), SubHelper.getWeekSkuId(), SubHelper.getYearSkuId())
            val list = mutableListOf<SubModel>()
            for (i in planList.indices) {
                val planId = planList[i]
                val model = SubModel()
                model.goods = goodsList[i]
                model.id = planId
                model.offerId = offerList[i]
                model.sku = skuList[i]
                val goods = Goods(goodsList[i], planId, offerList[i], skuList[i])
                val prices = BillFactory.getSubscribe().getGoodsPrice(context, goods)
                val trial = false
                if (prices[0] != null && prices[0] != "0.00") {
                    isQueryPrice = true
                }
                model.isFreeTrial = trial
                if (prices.size == 2) {
                    model.price = prices[0] ?: ""
                    model.currency = prices[1] ?: ""
                }
                list.add(model)
            }
            if (isQueryPrice) {
                suspendCoroutine.resume(list)
            } else {
                suspendCoroutine.resume(null)
            }
        }
    }

    suspend fun querySplashScreenSubProduct(context: Context, saleMode: Int = 0) = suspendCoroutine { suspendCoroutine ->
        viewModelScope.launch(Dispatchers.Default) {
            // 开屏订阅按销售模式查询不同套餐：0=年+周，1=月，2=年
            val planList = when (saleMode) {
                1 -> arrayListOf(SubHelper.getMonthPlanId())
                2 -> arrayListOf(SubHelper.getYearPlanId())
                else -> arrayListOf(SubHelper.getYearPlanId(), SubHelper.getWeekPlanId())
            }
            val skuList = when (saleMode) {
                1 -> arrayListOf(SubHelper.getMonthSkuId())
                2 -> arrayListOf(SubHelper.getYearSkuId())
                else -> arrayListOf(SubHelper.getYearSkuId(), SubHelper.getWeekSkuId())
            }
            val goodsList = ArrayList<String>().apply {
                repeat(planList.size) {
                    add(SubHelper.getProductId())
                }
            }
            val offerList = ArrayList<String>().apply {
                repeat(planList.size) {
                    add("")
                }
            }
            val list = mutableListOf<SubModel>()
            var isQueryPrice = false
            for (i in planList.indices) {
                val planId = planList[i]
                val model = SubModel()
                model.goods = goodsList[i]
                model.id = planId
                model.offerId = offerList[i]
                model.sku = skuList[i]
                val goods = Goods(goodsList[i], planId, offerList[i], skuList[i])
                val prices = BillFactory.getSubscribe().getGoodsPrice(context, goods)
                val trial = false
                if (prices[0] != null && prices[0] != "0.00") {
                    isQueryPrice = true
                }
                model.isFreeTrial = trial
                if (prices.size == 2) {
                    model.price = prices[0] ?: ""
                    model.currency = prices[1] ?: ""
                }
                list.add(model)
            }
            if (isQueryPrice) {
                suspendCoroutine.resume(list)
            } else {
                suspendCoroutine.resume(null)
            }
        }
    }

    fun buySubscribe(model: SubModel?, activity: FragmentActivity, dialog: MutableState<Boolean>) {
        viewModelScope.launch {
            val planId = model?.id.toString()
            Log.d("subscribe", "购买订阅 planId：$planId")
            val goods = Goods(model?.goods ?: SubHelper.getProductId(), planId, model?.offerId ?: "", model?.sku ?: SubHelper.getWeekSkuId())
            withContext(Dispatchers.Main) {
                dialog.value = false
            }
            println("ethan: $goods")
            BillFactory.getSubscribe().launchBilling(activity, goods, object : OnPayResultCallback {
                override fun begin() {
                    Log.d("subscribe", "InApp Billing 购买订阅开始")
                }

                override fun onSuccess(orderList: MutableList<OrderInfo>) {
                    Log.d("subscribe", "InApp Billing 购买订阅成功")
                    // 支付成功后立即更新全局订阅状态，避免等待页面重新进入前台。
                    SubscribeHelper.updateSubscribeState(true)
                    SubscribeHelper.refreshSubscribeState()
                }

                override fun onOwned(orderList: MutableList<OrderInfo>) {
                    Log.d("subscribe", "InApp Billing 已拥有订阅")
                    // 已拥有也视为订阅有效，并后台同步一次真实订单列表。
                    SubscribeHelper.updateSubscribeState(true)
                    SubscribeHelper.refreshSubscribeState()
                }

                override fun onFailed(msg: String?) {
                    viewModelScope.launch(Dispatchers.Main) {
                        Toast.makeText(activity, "purchase failed", Toast.LENGTH_SHORT).show()
                    }
                    if (model?.offerId.isNullOrBlank()) {
                        isBuySuccess.value = 2
                    } else {
                        isBuyDiscordSuccess.value = 2
                    }

                    Log.d("subscribe", "InApp Billing 购买订阅失败: $msg")
                }

                override fun onDisconnect() {
                    viewModelScope.launch(Dispatchers.Main) {
                        Toast.makeText(activity, "store disconnected", Toast.LENGTH_SHORT).show()
                    }
                    if (model?.offerId.isNullOrBlank()) {
                        isBuySuccess.value = 3
                    } else {
                        isBuyDiscordSuccess.value = 3
                    }
                    Log.d("subscribe", "InApp Billing GooglePlay连接中断")
                }

                override fun onCancel() {
                    if (model?.offerId.isNullOrBlank()) {
                        isBuySuccess.value = 4
                    } else {
                        isBuyDiscordSuccess.value = 4
                    }
                    viewModelScope.launch(Dispatchers.Main) {
                        Toast.makeText(activity, "purchase cancelled", Toast.LENGTH_SHORT).show()
                    }
                    Log.d("subscribe", "InApp Billing 购买订阅取消")
                }
            })
        }
    }
}
