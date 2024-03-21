package com.jaydev.webview.sample.webmodule

import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.json.Json

@Composable
fun WebViewEventHandler(
    webView: SampleWebView?,
    webViewEvent: SharedFlow<SampleWebViewEvent>,
    onWindowActivated: () -> Unit = {},
    onPushPage: (url: String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onNavigateToNative: (SampleWebViewEvent.NavigateToNative<*>) -> Unit = {},
    onOpenNotificationModal: (title: String) -> Unit = {},
    onRequestRewardedAd: () -> Unit = {}
) {
    val context = LocalContext.current
    val urlHandler = LocalUriHandler.current

    LaunchedEffect(webView) {
        webViewEvent.collect { event ->
            when (event) {
                is SampleWebViewEvent.PushPage -> onPushPage(event.url)
                is SampleWebViewEvent.ClosePage -> onBackClick()
                is SampleWebViewEvent.NavigateToNative<*> -> {
                    onNavigateToNative(event)
                }

                is SampleWebViewEvent.UpdateAccessToken -> {
                    webView?.executeAction(SampleWebViewAction.UpdateAccessToken(event.accessToken))
                }

                is SampleWebViewEvent.OpenInBrowser -> {
                    urlHandler.openUri(event.url)
                }

                is SampleWebViewEvent.LogEvent -> {
                    val params = Json.Default.decodeFromString<Map<String, String>>(event.paramsJson)
                    Log.d("WebViewEventHandler", "Log.event: ${event.eventName}, $params")
                }

                is SampleWebViewEvent.Share -> {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, event.url)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }
            }
        }
    }
}

@Composable
fun WebViewActionHandler(
    webView: SampleWebView,
    webViewAction: SharedFlow<SampleWebViewAction>
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(webView, webViewAction, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            webViewAction.collect { action ->
                webView.executeAction(action)
            }
        }
    }
}
