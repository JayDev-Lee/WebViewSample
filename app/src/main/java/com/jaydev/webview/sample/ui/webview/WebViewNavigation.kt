package com.jaydev.webview.sample.ui.webview

import android.net.Uri
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.jaydev.webview.sample.webmodule.SampleWebViewEvent

const val WEBVIEW_ARG_URL = "WEBVIEW_ARG_URL"
const val webViewNavigationRoute = "webview_route?url={$WEBVIEW_ARG_URL}"
private const val DEEP_LINK_URI_PATTERN = "://webview?url={$WEBVIEW_ARG_URL}"

fun NavController.navigateToWebView(url: String, navOptions: NavOptions? = null) {
    this.navigate(webViewNavigationRoute.replace("{$WEBVIEW_ARG_URL}", Uri.encode(url)), navOptions)
}

fun NavGraphBuilder.webViewScreen(
    onBackClick: () -> Unit = {},
    onPushPage: (String) -> Unit = {},
    onNavigateToNative: (SampleWebViewEvent.NavigateToNative<*>) -> Unit = {},
) {
    composable(
        route = webViewNavigationRoute,
        arguments = listOf(
            navArgument(WEBVIEW_ARG_URL) {
                type = NavType.StringType
                defaultValue = ""
            }
        ),
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        )
    ) {
        val url = it.arguments?.getString(WEBVIEW_ARG_URL)
        WebViewScreen(
            url = url,
            onBackClick = onBackClick,
            onPushPage = onPushPage,
            onNavigateToNative = onNavigateToNative
        )
    }
}
