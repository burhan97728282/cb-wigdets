package com.cb.widgets.feature.gallery

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cb.widgets.core.design.EmptyState
import com.cb.widgets.core.design.LocalSkeu
import com.cb.widgets.core.design.SkeuChip
import com.cb.widgets.core.design.SkeuDims
import com.cb.widgets.core.design.SkeuSearchField
import com.cb.widgets.core.design.WidgetPreviewCard
import com.cb.widgets.core.widgetkit.WidgetCatalog
import com.cb.widgets.core.widgetkit.WidgetCategory
import com.cb.widgets.core.widgetkit.defaultStyle
import com.cb.widgets.feature.library.LibraryViewModel

@Composable
fun GalleryScreen(
    initialCategory: String?,
    onOpenWidget: (String) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val palette = LocalSkeu.current
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val material by viewModel.defaultMaterial.collectAsStateWithLifecycle()

    var query by rememberSaveable { mutableStateOf("") }
    var selectedName by rememberSaveable(initialCategory) { mutableStateOf(initialCategory) }
    val selected = WidgetCategory.entries.firstOrNull { it.name == selectedName }
    val results = remember(query, selected) { WidgetCatalog.search(query, selected) }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 24.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            androidx.compose.foundation.layout.Column(Modifier.statusBarsPadding()) {
                Text(
                    "Gallery",
                    style = MaterialTheme.typography.headlineMedium,
                    color = palette.text,
                    modifier = Modifier.padding(start = 10.dp, top = 20.dp, bottom = 14.dp)
                )
                SkeuSearchField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "Search widgets",
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            LazyRow(contentPadding = PaddingValues(horizontal = 10.dp, vertical = 14.dp)) {
                item {
                    SkeuChip(
                        label = "All",
                        selected = selected == null,
                        onClick = { selectedName = null },
                        modifier = Modifier.padding(end = 10.dp)
                    )
                }
                items(WidgetCategory.entries.toList(), key = { it.name }) { category ->
                    SkeuChip(
                        label = category.label,
                        selected = selected == category,
                        onClick = { selectedName = category.name },
                        modifier = Modifier.padding(end = 10.dp)
                    )
                }
            }
        }
        if (results.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyState(
                    icon = Icons.Outlined.Search,
                    title = "No widgets found",
                    body = "Try a different word or choose another category."
                )
            }
        } else {
            items(results, key = { it.id }) { spec ->
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
