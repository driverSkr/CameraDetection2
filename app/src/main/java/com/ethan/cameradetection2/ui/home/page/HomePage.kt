package com.ethan.cameradetection2.ui.home.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.cameradetection2.R
import com.ethan.cameradetection2.theme.White
import com.ethan.cameradetection2.theme.White50

@Composable
fun HomePage() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(R.mipmap.img_home_bg), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth())
        Column(modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(start = 15.dp, end = 15.dp, top = 12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Spy Camera Locator", color = Color(0xFF152946), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Image(painter = painterResource(R.mipmap.img_pro), contentScale = ContentScale.Crop, contentDescription = null, modifier = Modifier.width(61.dp).height(26.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Image(painter = painterResource(R.drawable.svg_settings), contentDescription = null)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                Image(painter = painterResource(R.mipmap.img_home_func_bg), contentScale = ContentScale.FillWidth, contentDescription = null, modifier = Modifier.fillMaxWidth())
                Column(modifier = Modifier.padding(start = 15.dp, top = 15.dp, end = 120.dp)) {
                    Text("WiFi Camera", fontSize = 18.sp, color = White, fontWeight = FontWeight.Bold)
                    Text("These cameras upload the recorded video to the Internet via Wi-Fi.", fontSize = 12.sp, color = White50, lineHeight = 14.sp, modifier = Modifier.fillMaxWidth().padding(top = 5.dp))
                }
                Box(modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 15.dp, bottom = 15.dp)
                    .background(color = White, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text("Detect Now", color = Color(0xFFF53863), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(15.dp).wrapContentHeight().padding(15.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(painter = painterResource(R.mipmap.img_home_func_bg), contentScale = ContentScale.FillWidth, contentDescription = null, modifier = Modifier.fillMaxWidth())
                Column(modifier = Modifier.padding(start = 15.dp, top = 15.dp, end = 120.dp)) {
                    Text("Bluetooth Camera", fontSize = 18.sp, color = White, fontWeight = FontWeight.Bold)
                    Text("These cameras upload the recorded video to a nearby storage device via Bluetooth.", fontSize = 12.sp, color = White50, lineHeight = 14.sp, modifier = Modifier.fillMaxWidth().padding(top = 5.dp))
                }
                Box(modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 15.dp, bottom = 15.dp)
                    .background(color = White, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text("Detect Now", color = Color(0xFFF53863), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}