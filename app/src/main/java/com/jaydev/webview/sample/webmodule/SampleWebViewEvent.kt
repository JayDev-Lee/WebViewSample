package com.jaydev.webview.sample.webmodule

import android.net.Uri

sealed interface SampleWebViewEvent {
    data class PushPage(val url: String) : SampleWebViewEvent
    data object ClosePage : SampleWebViewEvent
    data class UpdateAccessToken(val accessToken: String) : SampleWebViewEvent
    data class OpenInBrowser(val url: String) : SampleWebViewEvent
    sealed class NavigateToNative<T>(val route: String) : SampleWebViewEvent {
        var data: T? = null

        open fun makeUrl(): Uri {
            return Uri.parse(route).buildUpon().apply {
                data?.let {
                    appendQueryParameter("data", data.toString())
                }
            }.build()
        }
    }

    data class Share(val url: String) : SampleWebViewEvent
    data class LogEvent(val eventName: String, val paramsJson: String) : SampleWebViewEvent
}

sealed interface SampleWebViewAction {
    val action: String

    data class UpdateAccessToken(val accessToken: String) : SampleWebViewAction {
        override val action = "updateAccessToken('$accessToken');"
    }
}
