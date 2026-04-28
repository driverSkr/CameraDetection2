package com.findhiddencamera.spycameralocator.ui.subscribe.page

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.findhiddencamera.spycameralocator.utils.findActivity
import com.findhiddencamera.spycameralocator.utils.findBaseActivityVBind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SubscribePage() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dialog = rememberLoadingDialog()
    var isLoading by remember { mutableStateOf(true) }
    var subModelList by remember { mutableStateOf<MutableList<SubModel>?>(null) }
    var selectedSubProduct by remember { mutableStateOf<SubModel?>(null) }
    val subscribeViewModel = context.findBaseActivityVBind()?.let { viewModel<SubscribeViewModel>(it) }

    /**
     * 查询订阅商品
     */
    LaunchedEffect(Unit) {
        isLoading = true
        val queryResult = subscribeViewModel?.querySubProduct(context)
        if (queryResult != null) {
            subModelList = queryResult
            selectedSubProduct = queryResult.getOrNull(1)
        }
        isLoading = false
    }

    // 监听 订阅状态
    LaunchedEffect(subscribeViewModel?.isBuySuccess?.value) {
        if (subscribeViewModel?.isBuySuccess?.value == 1) {
            context.findBaseActivityVBind()?.finish()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(color = Color(0xFF152946)).navigationBarsPadding()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(painter = painterResource(R.mipmap.img_subscribe_bg), modifier = Modifier.fillMaxWidth(), contentDescription = null)
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
                modifier = Modifier.clickable{ context.findBaseActivityVBind()?.finish() },
                contentDescription = null
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier
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
                        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.Bottom) {
                            list.forEach { model ->
                                SubProductView(modifier = Modifier.weight(1f),  selectedSubProduct?.id == model.id, model) {
                                    selectedSubProduct = model
                                }
                            }
                        }
                    } ?: EmptyView {
                        scope.launch(Dispatchers.Default) {
                            isLoading = true
                            val queryResult = subscribeViewModel?.querySubProduct(context)
                            if (queryResult != null) {
                                subModelList = queryResult
                                selectedSubProduct = queryResult[0]
                            }
                            isLoading = false
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
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

@Composable
fun EmptyView(modifier: Modifier = Modifier, onRetryClick: () -> Unit) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("No product found", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.W400)
        Box(modifier = Modifier
            .padding(top = 20.dp)
            .border(width = 1.dp, color = Color.White, shape = RoundedCornerShape(44.dp))
            .padding(vertical = 10.dp, horizontal = 30.dp)
            .clickable { onRetryClick.invoke() }) {
            Text("Retry", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.W400)
        }
    }
}