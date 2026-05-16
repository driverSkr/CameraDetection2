package com.findhiddencamera.spycameralocator.ui.camera.page

import android.Manifest
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.theme.Black
import com.findhiddencamera.spycameralocator.theme.White50
import com.findhiddencamera.spycameralocator.ui.camera.view.CameraPreview
import com.findhiddencamera.spycameralocator.ui.subscribe.SubscribeActivity
import com.findhiddencamera.spycameralocator.utils.SubscribeHelper
import com.findhiddencamera.spycameralocator.utils.findBaseActivityVBind
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun CameraScannerPage(showSubscribeGuide: Boolean = false) {
    val context = LocalContext.current
    val list = listOf(
        Triple(R.drawable.svg_red_selected, R.drawable.svg_red_not_selected, Color(0xFFFF0005)),
        Triple(R.drawable.svg_green_selected, R.drawable.svg_green_not_selected, Color(0xFF00EB5E)),
        Triple(R.drawable.svg_blue_selected, R.drawable.svg_blue_not_selected, Color(0xFF00ACFF)),
        Triple(R.drawable.svg_black_50_selected, R.drawable.svg_black_50_not_selected, Color(0x80000000)),
        Triple(R.drawable.svg_white_selected, R.drawable.svg_white_not_selected, Color(0xFFFFFFFF)),
    )
    var currentFilterColorIndex by remember { mutableStateOf(0) }
    var isDangerGuideState by remember { mutableStateOf(false) }
    var dangerDots by remember { mutableStateOf<List<Pair<Float, Float>>>(emptyList()) }
    val cameraPermissionGranted = remember {
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    LaunchedEffect(showSubscribeGuide) {
        if (!showSubscribeGuide || SubscribeHelper.isSubscribed) {
            return@LaunchedEffect
        }

        delay(2_000)
        dangerDots = List(Random.nextInt(from = 1, until = 4)) {
            (0.12f + Random.nextFloat() * 0.76f) to (0.12f + Random.nextFloat() * 0.66f)
        }
        isDangerGuideState = true
        delay(500)
        SubscribeActivity.launch(context)
        context.findBaseActivityVBind()?.finish()
    }

    Column(modifier = Modifier.fillMaxSize().background(color = Black).navigationBarsPadding()) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            if (cameraPermissionGranted) {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    lensFacing = CameraSelector.LENS_FACING_BACK
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(list[currentFilterColorIndex].third.copy(alpha = 0.3f))
            )

            if (isDangerGuideState) {
                InfraredDangerGuideDots(dots = dangerDots)
            }

            Image(
                painter = painterResource(R.drawable.svg_back),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 50.dp, start = 15.dp)
                    .clickable {
                        context.findBaseActivityVBind()?.finish()
                    }
            )

        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFF152946))
                .padding(top = 20.dp, bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.padding(bottom = 20.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                list.forEachIndexed { index, color ->
                    Image(
                        painter = painterResource(if (currentFilterColorIndex == index) color.first else color.second),
                        contentDescription = null,
                        modifier = Modifier.clickable { currentFilterColorIndex = index }
                    )
                }
            }
            Text(
                "Check for flickering or heat sources by aiming at sockets, lamps, TVs, etc.",
                color = White50,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun InfraredDangerGuideDots(dots: List<Pair<Float, Float>>) {
    val infiniteTransition = rememberInfiniteTransition(label = "infraredDangerDots")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 260),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )
    val dotScale by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotScale"
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        dots.forEachIndexed { index, dot ->
            Box(
                modifier = Modifier
                    .offset(
                        x = maxWidth * dot.first,
                        y = maxHeight * dot.second
                    )
                    .size((18 + index * 3).dp * dotScale)
                    .background(
                        color = Color(0xFFFF0005).copy(alpha = dotAlpha),
                        shape = CircleShape
                    )
            )
        }
    }
}
