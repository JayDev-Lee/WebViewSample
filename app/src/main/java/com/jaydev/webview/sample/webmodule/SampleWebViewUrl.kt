package com.jaydev.webview.sample.webmodule

import android.net.Uri
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

object SampleWebViewUrl {
    private const val base = "https://base.webview.url.here"

    const val home = "$base/home"

    const val event = "$base/event"
    const val eventDetail = "$event/detail"

    const val notice = "$base/notice"

    fun makeUrl(url: String, modelClass: Any? = null): String {
        return Uri.parse(url).buildUpon()
            .apply {
                modelClass?.let {
                    appendInstancePropertyAsQuery(it)
                }
            }
            .build()
            .toString()
    }

    private fun Uri.Builder.appendInstancePropertyAsQuery(modelClass: Any) {
        modelClass::class.declaredMemberProperties.forEach {
            val value = (it as KProperty1<Any, *>).get(modelClass) ?: return@forEach
            if (value::class.isData) {
                appendInstancePropertyAsQuery(value)
            } else {
                appendQueryParameter(it.name, value.toString())
            }
        }
    }
}
