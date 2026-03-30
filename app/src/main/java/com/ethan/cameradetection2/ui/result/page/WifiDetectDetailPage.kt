package com.ethan.cameradetection2.ui.result.page

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.cameradetection2.R
import com.ethan.cameradetection2.model.WifiDevice
import com.ethan.cameradetection2.theme.White
import com.ethan.cameradetection2.ui.camera.CameraScannerActivity
import com.ethan.cameradetection2.ui.magnetic.MagneticFieldActivity
import com.ethan.cameradetection2.utils.findBaseActivityVBind

@Composable
fun WifiDetectDetailPage(device: WifiDevice?) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(color = White)) {
        Image(painter = painterResource(R.mipmap.img_history_record_bg), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth())
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 15.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 9.dp)) {
                Image(painter = painterResource(R.drawable.svg_back), contentDescription = null, modifier = Modifier.align(Alignment.CenterStart).clickable{
                    context.findBaseActivityVBind()?.finish()
                })
                Text("Details", color = Color(0xFF152946), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
            }

            Spacer(modifier = Modifier.height(32.dp))
            Box(modifier = Modifier.size(70.dp).background(color = Color(0xFF939DAA).copy(0.08f), shape = RoundedCornerShape(12.dp))) {
                Image(painter = painterResource(R.drawable.svg_camera), contentDescription = null, modifier = Modifier.align(Alignment.Center))
                Image(painter = painterResource(R.drawable.svg_red_light), contentDescription = null, modifier = Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (-8).dp))
            }
            Spacer(modifier = Modifier.height(15.dp))
            Text("Device Name", color = Color(0xFF152946), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(40.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardColors(contentColor = White, containerColor = White, disabledContainerColor = White, disabledContentColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = BorderStroke(
                    width = 1.dp,  // 边框宽度
                    color = Color(0xFF5874FF).copy(0.08f)  // 边框颜色
                )
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 21.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(R.drawable.svg_network_state), contentDescription = null)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Online now", color = Color(0xFF152946), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(if (device?.connected == true) "Yes" else "No", color = Color(0xFF939DAA), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(R.drawable.svg_question), contentDescription = null)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Type", color = Color(0xFF152946), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(device?.type ?: "Unknown", color = Color(0xFF939DAA), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(R.drawable.svg_position), contentDescription = null)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("IP", color = Color(0xFF152946), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(device?.ip ?: "Unknown", color = Color(0xFF939DAA), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Text("Suspected hidden camera. Locate it immediately.", color = Color(0xFF44546B), fontSize = 14.sp, fontWeight = FontWeight.W700, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(15.dp))
            Box(modifier = Modifier.clickable{ MagneticFieldActivity.launch(context) }.fillMaxWidth().height(114.dp).background(color = Color(0xFF8095FF), shape = RoundedCornerShape(10.dp))) {
                Column(modifier = Modifier.fillMaxSize().padding(15.dp)) {
                    Text("Find out the operating range of equipment by magnetic field signal", color = White, fontSize = 12.sp, fontWeight = FontWeight.W500, lineHeight = 12.sp, modifier = Modifier.fillMaxWidth().padding(end = 125.dp))
                    Spacer(modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.height(36.dp).background(color = White, shape = RoundedCornerShape(8.dp)).padding(horizontal = 14.dp)) {
                        Text("Detect Now", color = Color(0xFF5672FF), fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.clickable{ CameraScannerActivity.launch(context) }.fillMaxWidth().height(114.dp).background(color = Color(0xFF8095FF), shape = RoundedCornerShape(10.dp))) {
                Column(modifier = Modifier.fillMaxSize().padding(15.dp)) {
                    Text("Find the flickering pinhole camera through the infrared camera.", color = White, fontSize = 12.sp, fontWeight = FontWeight.W500, lineHeight = 12.sp, modifier = Modifier.fillMaxWidth().padding(end = 125.dp))
                    Spacer(modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.height(36.dp).background(color = White, shape = RoundedCornerShape(8.dp)).padding(horizontal = 14.dp)) {
                        Text("Detect Now", color = Color(0xFF5672FF), fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}