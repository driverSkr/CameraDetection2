package com.ethan.cameradetection2.ui.start.page

import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.ethan.cameradetection2.R
import com.ethan.cameradetection2.theme.White
import com.ethan.cameradetection2.theme.White50
import com.ethan.cameradetection2.ui.home.HomeActivity
import com.ethan.cameradetection2.utils.LaunchUtils
import com.ethan.cameradetection2.utils.TextUtils
import com.ethan.cameradetection2.utils.findBaseActivityVBind

@Composable
fun PolicyPage() {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(color = Color(0xFF152946))) {
        Image(painter = painterResource(R.mipmap.img_policy_bg), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth())

        Column(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Spy Camera Locator", color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(15.dp))
            Text("Scan and locate all suspected hidden cameras, protect your privacy.", color = White50, fontSize = 14.sp, fontWeight = FontWeight.W600, lineHeight = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 38.dp))
            Spacer(modifier = Modifier.height(50.dp))
            Box(modifier = Modifier.clickable{
                HomeActivity.launch(context)
                context.findBaseActivityVBind()?.finish()
            }.padding(horizontal = 15.dp).fillMaxWidth().height(60.dp).background(color = Color(0xFF5672FF), shape = RoundedCornerShape(10.dp))) {
                Text("Accept & Continue", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
            }
            Spacer(modifier = Modifier.height(15.dp))
            AndroidView(
                factory = { context ->
                    val inflater = LayoutInflater.from(context)
                    val view = inflater.inflate(R.layout.layout_text_view, null, false)
                    val content = "By clicking Continue, you have read and agree to our Privacy Policy and Terms of Use"
                    val span1 = "Privacy Policy"
                    val span2 = "Terms of Use"
                    val click1 = object : ClickableSpan() {
                        override fun onClick(p0: View) {
                            // todo 需要配置外链
                            LaunchUtils.launchWeb(context, "", span1)
                        }
                    }
                    val click2 = object : ClickableSpan() {
                        override fun onClick(p0: View) {
                            LaunchUtils.launchWeb(context, "", span2)
                        }
                    }
                    val spans = arrayOf(
                        TextUtils.Span(span1, R.color.white, click1),
                        TextUtils.Span(span2, R.color.white, click2)
                    )
                    TextUtils.setSpanText(context, content, spans, view.findViewById(R.id.text))
                    view
                },
                modifier = Modifier.padding(horizontal = 23.dp).fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}