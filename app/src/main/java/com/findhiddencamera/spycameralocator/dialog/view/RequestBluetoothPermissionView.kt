package com.findhiddencamera.spycameralocator.dialog.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.base.dialog.BaseDialog
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.theme.White

@Composable
fun RequestBluetoothPermissionView(dialog: BaseDialog) {
    val iconSize = 66.dp
    val iconOverlap = iconSize / 2

    Box(
        modifier = Modifier
            .width(330.dp)
            .padding(top = iconOverlap)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = White, shape = RoundedCornerShape(10.dp))
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(iconOverlap)) {
                Image(
                    painter = painterResource(R.drawable.svg_close_50),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 10.dp)
                )
            }
            Text("Hiidden Camera Detect Master wants to enable Bluetooth.", color = Color(0xFF152946), fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 42.dp))
            Spacer(modifier = Modifier.height(30.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                Box(modifier = Modifier.clickable{ dialog.dismiss() }.weight(1f).height(46.dp).border(width = 1.dp, color = Color(0xFF152946), shape = RoundedCornerShape(10.dp))) {
                    Text("Not allow", color = Color(0xFF152946), fontSize = 16.sp, modifier = Modifier.align(Alignment.Center))
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(modifier = Modifier.clickable{ dialog.dismiss() }.weight(1f).height(46.dp).background(color = Color(0xFF152946), shape = RoundedCornerShape(10.dp))) {
                    Text("Allow", color = White, fontSize = 16.sp, modifier = Modifier.align(Alignment.Center))
                }
            }
            Spacer(modifier = Modifier.height(25.dp))
        }

        Image(
            painter = painterResource(R.mipmap.img_blutooth_big), // todo wifi图标有问题，应该给一个外层有白色圆环的图片
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
                .align(Alignment.TopCenter)
                .offset(y = (-iconOverlap))
        )
    }
}
