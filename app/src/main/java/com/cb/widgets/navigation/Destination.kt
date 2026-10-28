package com.cb.widgets.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    /** Concrete route used when navigating (differs from [route] when it has arguments). */
    val navRoute: String = route
) {
    data object Home : Destination("home", "Home", Icons.Outlined.Home)
    data object Gallery : Destination(
        route = "gallery?category={category}",
        label = "Gallery",
        icon = Icons.Outlined.GridView,
        navRoute = "gallery"
    )
    data object MyWidgets : Destination("my_widgets", "My widgets", Icons.Outlined.Widgets)
    data object Settings : Destination("settings", "Settings", Icons.Outlined.Settings)

    companion object {
        /**
         * Must be a getter, not a stored value. The subclasses above initialise their sealed parent, which
         * would run a stored list while Home/Gallery/... are still null and put null entries in it.
         */
        val bottomBar: List<Destination>
            get() = listOf(Home, Gallery, MyWidgets, Settings)

        const val DETAIL_ROUTE = "detail/{id}"
        fun detail(id: String) = "detail/$id"
        fun gallery(category: String?) = if (category == null) "gallery" else "gallery?category=$category"
    }
}
