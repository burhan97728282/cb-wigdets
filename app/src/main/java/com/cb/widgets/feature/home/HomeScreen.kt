package com.cb.widgets.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cb.widgets.core.design.LocalSkeu
import com.cb.widgets.core.design.SectionHeader
import com.cb.widgets.core.design.SkeuDims
import com.cb.widgets.core.design.skeuRecessed
import com.cb.widgets.core.widgetkit.WidgetCatalog
import com.cb.widgets.core.widgetkit.WidgetCategory
import com.cb.widgets.feature.library.CategoryTile
import com.cb.widgets.feature.library.LibraryViewModel
import com.cb.widgets.feature.library.WidgetCardRow

@Composable
fun HomeScreen(
    onOpenWidget: (String) -> Unit,
    onOpenGallery: (String?) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val palette = LocalSkeu.current
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val recents by viewModel.recents.collectAsStateWithLifecycle()
    val material by viewModel.defaultMaterial.collectAsStateWithLifecycle()

    val featured = remember { WidgetCatalog.all.filter { it.featured } }
    val popular = remember { WidgetCatalog.all.filter { it.popular } }
    val recentSpecs = remember(recents) { recents.mapNotNull { WidgetCatalog.byId(it) } }
    val favoriteSpecs = remember(favorites) { WidgetCatalog.all.filter { it.id in favorites } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(
                Modifier
                    .statusBarsPadding()
                    .padding(horizontal = SkeuDims.ScreenPadding)
                    .padding(top = 20.dp, bottom = 16.dp)
            ) {
                Text("CB Widgets", style = MaterialTheme.typography.headlineLarge, color = palette.text)
                Text(
                    "Make your home screen yours.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = palette.textSecondary
                )
            }
        }
        item {
            val source = remember { MutableInteractionSource() }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SkeuDims.ScreenPadding)
                    .heightIn(min = 56.dp)
                    .skeuRecessed(radius = 22.dp, depth = 5.dp)
                    .clickable(interactionSource = source, indication = null, role = Role.Button) { onOpenGallery(null) }
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Search, contentDescription = null, tint = palette.textSecondary)
                Spacer(Modifier.width(12.dp))
                Text("Search widgets", style = MaterialTheme.typography.bodyLarge, color = palette.textSecondary)
            }
        }
        item { SectionHeader("Featured") }
        item {
            WidgetCardRow(featured, favorites, material, 280.dp, onOpenWidget, viewModel::toggleFavorite)
        }
        item { SectionHeader("Categories") }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 14.dp)) {
                items(WidgetCategory.entries.toList(), key = { it.name }) { category ->
                    CategoryTile(category = category, onClick = { onOpenGallery(category.name) })
                }
            }
        }
        if (recentSpecs.isNotEmpty()) {
            item { SectionHeader("Recently used") }
            item {
                WidgetCardRow(recentSpecs, favorites, material, 220.dp, onOpenWidget, viewModel::toggleFavorite)
            }
        }
        if (favoriteSpecs.isNotEmpty()) {
            item { SectionHeader("Favorites") }
            item {
                WidgetCardRow(favoriteSpecs, favorites, material, 220.dp, onOpenWidget, viewModel::toggleFavorite)
            }
        }
        item { SectionHeader("Popular") }
        item {
            WidgetCardRow(popular, favorites, material, 220.dp, onOpenWidget, viewModel::toggleFavorite)
        }
    }
}
