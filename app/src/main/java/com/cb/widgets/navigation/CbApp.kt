package com.cb.widgets.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cb.widgets.core.design.LocalSkeu
import com.cb.widgets.core.design.NavBarItem
import com.cb.widgets.core.design.SkeuNavBar
import com.cb.widgets.feature.detail.DetailScreen
import com.cb.widgets.feature.gallery.GalleryScreen
import com.cb.widgets.feature.home.HomeScreen
import com.cb.widgets.feature.mywidgets.MyWidgetsScreen
import com.cb.widgets.feature.onboarding.OnboardingScreen
import com.cb.widgets.feature.settings.SettingsScreen

@Composable
fun CbApp(onboardingDone: Boolean, onFinishOnboarding: () -> Unit) {
    val palette = LocalSkeu.current
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(palette.bgTop, palette.bgBottom)))
    ) {
        if (onboardingDone) MainContent() else OnboardingScreen(onFinish = onFinishOnboarding)
    }
}

@Composable
private fun MainContent() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val showBar = destination?.hierarchy?.any { d ->
        Destination.bottomBar.any { it.route == d.route }
    } == true

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBar) {
                SkeuNavBar(
                    items = Destination.bottomBar.map { dest ->
                        NavBarItem(
                            label = dest.label,
                            icon = dest.icon,
                            selected = destination?.hierarchy?.any { it.route == dest.route } == true,
                            onClick = {
                                navController.navigate(dest.navRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.Home.route) {
                HomeScreen(
                    onOpenWidget = { navController.navigate(Destination.detail(it)) },
                    onOpenGallery = { category ->
                        navController.navigate(Destination.gallery(category)) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(
                route = Destination.Gallery.route,
                arguments = listOf(
                    navArgument("category") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { entry ->
                GalleryScreen(
                    initialCategory = entry.arguments?.getString("category"),
                    onOpenWidget = { navController.navigate(Destination.detail(it)) }
                )
            }
            composable(Destination.MyWidgets.route) {
                MyWidgetsScreen(
                    onOpenWidget = { navController.navigate(Destination.detail(it)) },
                    onBrowse = {
                        navController.navigate(Destination.Gallery.navRoute) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Destination.Settings.route) { SettingsScreen() }
            composable(
                route = Destination.DETAIL_ROUTE,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) {
                DetailScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
