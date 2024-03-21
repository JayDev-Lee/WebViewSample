package com.jaydev.webview.sample.ui.sample

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.jaydev.webview.sample.ui.Constants

const val sampleNavigationRoute = "sample_route"
const val SAMPLE_DEEP_LINK_URI = "${Constants.APP_LINK_SCHEME}://profile"

fun NavController.navigateToSample(navOptions: NavOptions? = null) {
    val uri = SAMPLE_DEEP_LINK_URI.toUri()
        .buildUpon()
        .build()
    this.navigate(uri, navOptions)
}

fun NavGraphBuilder.sampleScreen(
    onNextClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    composable(
        route = sampleNavigationRoute,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        arguments = listOf(
            navArgument(SAMPLE_DEEP_LINK_URI) {
                type = NavType.BoolType
                defaultValue = false
            }
        ),
        deepLinks = listOf(
            navDeepLink { uriPattern = SAMPLE_DEEP_LINK_URI }
        )
    ) {
        Scaffold {
            Box(
                modifier = Modifier
                .fillMaxSize()
                .padding(it)
            ) {
                // some ui here
            }
        }
    }
}
