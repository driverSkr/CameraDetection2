package com.ethan.cameradetection2.ui.camera.page

import android.Manifest
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.PermissionUtils
import com.ethan.cameradetection2.R
import com.ethan.cameradetection2.theme.Black
import com.ethan.cameradetection2.theme.Transparent
import com.ethan.cameradetection2.theme.White
import com.ethan.cameradetection2.theme.White50
import com.ethan.cameradetection2.ui.camera.view.CameraPreview
import com.ethan.cameradetection2.utils.findBaseActivityVBind

@Composable
fun CameraScannerPage() {
    val context = LocalContext.current
    val list = listOf(
        Triple(R.drawable.svg_red_selected, R.drawable.svg_red_not_selected, Color(0xFFFF0005)),
        Triple(R.drawable.svg_green_selected, R.drawable.svg_green_not_selected, Color(0xFF00EB5E)),
        Triple(R.drawable.svg_blue_selected, R.drawable.svg_blue_not_selected, Color(0xFF00ACFF)),
        Triple(R.drawable.svg_black_50_selected, R.drawable.svg_black_50_not_selected, Color(0x80000000)),
        Triple(R.drawable.svg_white_selected, R.drawable.svg_white_not_selected, Color(0xFFFFFFFF)),
    )
    var currentFilterColorIndex by remember { mutableStateOf(0) }
    var cameraPermissionGranted by remember { mutableStateOf(false) }

    // 请求摄像头权限
    LaunchedEffect(Unit) {
        PermissionUtils.permission(Manifest.permission.CAMERA)
            .callback { isAllGranted, granted, deniedForever, denied ->
                if (!isAllGranted) {
                    Toast.makeText(
                        context,
                        "The camera function cannot be used because the camera permission is not obtained.",
                        Toast.LENGTH_SHORT
                    ).show()
                    context.findBaseActivityVBind()?.finish()
                } else {
                    cameraPermissionGranted = true
                }
            }
            .request()
    }

    Column(modifier = Modifier.fillMaxSize().background(color = Black).navigationBarsPadding()) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            if (cameraPermissionGranted) {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    lensFacing = CameraSelector.LENS_FACING_BACK
                )
            }

            // 滤镜层
            Box(modifier = Modifier
                .fillMaxSize()
                .background(list[currentFilterColorIndex].third.copy(alpha = 0.3f))
            )

            Image(painter = painterResource(R.drawable.svg_back), contentDescription = null, modifier = Modifier.align(Alignment.TopStart).padding(top = 50.dp, start = 15.dp).clickable{
                context.findBaseActivityVBind()?.finish()
            })

        }
        Column(modifier = Modifier.fillMaxWidth().background(color = Color(0xFF152946)).padding(top = 20.dp, bottom = 30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.padding(bottom = 20.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                list.forEachIndexed { index, color ->
                    Image(painter = painterResource(if (currentFilterColorIndex == index) color.first else color.second), contentDescription = null, modifier = Modifier.clickable{ currentFilterColorIndex = index })
                }
            }
            Text("Check for flickering or heat sources by aiming at sockets, lamps, TVs, etc.", color = White50, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp))
        }
    }
}