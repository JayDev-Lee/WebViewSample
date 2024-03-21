package com.jaydev.webview.sample.webmodule

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.WebViewNavigator
import com.google.accompanist.web.rememberWebViewNavigator
import com.jaydev.webview.sample.ui.webview.SampleWebViewModel

@Suppress("SetJavaScriptEnabled")
class SampleWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleRes: Int = 0
) : WebView(context, attrs, defStyleRes) {
    init {
        settings.javaScriptEnabled = true
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.domStorageEnabled = true
        settings.setSupportMultipleWindows(false)
        settings.javaScriptCanOpenWindowsAutomatically = false
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.textZoom = 100
        settings.setSupportZoom(false)
        settings.builtInZoomControls = false

        isFocusable = true
        isFocusableInTouchMode = true

        overScrollMode = OVER_SCROLL_NEVER
    }

    override fun loadUrl(url: String) {
        super.loadUrl(url)
        Log.i(TAG, "loadUrl: url=$url")
    }

    override fun loadUrl(url: String, additionalHttpHeaders: MutableMap<String, String>) {
        super.loadUrl(url, additionalHttpHeaders)
        Log.i(TAG, "loadUrl: url=$url, headers=$additionalHttpHeaders}")
    }

    fun executeAction(action: SampleWebViewAction) {
        Log.i(TAG, "executeAction: action=${action.action}")
        evaluateJavascript(action.action, null)
    }

    companion object {
        const val TAG = "SampleWebView"
    }
}

@Suppress("ClickableViewAccessibility")
@Composable
fun SampleWebView(
    modifier: Modifier = Modifier,
    url: String,
    viewModel: SampleWebViewModel,
    captureBackPresses: Boolean = true,
    navigator: WebViewNavigator = rememberWebViewNavigator(),
    onCreated: ((SampleWebView) -> Unit) = {},
    onDragging: (Boolean) -> Unit = {},
    onDispose: (SampleWebView) -> Unit = {},
    client: SampleWebViewClient = remember { SampleWebViewClient() },
    chromeClient: AccompanistWebChromeClient = remember { AccompanistWebChromeClient() },
) {
    val context = LocalContext.current
    val state = remember {
        viewModel.makeWebViewState(url = url)
    }

    WebView(
        state = state,
        modifier = modifier,
        captureBackPresses = captureBackPresses,
        navigator = navigator,
        onCreated = {
            it.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN,
                    MotionEvent.ACTION_MOVE -> {
                        onDragging(false)
                        false
                    }

                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> {
                        onDragging(true)
                        false
                    }

                    else -> {
                        false
                    }
                }
            }
            it.addJavascriptInterface(viewModel, "Sample")
            onCreated(it as SampleWebView)
        },
        onDispose = {
            onDispose(it as SampleWebView)
        },
        client = client,
        chromeClient = chromeClient,
        factory = {
            SampleWebView(it)
        }
    )
}
