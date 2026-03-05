package com.ethan.cameradetection2.ui.history.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.ethan.cameradetection2.ui.history.view.HistoryRecordItemView
import com.ethan.cameradetection2.utils.findBaseActivityVBind

@Composable
fun HistoryRecordPage() {
    val context = LocalContext.current
    val list = listOf(
        Triple("2025-10-01 12:12:12", "3 Cameras", 1),
        Triple("2025-10-01 12:12:12", "99 Suspected Devices", 2),
        Triple("2025-10-01 12:12:12", "3 Cameras", 1),
        Triple("2025-10-01 12:12:12", "99 Suspected Devices", 2),
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(R.mipmap.img_history_record_bg), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth())
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Box(modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 9.dp)) {
                Image(painter = painterResource(R.drawable.svg_back), contentDescription = null, modifier = Modifier.align(Alignment.CenterStart).clickable{
                    context.findBaseActivityVBind()?.finish()
                })
                Text("Record", color = Color(0xFF152946), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
            }

            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("99", color = Color(0xFF5874FF), fontSize = 50.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Cumulative Detections", color = Color(0xFF152946), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 15.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(list.size) {
                    HistoryRecordItemView(list[it])
                }
            }
        }
    }
}