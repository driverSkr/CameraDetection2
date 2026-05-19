package com.findhiddencamera.spycameralocator.dialog.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.base.dialog.BaseDialog
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.theme.White

@Composable
fun GuideCheckView(
    dialog: BaseDialog,
    content: String = "Hide & Spy Cameras stream your video to nearby devices via Bluetooth, so be sure to check as soon as possible.",
    imageRes: Int = R.mipmap.img_bluetooth_radar_plate,
    onStart: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    Box(modifier = Modifier.width(330.dp).background(color = White, shape = RoundedCornerShape(10.dp))) {
        Image(
            painter = painterResource(R.drawable.svg_close_50),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 10.dp, end = 10.dp)
                .clickable {
                    onCancel.invoke()
                    dialog.dismiss()
                }
        )
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(20.dp))
            Image(painter = painterResource(imageRes), contentScale = ContentScale.Crop, contentDescription = null, modifier = Modifier.size(120.dp))
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = content,
                color = Color(0xFF152946),
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
            Spacer(modifier = Modifier.height(30.dp))
            Row(modifier = Modifier.fillMaxWidth().height(46.dp)) {
                Box(modifier = Modifier.clickable{
                    onCancel.invoke()
                    dialog.dismiss()
                }.fillMaxHeight().weight(1f).border(width = 1.dp, color = Color(0xFF152946), shape = RoundedCornerShape(10.dp))) {
                    Text("Not Now", color = Color(0xFF152946), fontSize = 16.sp, fontWeight = FontWeight.W600, modifier = Modifier.align(Alignment.Center))
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(modifier = Modifier.clickable{
                    dialog.dismiss()
                    onStart.invoke()
                }.fillMaxHeight().weight(1f).background(color = Color(0xFF152946), shape = RoundedCornerShape(10.dp))) {
                    Text("Start", color = White, fontSize = 16.sp, fontWeight = FontWeight.W600, modifier = Modifier.align(Alignment.Center))
                }
            }
            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}
