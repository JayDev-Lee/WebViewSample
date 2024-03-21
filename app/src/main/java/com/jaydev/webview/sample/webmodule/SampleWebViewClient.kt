package com.jaydev.webview.sample.webmodule

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import com.google.accompanist.web.AccompanistWebViewClient
import com.jaydev.webview.sample.ui.Constants.APP_LINK_SCHEME
import java.net.URISyntaxException

class SampleWebViewClient : AccompanistWebViewClient() {
    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        return handleShouldOverrideUrlLoading(view.context, url)
    }

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        return handleShouldOverrideUrlLoading(view.context, request.url.toString())
    }

    @Suppress("ReturnCount")
    private fun handleShouldOverrideUrlLoading(context: Context, url: String): Boolean {
        if (url.isEmpty()) return false

        when {
            url.startsWith(URL_TEL_PREFIX) -> try {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                context.startActivity(intent)
                return true
            } catch (e: URISyntaxException) {
                Log.d(TAG, "parse tel: error", e)
            }
            url.startsWith(URL_MAIL_PREFIX) -> {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                context.startActivity(intent)
                return true
            }
            url.startsWith(APP_LINK_SCHEME) -> {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                return true
            }
            url.contains(KAKAO_AUTH_DOMAIN) -> {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                return true
            }
            url.startsWith(URL_INTENT_PREFIX) -> {
                try {
                    val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)

                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity((intent))
                        return true
                    }

                    val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                    if (fallbackUrl != null) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl)))
                        return true
                    }

                    val existPackage = context.packageManager.getLaunchIntentForPackage(intent.getPackage()!!)
                    return if (existPackage != null) {
                        context.startActivity(intent)
                        true
                    } else {
                        val marketIntent = Intent(Intent.ACTION_VIEW)
                        marketIntent.data = Uri.parse("market://details?id=" + intent.getPackage()!!)
                        context.startActivity(marketIntent)
                        true
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "intent:// error", e)
                }
            }
        }

        return false
    }

    companion object {
        const val TAG = "SampleWebViewClient"
        private const val URL_TEL_PREFIX = "tel:"
        private const val URL_INTENT_PREFIX = "intent://"
        private const val URL_MAIL_PREFIX = "mailto:"
        private const val KAKAO_AUTH_DOMAIN = "kauth.kakao.com"
    }
}
