package com.ethan.cameradetection2.ui.home.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.ethan.cameradetection2.R
import com.ethan.cameradetection2.dialog.DialogHelper
import com.ethan.cameradetection2.theme.White
import com.ethan.cameradetection2.theme.White50
import com.ethan.cameradetection2.ui.bluetooth.BluetoothCamerasActivity
import com.ethan.cameradetection2.ui.camera.CameraScannerActivity
import com.ethan.cameradetection2.ui.history.HistoryRecordActivity
import com.ethan.cameradetection2.ui.magnetic.MagneticFieldActivity
import com.ethan.cameradetection2.ui.setting.Setting2Activity
import com.ethan.cameradetection2.ui.subscribe.SubscribeActivity
import com.ethan.cameradetection2.ui.wifi.WiFiCamerasActivity

@Composable
fun HomePage() {
    val context = LocalContext.current

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
                Image(painter = painterResource(R.mipmap.img_pro), contentScale = ContentScale.Crop, contentDescription = null, modifier = Modifier.width(61.dp).height(26.dp).clickable{
                    SubscribeActivity.launch(context)
                })
                Spacer(modifier = Modifier.width(12.dp))
                Image(painter = painterResource(R.drawable.svg_settings), contentDescription = null, modifier = Modifier.clickable {
                    Setting2Activity.launch(context)
                })
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 15.dp,
                contentPadding = PaddingValues(bottom = 15.dp)
            ) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(modifier = Modifier.fillMaxWidth().clickable{ WiFiCamerasActivity.launch(context) }) {
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
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(modifier = Modifier.fillMaxWidth().clickable{ BluetoothCamerasActivity.launch(context) }) {
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

                item {
                    Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f).background(color = White, shape = RoundedCornerShape(10.dp)).clickable{
                        MagneticFieldActivity.launch(context)
                    }) {
                        Column(modifier = Modifier.padding(top = 20.dp, start = 15.dp)) {
                            Text("Magnetic Field", color = Color(0xFF152946), fontSize = 16.sp, fontWeight = FontWeight.Bold, lineHeight = 18.sp)
                            Spacer(modifier = Modifier.height(5.dp))
                            Text("Abnormal signal", color = Color(0xFF939DAA), fontSize = 12.sp, fontWeight = FontWeight.Normal, lineHeight = 14.sp)
                        }
                        Image(
                            painter = painterResource(R.mipmap.img_magnetic_field),
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
                        )
                    }
                }

                item {
                    Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f).background(color = White, shape = RoundedCornerShape(10.dp)).clickable{
                        CameraScannerActivity.launch(context)
                    }) {
                        Column(modifier = Modifier.padding(top = 20.dp, start = 15.dp)) {
                            Text("Infrared Camera", color = Color(0xFF152946), fontSize = 16.sp, fontWeight = FontWeight.Bold, lineHeight = 18.sp)
                            Spacer(modifier = Modifier.height(5.dp))
                            Text("Flashing light", color = Color(0xFF939DAA), fontSize = 12.sp, fontWeight = FontWeight.Normal, lineHeight = 14.sp)
                        }
                        Image(
                            painter = painterResource(R.mipmap.img_infrared_camera),
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
                        )
                    }
                }

                item {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f)
                        .background(color = White, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 15.dp)
                    ) {
                        Text("How it works", color = Color(0xFF152946), fontSize = 12.sp, fontWeight = FontWeight.Normal, modifier = Modifier.align(Alignment.CenterStart))
                        Image(
                            painter = painterResource(R.drawable.svg_question),
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }

                item {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f)
                        .background(color = White, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 15.dp)
                        .clickable {
                            HistoryRecordActivity.launch(context)
                        }
                    ) {
                        Text("Detection history", color = Color(0xFF152946), fontSize = 12.sp, fontWeight = FontWeight.Normal, modifier = Modifier.align(Alignment.CenterStart))
                        Image(
                            painter = painterResource(R.drawable.svg_detection_history),
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(painter = painterResource(R.mipmap.img_home_sub_bg), contentScale = ContentScale.FillWidth, contentDescription = null, modifier = Modifier.fillMaxWidth())
                        Column(modifier = Modifier.padding(start = 15.dp, top = 15.dp)) {
                            Text("41%OFF，Get VIP", color = Color(0xFF152946), fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 18.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("2.33 per week only", color = Color(0xFF939DAA), fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 14.sp)
                            Spacer(modifier = Modifier.height(15.dp))
                            Row(modifier = Modifier
                                .height(26.dp)
                                .background(color = Color(0xFF5672FF), shape = RoundedCornerShape(200.dp))
                                .padding(start = 12.dp, end = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("START", color = White, fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Image(painter = painterResource(R.drawable.svg_next_with_bg), contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }
}