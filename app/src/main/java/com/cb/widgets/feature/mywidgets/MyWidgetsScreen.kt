package com.cb.widgets.feature.mywidgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cb.widgets.core.design.EmptyState
import com.cb.widgets.core.design.LocalSkeu
import com.cb.widgets.core.design.SkeuSegmentedControl
import com.cb.widgets.core.design.WidgetPreviewCard
import com.cb.widgets.core.widgetkit.WidgetCatalog
import com.cb.widgets.core.widgetkit.defaultStyle
import com.cb.widgets.feature.library.LibraryViewModel

@Composable
fun MyWidgetsScreen(
    onOpenWidget: (String) -> Unit,
    onBrowse: () -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val palette = LocalSkeu.current
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val recents by viewModel.recents.collectAsStateWithLifecycle()
    val material by viewModel.defaultMaterial.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableIntStateOf(0) }

    val favoriteSpecs = remember(favorites) { WidgetCatalog.all.filter { it.id in favorites } }
    val recentSpecs = remember(recents) { recents.mapNotNull { WidgetCatalog.byId(it) } }
    val shown = when (tab) {
        1 -> favoriteSpecs
        2 -> recentSpecs
        else -> emptyList()
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 24.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(Modifier.statusBarsPadding().padding(horizontal = 10.dp)) {
                Text(
                    "My widgets",
                    style = MaterialTheme.typography.headlineMedium,
                    color = palette.text,
                    modifier = Modifier.padding(top = 20.dp, bottom = 16.dp)
                )
                SkeuSegmentedControl(
                    options = listOf("Placed", "Favorites", "Recent"),
                    selectedIndex = tab,
                    onSelect = { tab = it }
                )
                androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = 12.dp))
            }
        }
        if (shown.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                when (tab) {
                    1 -> EmptyState(
                        icon = Icons.Outlined.FavoriteBorder,
                        title = "No favorites yet",
                        body = "Tap the heart on any widget to keep it here.",
                        actionLabel = "Browse widgets",
                        onAction = onBrowse
                    )
                    2 -> EmptyState(
                        icon = Icons.Outlined.History,
                        title = "Nothing here yet",
                        body = "Widgets you open will appear here.",
                        actionLabel = "Browse widgets",
                        onAction = onBrowse
                    )
                    else -> EmptyState(
                        icon = Icons.Outlined.Widgets,
                        title = "No widgets on your home screen",
                        body = "Widgets you place on your home screen will be listed here.",
                        actionLabel = "Browse widgets",
                        onAction = onBrowse
                    )
                }
            }
        } else {
            items(shown, key = { it.id }) { spec ->
                WidgetPreviewCard(
                    spec = spec,
                    style = spec.defaultStyle(material),
                    isFavorite = spec.id in favorites,
                    onClick = { onOpenWidget(spec.id) },
                    onToggleFavorite = { viewModel.toggleFavorite(spec.id) }
                )
            }
        }
    }
}
