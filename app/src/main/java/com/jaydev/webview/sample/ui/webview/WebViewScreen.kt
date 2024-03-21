package com.jaydev.webview.sample.ui.webview

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.jaydev.webview.sample.webmodule.SampleWebView
import com.jaydev.webview.sample.webmodule.SampleWebViewEvent
import com.jaydev.webview.sample.webmodule.WebViewActionHandler
import com.jaydev.webview.sample.webmodule.WebViewEventHandler

@Composable
fun WebViewScreen(
    url: String?,
    onBackClick: () -> Unit,
    onPushPage: (String) -> Unit,
    onNavigateToNative: (SampleWebViewEvent.NavigateToNative<*>) -> Unit = {},
    viewModel: SampleWebViewModel = hiltViewModel()
) {
    var webView by remember { mutableStateOf<SampleWebView?>(null) }
    var windowActivated by remember { mutableStateOf(false) }

    WebViewEventHandler(
        webView = webView,
        webViewEvent = viewModel.webViewEvent,
        onWindowActivated = {
            windowActivated = true
        },
        onPushPage = onPushPage,
        onBackClick = onBackClick,
        onNavigateToNative = onNavigateToNative,
    )

    if (webView != null && windowActivated) {
        WebViewActionHandler(webView!!, viewModel.webViewAction)
    }

    BackHandler {
        onBackClick()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
        if (url == null) return@Scaffold

        SampleWebView(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            url = url,
            viewModel = viewModel,
            onCreated = {
                webView = it
            }
        )
    }
}
