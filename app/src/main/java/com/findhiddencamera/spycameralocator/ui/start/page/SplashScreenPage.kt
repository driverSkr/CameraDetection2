package com.findhiddencamera.spycameralocator.ui.start.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findhiddencamera.spycameralocator.theme.White
import com.findhiddencamera.spycameralocator.theme.White30
import com.findhiddencamera.spycameralocator.ui.home.HomeActivity
import com.findhiddencamera.spycameralocator.ui.start.PolicyActivity
import com.findhiddencamera.spycameralocator.utils.DataHelper
import com.findhiddencamera.spycameralocator.utils.findBaseActivityVBind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SplashScreenPage() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.Default) {
            delay(1000)
            withContext(Dispatchers.Main) {
                if (DataHelper.isFirst(context, "open_app")) {
                    PolicyActivity.launch(context)
                } else {
                    HomeActivity.launch(context)
                }
                context.findBaseActivityVBind()?.finish()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(color = Color(0xFF8095FF))) {
        Box(modifier = Modifier
            .align(Alignment.Center)
            .offset(y = (-100).dp)
            .size(100.dp)
            .background(color = Color(0xFFD9D9D9).copy(alpha = 0.21f), shape = RoundedCornerShape(20.dp))
        )

        Column(modifier = Modifier.align(Alignment.BottomCenter).offset(y = (-60).dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Spy Camera Locator", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(22.dp))
            CircularProgressIndicator(color = White, strokeWidth = 1.5.dp, trackColor = White30, strokeCap = StrokeCap.Round, modifier = Modifier.size(20.dp))
        }
    }
}