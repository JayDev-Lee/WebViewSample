package com.jaydev.webview.sample.webmodule

import android.net.Uri
import com.jaydev.webview.sample.ui.sample.SAMPLE_DEEP_LINK_URI
import com.jaydev.webview.sample.ui.sample.sampleNavigationRoute

class SampleWebViewNavigator : SampleWebViewEvent.NavigateToNative<Nothing>(sampleNavigationRoute) {
    override fun makeUrl(): Uri {
        return Uri.parse(SAMPLE_DEEP_LINK_URI)
    }
}
