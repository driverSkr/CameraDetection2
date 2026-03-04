package com.ethan.cameradetection2.ui.setting.page

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
import com.ethan.cameradetection2.utils.findBaseActivityVBind

@Composable
fun Setting2Page() {
    val context = LocalContext.current

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
            Row(modifier = Modifier.padding(start = 20.dp, top = 25.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(R.drawable.svg_restore), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Restore", color = Color(0xFF1D2833), fontSize = 14.sp)
            }
            Row(modifier = Modifier.padding(start = 20.dp, top = 25.dp), verticalAlignment = Alignment.CenterVertically) {
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
            Row(modifier = Modifier.padding(start = 20.dp, top = 25.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(R.drawable.svg_rate_us), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Rate us", color = Color(0xFF1D2833), fontSize = 14.sp)
            }
        }
    }
}