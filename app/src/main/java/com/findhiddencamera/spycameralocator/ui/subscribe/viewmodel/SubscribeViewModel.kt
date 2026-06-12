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
    private val tag = "subscribe"

    var isBuySuccess: MutableState<Int> = mutableIntStateOf(0)
    var isBuyDiscordSuccess: MutableState<Int> = mutableIntStateOf(0)

    suspend fun querySubProduct(context: Context) = suspendCoroutine { suspendCoroutine ->
        viewModelScope.launch(Dispatchers.Default) {
            val planList = listOf(
                SubscribePlanConfig(SubHelper.getMonthPlanId(), SubHelper.getMonthSkuId(), SubHelper.getMonthOfferId()),
                SubscribePlanConfig(SubHelper.getWeekPlanId(), SubHelper.getWeekSkuId(), ""),
                SubscribePlanConfig(SubHelper.getYearPlanId(), SubHelper.getYearSkuId(), SubHelper.getYearOfferId())
            )
            suspendCoroutine.resume(querySubProductList(context, planList))
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
            val subscribePlanList = planList.mapIndexed { index, planId ->
                SubscribePlanConfig(planId, skuList[index], getOfferIdByPlanId(planId))
            }
            suspendCoroutine.resume(querySubProductList(context, subscribePlanList))
        }
    }

    fun buySubscribe(model: SubModel?, activity: FragmentActivity, dialog: MutableState<Boolean>) {
        viewModelScope.launch {
            val planId = model?.id.toString()
            Log.d(tag, "购买订阅 planId：$planId")
            val goods = Goods(model?.goods ?: SubHelper.getProductId(), planId, model?.offerId ?: "", model?.sku ?: SubHelper.getWeekSkuId())
            withContext(Dispatchers.Main) {
                dialog.value = false
            }
            Log.d(tag, "购买订阅参数：productId=${goods.productId}, planId=${goods.planId}, offerId=${goods.offerId}, skuId=${goods.skuId}")
            BillFactory.getSubscribe().launchBilling(activity, goods, object : OnPayResultCallback {
                override fun begin() {
                    Log.d(tag, "InApp Billing 购买订阅开始")
                }

                override fun onSuccess(orderList: MutableList<OrderInfo>) {
                    Log.d(tag, "InApp Billing 购买订阅成功")
                    // 支付成功后立即更新全局订阅状态，避免等待页面重新进入前台。
                    SubscribeHelper.updateSubscribeState(true)
                    SubscribeHelper.refreshSubscribeState()
                    viewModelScope.launch(Dispatchers.Main) {
                        if (model?.offerId.isNullOrBlank()) {
                            isBuySuccess.value = 1
                        } else {
                            isBuyDiscordSuccess.value = 1
                        }
                    }
                }

                override fun onOwned(orderList: MutableList<OrderInfo>) {
                    Log.d(tag, "InApp Billing 已拥有订阅")
                    // 已拥有也视为订阅有效，并后台同步一次真实订单列表。
                    SubscribeHelper.updateSubscribeState(true)
                    SubscribeHelper.refreshSubscribeState()
                    viewModelScope.launch(Dispatchers.Main) {
                        if (model?.offerId.isNullOrBlank()) {
                            isBuySuccess.value = 1
                        } else {
                            isBuyDiscordSuccess.value = 1
                        }
                    }
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

                    Log.d(tag, "InApp Billing 购买订阅失败: $msg")
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
                    Log.d(tag, "InApp Billing GooglePlay连接中断")
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
                    Log.d(tag, "InApp Billing 购买订阅取消")
                }
            })
        }
    }

    private suspend fun querySubProductList(context: Context, planList: List<SubscribePlanConfig>): MutableList<SubModel>? {
        var isQueryPrice = false
        val list = mutableListOf<SubModel>()
        planList.forEach { config ->
            val model = createSubModel(context, config)
            if (!model.price.isNullOrBlank() && model.price != "0.00") {
                isQueryPrice = true
                list.add(model)
            } else {
                Log.e(tag, "忽略无效订阅套餐：planId=${config.planId}, offerId=${config.offerId}, price=${model.price}")
            }
        }
        if (!isQueryPrice) {
            Log.e(tag, "订阅商品查询失败：所有套餐价格为空或为0，productId=${SubHelper.getProductId()}")
            return null
        }
        return list
    }

    private suspend fun createSubModel(context: Context, config: SubscribePlanConfig): SubModel {
        val baseGoods = Goods(SubHelper.getProductId(), config.planId, "", config.skuId)
        val basePrices = BillFactory.getSubscribe().getGoodsPrice(context, baseGoods)
        val discountGoods = baseGoods.copy(offerId = config.offerId)
        val hasDiscount = config.offerId.isNotBlank() && BillFactory.getSubscribe().hasDiscount(discountGoods)
        val prices = if (hasDiscount) {
            BillFactory.getSubscribe().getDiscountPrice(context, discountGoods)
        } else {
            basePrices
        }
        if (config.offerId.isNotBlank() && !hasDiscount) {
            // 优惠不是所有用户都能拿到，拿不到时回落到基础方案，避免购买时offerToken为空。
            Log.w(tag, "订阅优惠不可用，回落基础方案：planId=${config.planId}, offerId=${config.offerId}")
        }
        return SubModel().apply {
            goods = SubHelper.getProductId()
            id = config.planId
            offerId = if (hasDiscount) config.offerId else ""
            sku = config.skuId
            price = prices[0] ?: ""
            offerprice = basePrices[0] ?: ""
            currency = prices[1] ?: basePrices[1] ?: ""
            isFreeTrial = false
        }.also { model ->
            Log.d(
                tag,
                "订阅套餐查询完成：productId=${model.goods}, planId=${model.id}, offerId=${model.offerId}, price=${model.price}, currency=${model.currency}"
            )
        }
    }

    private fun getOfferIdByPlanId(planId: String): String {
        return when (planId) {
            SubHelper.getMonthPlanId() -> SubHelper.getMonthOfferId()
            SubHelper.getYearPlanId() -> SubHelper.getYearOfferId()
            else -> ""
        }
    }

    private data class SubscribePlanConfig(
        val planId: String,
        val skuId: String,
        val offerId: String
    )
}
