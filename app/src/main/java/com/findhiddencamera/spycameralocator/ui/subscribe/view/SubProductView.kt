package com.findhiddencamera.spycameralocator.ui.subscribe.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.pay.utils.SubHelper
import com.findhiddencamera.spycameralocator.model.SubModel
import com.findhiddencamera.spycameralocator.theme.White
import com.findhiddencamera.spycameralocator.theme.White30
import com.findhiddencamera.spycameralocator.theme.White5
import com.findhiddencamera.spycameralocator.theme.White50
import com.findhiddencamera.spycameralocator.utils.SubscribeHelper

@Composable
fun SubProductView(modifier: Modifier = Modifier, isSelected: Boolean, model: SubModel, onClick: () -> Unit) {

    Box(modifier = modifier
        .height(156.dp)
        .background(
            color = if (isSelected) Color(0xFF5672FF) else White5,
            shape = RoundedCornerShape(10.dp)
        )
        .clickable{ onClick.invoke() }
    ) {
        // todo 折扣需要再计算
        Text("41% OFF", color = White50, fontSize = 16.sp, fontWeight = FontWeight.W500, modifier = Modifier.align(Alignment.TopCenter).padding(top = 10.dp))
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 2.dp, end = 2.dp, bottom = 2.dp)
                .height(117.dp)
                .fillMaxWidth()
                .background(color = Color(0xFF4D6180), shape = RoundedCornerShape(8.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(SubscribeHelper.getProductType(model.id), color = White50, fontSize = 14.sp, fontWeight = FontWeight.W400)
            Spacer(modifier = Modifier.weight(1f))
            Text("${model.currency}${model.price}${SubscribeHelper.getProductType2(model.id)}", color = White, fontSize = 16.sp, fontWeight = FontWeight.W500)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                // todo 价格计算
                text = when(model.id) {
                    SubHelper.getMonthPlanId()  -> "2.33/week"
                    SubHelper.getYearPlanId() -> "0.48/week"
                    else -> ""
                } ,
                color = White30, fontSize = 12.sp, fontWeight = FontWeight.W400
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}