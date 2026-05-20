package com.findhiddencamera.spycameralocator.ui.setting.page

import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.pay.utils.SubHelper
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.dialog.rememberLoadingDialog
import com.findhiddencamera.spycameralocator.utils.LaunchUtils
import com.findhiddencamera.spycameralocator.utils.SubscribeHelper
import com.findhiddencamera.spycameralocator.utils.ToastType
import com.findhiddencamera.spycameralocator.utils.findBaseActivityVBind
import com.findhiddencamera.spycameralocator.utils.showToast
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SettingPage() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dialog = rememberLoadingDialog()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(R.mipmap.img_settings_bg), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth())
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Box(modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 9.dp)) {
                Image(painter = painterResource(R.drawable.svg_back), contentDescription = null, modifier = Modifier.align(Alignment.CenterStart).clickable{
                    context.findBaseActivityVBind()?.finish()
                })
                Text("Settings", color = Color(0xFF152946), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
            }

            Spacer(modifier = Modifier.height(28.dp))
            Box(modifier = Modifier.fillMaxWidth().height(32.dp).background(color = Color(0x0844546B))) {
                Text("Suscription", color = Color(0xFF1D2833), fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterStart).padding(start = 20.dp))
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    scope.launch {
                        dialog.value = true
                        // 恢复订阅时主动刷新Google Play订单，确保本地订阅状态同步到最新。
                        val isSubscribed = withContext(Dispatchers.Default) {
                            SubscribeHelper.refreshSubscribeStateSuspend()
                        }
                        dialog.value = false
                        if (isSubscribed) {
                            "Welcome back, dear VIP".showToast(context, ToastType.SUCCESS)
                        } else {
                            "No valid subscriptions found.".showToast(context, ToastType.HINT)
                        }
                    }
                }
                .padding(start = 20.dp, top = 25.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(R.drawable.svg_restore), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Restore", color = Color(0xFF1D2833), fontSize = 14.sp)
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    LaunchUtils.launchSubscriptionManage(context, SubHelper.getProductId())
                }
                .padding(start = 20.dp, top = 25.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(R.drawable.svg_manage), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Manage", color = Color(0xFF1D2833), fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(35.dp))
            Box(modifier = Modifier.fillMaxWidth().height(32.dp).background(color = Color(0x0844546B))) {
                Text("About", color = Color(0xFF1D2833), fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterStart).padding(start = 20.dp))
            }

            Row(modifier = Modifier.padding(start = 20.dp, top = 25.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(R.drawable.svg_term_of_use), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Term of Use", color = Color(0xFF1D2833), fontSize = 14.sp)
            }
            Row(modifier = Modifier.padding(start = 20.dp, top = 25.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(R.drawable.svg_privacy_policy), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Privacy Policy", color = Color(0xFF1D2833), fontSize = 14.sp)
            }
            Row(modifier = Modifier.fillMaxWidth().clickable {
                /**
                 * TODO Google Play 内评（In-App Review）有严格限制，满足下面任意一条，就绝对不会显示：
                 ** 1.调试 / 测试包（debug 包、非 Google Play 安装的包、本地直接运行的 APK、模拟器）
                 ** 2.配额用光了（Google 限制：每个 app 每用户 最多弹 3 次 / 年，弹过一次后，冷却几小时 / 几天才会再弹）
                 ** 3.用户已经评价过（一旦你点过【提交】，永远不会再弹给你这个账号）
                 ** 4.用户已经评价过该 app 的其他版本（一旦你评价过某个版本，那么这个版本下的所有用户都不再会弹）
                 ** 5.设备没有安装最新版 Google Play 服务
                 ** 6.应用不是从 Google Play 安装的
                 */
                context.findBaseActivityVBind()?.let { activity ->
                    val manager = ReviewManagerFactory.create(context)
                    val request = manager.requestReviewFlow()
                    request.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val reviewInfo = task.result
                            // 必须用 Activity 才能弹！
                            manager.launchReviewFlow(activity, reviewInfo).addOnCompleteListener { result ->
                                // 用户已经看到评分弹窗，无论他们是否实际评分
                                // 用户在这里记录日志或执行其他操作
                                Log.d("ethan", "结果${result.result}")
                            }
                        } else {
                            // 弹不出来，这里会打印原因
                            val errorCode = (task.exception as? ReviewException)?.errorCode
                            Log.e("ethan", "评分弹窗失败 errorCode: $errorCode")
                        }
                    }
                }
            }.padding(start = 20.dp, top = 25.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(R.drawable.svg_rate_us), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Rate us", color = Color(0xFF1D2833), fontSize = 14.sp)
            }
        }
    }
}
