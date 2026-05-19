package com.findhiddencamera.spycameralocator.ui.subscribe.page

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.dialog.rememberLoadingDialog
import com.findhiddencamera.spycameralocator.model.SubModel
import com.findhiddencamera.spycameralocator.theme.Transparent
import com.findhiddencamera.spycameralocator.theme.White
import com.findhiddencamera.spycameralocator.theme.White10
import com.findhiddencamera.spycameralocator.theme.White50
import com.findhiddencamera.spycameralocator.ui.subscribe.view.SubProductView
import com.findhiddencamera.spycameralocator.ui.subscribe.viewmodel.SubscribeViewModel
import com.findhiddencamera.spycameralocator.utils.DataHelper
import com.findhiddencamera.spycameralocator.utils.SubscribeHelper
import com.findhiddencamera.spycameralocator.utils.ToastType
import com.findhiddencamera.spycameralocator.utils.findActivity
import com.findhiddencamera.spycameralocator.utils.findBaseActivityVBind
import com.findhiddencamera.spycameralocator.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 开屏订阅页
 */
@Composable
fun SplashScreenSubscribePage(
    onClose: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dialog = rememberLoadingDialog()
    var isLoading by remember { mutableStateOf(true) }
    var subModelList by remember { mutableStateOf<MutableList<SubModel>?>(null) }
    var selectedSubProduct by remember { mutableStateOf<SubModel?>(null) }
    val subscribeViewModel = context.findBaseActivityVBind()?.let { viewModel<SubscribeViewModel>(it) }
    val showBundleType = remember {
        val showCount = DataHelper.getDailyShowCount(context, "splash_screen_subscribe")
        // 每日6次按 0、1、2、0、1、2 轮换展示三种销售模式
        ((showCount - 1).coerceAtLeast(0)) % 3
    }

    /**
     * 查询订阅商品
     */
    LaunchedEffect(showBundleType) {
        isLoading = true
        val queryResult = subscribeViewModel?.querySplashScreenSubProduct(context, showBundleType)
        if (queryResult != null) {
            subModelList = queryResult
            selectedSubProduct = queryResult.getOrNull(1) ?: queryResult.getOrNull(0)
        }
        isLoading = false
    }

    // 监听 订阅状态
    LaunchedEffect(subscribeViewModel?.isBuySuccess?.value) {
        if (subscribeViewModel?.isBuySuccess?.value == 1) {
            onClose()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(color = Color(0xFF152946)).navigationBarsPadding()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(painter = painterResource(R.mipmap.img_subscribe_bg), contentScale = ContentScale.FillWidth, modifier = Modifier.fillMaxWidth(), contentDescription = null)
            Box(modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(200.dp)
                .background(brush = Brush.verticalGradient(colorStops = arrayOf(0f to Transparent, 1f to Color(0xFF152946))))
            )
        }

        Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(top = 4.dp, start = 20.dp, end = 15.dp)) {
            Image(
                painter = painterResource(R.drawable.svg_icon_close_30),
                modifier = Modifier.clickable { onClose() },
                contentDescription = null
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier
                .clickable {
                    val activity = context.findActivity() as? FragmentActivity
                    scope.launch {
                        dialog.value = true
                        // Restore时主动刷新一次订阅状态，确保拿到最新购买结果
                        val isSubscribed = withContext(Dispatchers.Default) {
                            SubscribeHelper.refreshSubscribeStateSuspend()
                        }
                        dialog.value = false
                        if (isSubscribed) {
                            "Welcome back, dear VIP".showToast(context, ToastType.SUCCESS)
                            onClose()
                        } else {
                            "No valid subscriptions found.".showToast(context, ToastType.HINT)
                            if (selectedSubProduct != null && activity != null) {
                                // 未恢复到有效订阅时，继续发起当前选中套餐的订阅购买
                                dialog.value = true
                                subscribeViewModel?.buySubscribe(selectedSubProduct, activity, dialog)
                            } else {
                                Toast.makeText(context, "no product", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .background(color = Color(0xFF152946).copy(alpha = 0.3f), shape = RoundedCornerShape(11.dp))
                .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text("Restore", color = White, fontSize = 12.sp)
            }
        }

        Column(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = 15.dp)) {
            Text("Start to Find Cameras", color = White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(15.dp))
            Row(modifier = Modifier) {
                Image(painter = painterResource(R.drawable.svg_selected), modifier = Modifier.padding(end = 8.dp), contentDescription = null)
                Text("Detect WiFi & Bluetooth Camera", color = White50, fontSize = 18.sp, fontWeight = FontWeight.W500)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier) {
                Image(painter = painterResource(R.drawable.svg_selected), modifier = Modifier.padding(end = 8.dp), contentDescription = null)
                Text("Analyze Camera Details", color = White50, fontSize = 18.sp, fontWeight = FontWeight.W500)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier) {
                Image(painter = painterResource(R.drawable.svg_selected), modifier = Modifier.padding(end = 8.dp), contentDescription = null)
                Text("Find Area via Magnetic Field", color = White50, fontSize = 18.sp, fontWeight = FontWeight.W500)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier) {
                Image(painter = painterResource(R.drawable.svg_selected), modifier = Modifier.padding(end = 8.dp), contentDescription = null)
                Text("Find Flicker via Infrared", color = White50, fontSize = 18.sp, fontWeight = FontWeight.W500)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier) {
                Image(painter = painterResource(R.drawable.svg_selected), modifier = Modifier.padding(end = 8.dp), contentDescription = null)
                Text("Save Detection Records", color = White50, fontSize = 18.sp, fontWeight = FontWeight.W500)
            }

            Spacer(modifier = Modifier.height(30.dp))
            Box(modifier = Modifier.fillMaxWidth().height(156.dp)) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center).size(36.dp),
                            color = Color.White,
                            trackColor = White10,
                            strokeCap = StrokeCap.Round
                        )
                    }
                } else {
                    subModelList?.let { list ->
                        AnimatedContent(showBundleType) {
                            when (it) {
                                0 -> {
                                    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.Bottom) {
                                        list.forEach { model ->
                                            SubProductView(modifier = Modifier.weight(1f),  selectedSubProduct?.id == model.id, model) {
                                                selectedSubProduct = model
                                            }
                                        }
                                    }
                                }
                                1 -> {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Text("${list[0].price}${list[0].currency} per month", color = Color(0xFF939DAA), fontSize = 12.sp, fontWeight = FontWeight.W500, textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth())
                                    }
                                }
                                2 -> {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Text("${list[0].price}${list[0].currency} per year", color = Color(0xFF939DAA), fontSize = 12.sp, fontWeight = FontWeight.W500, textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth())
                                    }
                                }
                            }
                        }

                    } ?: EmptyView {
                        scope.launch(Dispatchers.Default) {
                            isLoading = true
                            val queryResult = subscribeViewModel?.querySplashScreenSubProduct(context, showBundleType)
                            if (queryResult != null) {
                                subModelList = queryResult
                                selectedSubProduct = queryResult.getOrNull(1) ?: queryResult.getOrNull(0)
                            }
                            isLoading = false
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(if (showBundleType == 0) 30.dp else 10.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(color = Color(0xFF5672FF), shape = RoundedCornerShape(10.dp))
                .clickable {
                    val activity = context.findActivity() as? FragmentActivity
                    if (selectedSubProduct != null && activity != null) {
                        dialog.value = true
                        subscribeViewModel?.buySubscribe(selectedSubProduct, activity, dialog)
                    } else {
                        dialog.value = false
                        Toast.makeText(context, "no product", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Continue", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
            }
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                "The subscription will automatically renew unless you cancel it in the store settings 24 hours before the end of the subscription period.",
                color = Color(0xFF939DAA),
                fontSize = 12.sp,
                lineHeight = 14.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W400,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
