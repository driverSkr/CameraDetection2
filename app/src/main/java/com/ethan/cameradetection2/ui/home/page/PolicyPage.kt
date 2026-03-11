package com.ethan.cameradetection2.ui.home.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ethan.cameradetection2.R
import com.ethan.cameradetection2.theme.Transparent

@Composable
fun PolicyPage() {
    Box(modifier = Modifier.fillMaxSize().background(color = Color(0xFF152946))) {
        Box(modifier = Modifier) {
            Image(painter = painterResource(R.mipmap.img_policy_bg), contentDescription = null, contentScale = ContentScale.FillWidth, modifier = Modifier.fillMaxWidth())
            Box(modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(333.dp)
                .background(brush = Brush.verticalGradient(colorStops = arrayOf(0f to Transparent, 1f to Color(0xFF152946))))
            )
        }
    }
}