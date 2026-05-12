package com.findhiddencamera.spycameralocator.ui.web

import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.theme.White

private const val TAG = "WebViewPage"

@Composable
fun WebViewPage(
    title: String,
    url: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val webView = remember(context) {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    // 保持网页内部跳转仍然在当前WebView中展示
                    return false
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    Log.e(TAG, "WebView加载失败：${error?.description}")
                }
            }
        }
    }

    BackHandler {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            onBack()
        }
    }

    DisposableEffect(webView) {
        onDispose {
            // 页面退出时释放WebView，避免持有Activity引用
            webView.stopLoading()
            webView.destroy()
        }
    }

    LaunchedEffect(title, url) {
        if (url.isBlank()) {
            Log.w(TAG, "网页链接未配置：$title")
        }
    }

    Column(modifier = modifier.fillMaxSize().background(color = White).statusBarsPadding()) {
        Box(modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 9.dp, end = 15.dp)) {
            Row(modifier = Modifier.align(Alignment.CenterStart).clickable {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    onBack()
                }
            }, verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(R.drawable.svg_back), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(title, color = Color(0xFF152946), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
        }

        Spacer(modifier = Modifier.height(12.dp))
        if (url.isBlank()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text("Content is not configured", color = Color(0xFF939DAA), fontSize = 14.sp, modifier = Modifier.align(Alignment.Center))
            }
        } else {
            AndroidView(
                factory = {
                    webView
                },
                update = {
                    if (it.url != url) {
                        // 只在链接变化时加载，避免重组导致页面重复刷新
                        it.loadUrl(url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
