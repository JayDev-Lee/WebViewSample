package com.jaydev.webview.sample.ui.webview

import android.content.res.loader.ResourcesProvider
import android.os.Build
import android.util.Log
import android.webkit.JavascriptInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.web.WebContent
import com.google.accompanist.web.WebViewState
import com.jaydev.webview.sample.domain.usecase.GetTokenUseCase
import com.jaydev.webview.sample.domain.usecase.RefreshTokenUseCase
import com.jaydev.webview.sample.webmodule.SampleWebViewAction
import com.jaydev.webview.sample.webmodule.SampleWebViewEvent
import com.jaydev.webview.sample.webmodule.SampleWebViewNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

@Suppress("TooManyFunctions", "LongParameterList")
@HiltViewModel
open class SampleWebViewModel @Inject constructor(
    val getTokenUseCase: GetTokenUseCase,
    val refreshTokenUseCase: RefreshTokenUseCase,
) : ViewModel() {


    private lateinit var webViewState: WebViewState

    private val _webViewEvent = MutableSharedFlow<SampleWebViewEvent>()
    val webViewEvent = _webViewEvent.asSharedFlow()

    @Suppress("PropertyName", "VariableNaming")
    protected val _webViewAction: MutableSharedFlow<SampleWebViewAction> = MutableSharedFlow()
    val webViewAction = _webViewAction.asSharedFlow()

    private fun makeHeaders(): MutableMap<String, String> {
        val headers = mutableMapOf(
            "X-APP-VERSION" to "1.0.0",
            "X-USERAGENT" to
                    "/${Build.VERSION.SDK_INT}" +
                    "/${Build.MANUFACTURER}-${Build.MODEL.trim().replace(" ", "")}",
        ).apply {
            getAccessToken()?.let { put("X-ACCESS-TOKEN", it) }
        }
        return headers
    }

    fun makeWebViewState(url: String): WebViewState {
        val headers = makeHeaders()
        val webContent = WebContent.Url(url = url, additionalHttpHeaders = headers)
        return WebViewState(webContent).also { webViewState = it }
    }

    private fun getAccessToken() = runBlocking { getTokenUseCase().first().getOrNull()?.accessToken }

    @JavascriptInterface
    fun pushPage(url: String) {
        Log.i(TAG, "pushPage: $url")
        viewModelScope.launch {
            _webViewEvent.emit(SampleWebViewEvent.PushPage(url))
        }
    }

    @JavascriptInterface
    fun pushPage(url: String, isClosableByPullingDown: Boolean? = null) = pushPage(url)

    @JavascriptInterface
    fun closePage() {
        Log.i(TAG, "closePage")
        viewModelScope.launch {
            _webViewEvent.emit(SampleWebViewEvent.ClosePage)
        }
    }

    @JavascriptInterface
    fun navigateToNative(payload: String) {
        Log.i(TAG, "navigateToNative: $payload")
        viewModelScope.launch {
            val jsonElement = Json.Default.parseToJsonElement(payload)
            val name = jsonElement.jsonObject["name"]?.jsonPrimitive?.content
            val data = jsonElement.jsonObject["data"]
            when (name) {
                "Sample" -> _webViewEvent.emit(SampleWebViewNavigator())
                else -> {
                    // no-op
                }
            }
        }
    }

    @JavascriptInterface
    fun updateAccessToken() {
        Log.i(TAG, "updateAccessToken")
        viewModelScope.launch {
            refreshTokenUseCase()
                .onEach {
                    it.onSuccess {
                        _webViewAction.emit(SampleWebViewAction.UpdateAccessToken(it.accessToken))
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    @JavascriptInterface
    fun updateAccessToken(callback: String? = null) {
        updateAccessToken()
    }

    @JavascriptInterface
    fun openInBrowser(url: String) {
        Log.i(TAG, "openInBrowser: $url")
        viewModelScope.launch {
            _webViewEvent.emit(SampleWebViewEvent.OpenInBrowser(url))
        }
    }

    @JavascriptInterface
    fun share(url: String) {
        Log.i(TAG, "share: $url")
        viewModelScope.launch {
            _webViewEvent.emit(SampleWebViewEvent.Share(url))
        }
    }

    @JavascriptInterface
    fun logEvent(eventName: String, paramsJson: String) {
        Log.i(TAG, "logEvent: $eventName, $paramsJson")
        viewModelScope.launch {
            _webViewEvent.emit(SampleWebViewEvent.LogEvent(eventName, paramsJson))
        }
    }

    companion object {
        private val TAG = SampleWebViewModel::class.java.simpleName
    }
}
