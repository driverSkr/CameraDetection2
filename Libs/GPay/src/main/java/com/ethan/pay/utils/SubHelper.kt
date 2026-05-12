package com.ethan.pay.utils

object SubHelper {

    /**
     * 订阅商品id
     */
    private const val product_id_sub = "spy.yearly"

    /**
     * 订阅商品plan_id
     */
    private const val plan_id_week = "spy-weekly-basic"
    private const val plan_id_month = "spy-monthly-basic"
    private const val plan_id_year = "spy-yearly-basic"

    private const val offer_id_month = "spy-monthly-first-discount"
    private const val offer_id_year = "spy-yearly-first-discount"

    /**
     * todo 请注意，如果添加了lifetime套餐，请向该list添加！！！，不然无法识别lifetime权益,以及积分包添加！！！！
     */
    val listLifeGoodsList = listOf<String>()

    fun getProductId(): String {
        return product_id_sub
    }

    fun getWeekPlanId(): String {
        return plan_id_week
    }

    fun getWeekSkuId(): String {
        return product_id_sub
    }

    fun getMonthPlanId(): String {
        return plan_id_month
    }

    fun getMonthOfferId(): String {
        return offer_id_month
    }

    fun getMonthSkuId(): String {
        return product_id_sub
    }

    fun getYearPlanId(): String {
        return plan_id_year
    }

    fun getYearOfferId(): String {
        return offer_id_year
    }

    fun getYearSkuId(): String {
        return product_id_sub
    }
}