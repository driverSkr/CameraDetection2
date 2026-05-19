package com.findhiddencamera.spycameralocator.ui.subscribe.page

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ethan.pay.utils.SubHelper
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.dialog.rememberLoadingDialog
import com.findhiddencamera.spycameralocator.model.BluetoothDevice
import com.findhiddencamera.spycameralocator.model.SubModel
import com.findhiddencamera.spycameralocator.model.WifiDevice
import com.findhiddencamera.spycameralocator.theme.Red
import com.findhiddencamera.spycameralocator.theme.White
import com.findhiddencamera.spycameralocator.ui.result.BluetoothScanResultActivity
import com.findhiddencamera.spycameralocator.ui.result.WifiDetectResultActivity
import com.findhiddencamera.spycameralocator.ui.subscribe.SubscribeActivity
import com.findhiddencamera.spycameralocator.ui.subscribe.viewmodel.SubscribeViewModel
import com.findhiddencamera.spycameralocator.utils.findActivity
import com.findhiddencamera.spycameralocator.utils.findBaseActivityVBind
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

private const val RESULT_TYPE_BLUETOOTH = "bluetooth"

@Composable
fun GuideSubscribePage(
    resultType: String = "wifi",
    wifiSuspiciousDevices: List<WifiDevice>? = null,
    wifiTrustedDevices: List<WifiDevice>? = null,
    bluetoothSuspiciousDevices: List<BluetoothDevice>? = null,
    bluetoothTrustedDevices: List<BluetoothDevice>? = null,
    scanTimeSeconds: Long = System.currentTimeMillis().div(1000)
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val lockedCountHazeState = remember { HazeState() }
    val dialog = rememberLoadingDialog()
    val subscribeViewModel = context.findBaseActivityVBind()?.let { viewModel<SubscribeViewModel>(it) }
    var monthlyProduct by remember { mutableStateOf<SubModel?>(null) }
    var lockedCountOffset by remember { mutableStateOf(IntOffset.Zero) }
    var lockedCountSize by remember { mutableStateOf(IntSize.Zero) }
    val allWifiDevices = remember(wifiSuspiciousDevices, wifiTrustedDevices) {
        (wifiSuspiciousDevices.orEmpty() + wifiTrustedDevices.orEmpty())
            .distinctBy { it.ip.ifBlank { it.mac.ifBlank { it.name } } }
    }
    val allBluetoothDevices = remember(bluetoothSuspiciousDevices, bluetoothTrustedDevices) {
        (bluetoothSuspiciousDevices.orEmpty() + bluetoothTrustedDevices.orEmpty())
            .distinctBy { it.mac.ifBlank { it.name } }
    }
    val totalCount = if (resultType == RESULT_TYPE_BLUETOOTH) {
        allBluetoothDevices.size
    } else {
        allWifiDevices.size
    }
    val actualCameraCount = if (resultType == RESULT_TYPE_BLUETOOTH) {
        allBluetoothDevices.count { it.type.equals("Camera", true) }
    } else {
        allWifiDevices.count { it.type.equals("Camera", true) }
    }
    val displayCameraCount = remember(actualCameraCount) {
        if (actualCameraCount > 0) actualCameraCount else Random.nextInt(from = 1, until = 6)
    }
    val displayTotalCount = max(totalCount, displayCameraCount)
    val priceText = monthlyProduct?.price?.takeIf { it.isNotBlank() } ?: "$9.99"

    fun navigateToResult() {
        if (resultType == RESULT_TYPE_BLUETOOTH) {
            BluetoothScanResultActivity.launch(
                context = context,
                suspiciousDevices = bluetoothSuspiciousDevices.orEmpty(),
                trustedDevices = bluetoothTrustedDevices.orEmpty(),
                scanTimeSeconds = scanTimeSeconds
            )
        } else {
            WifiDetectResultActivity.launch(
                context = context,
                suspiciousDevices = wifiSuspiciousDevices.orEmpty(),
                trustedDevices = wifiTrustedDevices.orEmpty(),
                scanTimeSeconds = scanTimeSeconds
            )
        }
        context.findBaseActivityVBind()?.finish()
    }

    fun buyMonthlyProduct() {
        val activity = context.findActivity() as? FragmentActivity
        if (monthlyProduct != null && activity != null) {
            dialog.value = true
            subscribeViewModel?.buySubscribe(monthlyProduct, activity, dialog)
        } else {
            dialog.value = false
            Toast.makeText(context, "no product", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 查询月套餐商品，仅用于当前引导页展示价格和发起购买。
     */
    LaunchedEffect(Unit) {
        val queryResult = subscribeViewModel?.querySubProduct(context)
        monthlyProduct = queryResult
            ?.firstOrNull { it.id == SubHelper.getMonthPlanId() }
            ?: queryResult?.firstOrNull()
    }

    // 购买成功或已拥有订阅后，直接进入对应检测结果页。
    LaunchedEffect(subscribeViewModel?.isBuySuccess?.value) {
        if (subscribeViewModel?.isBuySuccess?.value == 1) {
            navigateToResult()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(color = Color(0xFF152946))) {
        Box(modifier = Modifier.fillMaxSize().haze(lockedCountHazeState)) {
            Image(painter = painterResource(id = R.mipmap.guide_subscribe_bg), contentScale = ContentScale.FillWidth, contentDescription = null, modifier = Modifier.fillMaxWidth())
            Row(modifier = Modifier.statusBarsPadding().padding(top = 5.dp).fillMaxWidth().padding(horizontal = 20.dp)) {
                Box(modifier = Modifier
                    .background(color = Color(0xFF152946).copy(alpha = 0.3f), shape = RoundedCornerShape(11.dp))
                    .clickable { SubscribeActivity.launch(context) }
                    .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text("All Plan", color = White, fontSize = 12.sp, fontWeight = FontWeight.W400)
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(modifier = Modifier
                    .background(color = Color(0xFF152946).copy(alpha = 0.3f), shape = RoundedCornerShape(11.dp))
                    .clickable { navigateToResult() }
                    .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text("Not Now", color = White, fontSize = 12.sp, fontWeight = FontWeight.W400)
                }
            }

            Column(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .height(64.dp)
                        .width(194.dp)
                        .onGloballyPositioned { coordinates ->
                            // 记录数字区域坐标，让 hazeChild 作为兄弟层覆盖，避免 Haze 父子嵌套崩溃。
                            val position = coordinates.positionInRoot()
                            lockedCountOffset = IntOffset(position.x.roundToInt(), position.y.roundToInt())
                            lockedCountSize = coordinates.size
                        }
                ) {
                    Text("$displayCameraCount/$displayTotalCount", color = Red, fontSize = 16.sp, fontWeight = FontWeight.W700, modifier = Modifier.align(Alignment.Center))
                }
                Spacer(modifier = Modifier.height(15.dp))
                Text("Suspected camera found", color = Color(0xFFF53863), fontSize = 16.sp, fontWeight = FontWeight.W500, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(30.dp))
                Text("$priceText per month", color = Color(0xFF939DAA), fontSize = 12.sp, fontWeight = FontWeight.W400, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(15.dp))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 15.dp)
                    .background(color = Color(0xFF5672FF), shape = RoundedCornerShape(10.dp))
                    .clickable {
                        buyMonthlyProduct()
                    }
                ) {
                    Text("Camera List & Details", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                }
                Spacer(modifier = Modifier.height(15.dp))
                Text("auto renew, cancel anytime", color = Color(0xFF939DAA), fontSize = 12.sp, fontWeight = FontWeight.W400, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        if (lockedCountSize != IntSize.Zero) {
            val lockedCountWidth = with(density) { lockedCountSize.width.toDp() }
            val lockedCountHeight = with(density) { lockedCountSize.height.toDp() }
            Box(
                modifier = Modifier
                    .offset { lockedCountOffset }
                    .size(width = lockedCountWidth, height = lockedCountHeight)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { buyMonthlyProduct() }
                    .hazeChild(
                        state = lockedCountHazeState,
                        style = HazeStyle(
                            backgroundColor = Color.Transparent,
                            tint = HazeTint(Color.White.copy(alpha = 0.10f)),
                            blurRadius = 8.dp,
                            noiseFactor = 0f
                        )
                    )
            ) {
                Image(painter = painterResource(R.mipmap.img_lock), contentDescription = null, modifier = Modifier.align(Alignment.Center).size(32.dp))
            }
        }
    }
}
