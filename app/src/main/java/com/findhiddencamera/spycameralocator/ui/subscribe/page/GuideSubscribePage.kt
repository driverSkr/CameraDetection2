package com.findhiddencamera.spycameralocator.ui.subscribe.page

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.theme.Red
import com.findhiddencamera.spycameralocator.theme.White
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild

@Composable
fun GuideSubscribePage() {
    val lockedCountHazeState = remember { HazeState() }

    Box(modifier = Modifier.fillMaxSize().background(color = Color(0xFF152946))) {
        Box(modifier = Modifier.fillMaxSize().haze(lockedCountHazeState)) {
            Image(painter = painterResource(id = R.mipmap.guide_subscribe_bg), contentScale = ContentScale.FillWidth, contentDescription = null, modifier = Modifier.fillMaxWidth())
            Row(modifier = Modifier.statusBarsPadding().padding(top = 5.dp).fillMaxWidth().padding(horizontal = 20.dp)) {
                Box(modifier = Modifier.background(color = Color(0xFF152946).copy(alpha = 0.3f), shape = RoundedCornerShape(11.dp)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                    Text("All Plan", color = White, fontSize = 12.sp, fontWeight = FontWeight.W400)
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(modifier = Modifier.background(color = Color(0xFF152946).copy(alpha = 0.3f), shape = RoundedCornerShape(11.dp)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                    Text("Not Now", color = White, fontSize = 12.sp, fontWeight = FontWeight.W400)
                }
            }

            Column(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.height(64.dp).width(194.dp)) {
                    Text("4/32", color = Red, fontSize = 16.sp, fontWeight = FontWeight.W700, modifier = Modifier.align(Alignment.Center))
                }
                Spacer(modifier = Modifier.height(15.dp))
                Text("Suspected camera found", color = Color(0xFFF53863), fontSize = 16.sp, fontWeight = FontWeight.W500, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(30.dp))
                Text("9.99 per month", color = Color(0xFF939DAA), fontSize = 12.sp, fontWeight = FontWeight.W400, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(15.dp))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 15.dp)
                    .background(color = Color(0xFF5672FF), shape = RoundedCornerShape(10.dp))
                    .clickable {

                    }
                ) {
                    Text("Camera List & Details", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                }
                Spacer(modifier = Modifier.height(15.dp))
                Text("auto renew, cancel anytime", color = Color(0xFF939DAA), fontSize = 12.sp, fontWeight = FontWeight.W400, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        Column(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.height(64.dp).width(194.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                        .hazeChild(
                            state = lockedCountHazeState,
                            style = HazeStyle(
                                backgroundColor = Color.Transparent,
                                tint = HazeTint(Color.White.copy(alpha = 0.10f)),
                                blurRadius = 18.dp,
                                noiseFactor = 0f
                            )
                        )
                )
                Image(painter = painterResource(R.mipmap.img_lock), contentDescription = null, modifier = Modifier.align(Alignment.Center).size(32.dp))
            }
            Spacer(modifier = Modifier.height(15.dp))
            Text("Suspected camera found", color = Color.Transparent, fontSize = 16.sp, fontWeight = FontWeight.W500, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(30.dp))
            Text("9.99 per month", color = Color.Transparent, fontSize = 12.sp, fontWeight = FontWeight.W400, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(15.dp))
            Box(modifier = Modifier.fillMaxWidth().height(60.dp).padding(horizontal = 15.dp))
            Spacer(modifier = Modifier.height(15.dp))
            Text("auto renew, cancel anytime", color = Color.Transparent, fontSize = 12.sp, fontWeight = FontWeight.W400, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
